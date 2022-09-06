package com.lingea.segmenter.substitus;

import com.github.josefplch.utils.data.Either;
import com.github.josefplch.utils.data.list.CharList;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.number.DoubleList;
import com.github.josefplch.utils.data.math.Mean;
import com.github.josefplch.utils.data.set.StringSet;
import com.github.josefplch.utils.data.string.AlignmentUtils;
import com.github.josefplch.utils.data.string.DoubleFormatter;
import com.github.josefplch.utils.data.string.StringUtils;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.tuple.Tuple3;
import com.github.josefplch.utils.data.tuple.Tuple4;
import com.github.josefplch.utils.system.TextFileUtils;
import com.lingea.segmenter.GlobalSettings;
import com.lingea.segmenter.data.ProbabilisticStringSegmentation;
import com.lingea.segmenter.data.SimpleStringSegmentation;
import com.lingea.segmenter.substitus.data.SegmentationFileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author  Josef Plch
 * @since   2020-12-02
 * @version 2021-02-10
 */
public abstract class TokenUsageAnalyser {
    private static final boolean CLEAN_UNKNOWN = true;
    public static String FILTER_ALL = "all";
    public static String FILTER_KNOWN = "known";
    public static String FILTER_UNKNOWN = "unknown";
    
    // Note: @ must be separated, like this: ov_ec_@
    public static void analyse (
        BufferedReader reader,
        Optional <Integer> frequencyListLimit,
        int maxWordLength,
        double normalizationMean,
        FunctionalList <SimpleStringSegmentation> tokenNgrams,
        String wordFilter,
        boolean lemmatize,
        Optional <Integer> maxResultSize,
        int verbosity,
        Writer writer
    ) throws IOException {
        // Problematic for special characters.
        Predicate <String> regex =
            Pattern.compile (
                "("
                + tokenNgrams.mapToString (segmentation ->
                    Pattern.quote (segmentation.elements ().filter (c -> c != GlobalSettings.WORD_BOUNDARY).toString ())
                )
                .join ("|")
                + ")"
            )
            .asPredicate ();
        Set <String> unaccentedWords = new HashSet <> ();
        Stream <Tuple4 <Long, StringSet, ProbabilisticStringSegmentation, Double>> correspondingEntries =
            TextFileUtils.readLineStream (reader, frequencyListLimit)
            // Just for efficiency (however, it disables the cleaning.)
            .filter (regex)
            .map (SegmentationFileReader :: readLine)
            .filter (entry -> entry.get2 ().length () <= maxWordLength)
            .filter (entry -> {
                boolean ignoredVariant;
                if (CLEAN_UNKNOWN == false) {
                    ignoredVariant = false;
                }
                else {
                    String unaccentedWord = StringUtils.removeDiacritics (entry.get2 ());
                    // Of course, we must check the presence before we add the word.
                    boolean alreadySeen = unaccentedWords.contains (unaccentedWord);
                    // Remember only words with known lemma.
                    if (entry.get3 ().size () > 0) {
                        unaccentedWords.add (unaccentedWord);
                    }
                    ignoredVariant = entry.get3 ().isEmpty () && alreadySeen;
                }
                return (
                    ignoredVariant == false
                    && (
                        wordFilter.equals (FILTER_ALL)
                        || wordFilter.equals (FILTER_KNOWN) && ! entry.get3 ().isEmpty ()
                        || wordFilter.equals (FILTER_UNKNOWN) && entry.get3 ().isEmpty ()
                    )
                );
            })
            .map (entry -> entry.map4 (s -> s.normalize (normalizationMean)))
            .map (entry -> {
                CharList entryChars = new CharList ();
                entryChars.add (GlobalSettings.WORD_BOUNDARY);
                entryChars.addAll (entry.get4 ().elements ());
                entryChars.add (GlobalSettings.WORD_BOUNDARY);

                // For each segmentation, try all occurrences of all n-grams.
                return tokenNgrams.flatMap (ngram ->
                    entryChars.subIndices (ngram.elements ()).map (index -> {
                        FunctionalList <Optional <Boolean>> ngramGlues = ngram.glues ();

                        // Add the boundary segmentability (100 %).
                        DoubleList segmentabilities = new DoubleList ();
                        segmentabilities.add (1.0);
                        segmentabilities.add (1.0);
                        segmentabilities.addAll (entry.get4 ().glues ());
                        segmentabilities.add (1.0);
                        segmentabilities.add (1.0);

                        // Word: @ u n s i n k a b l e @
                        // Glue:  0 1 2 3 4 5 6 7 8 9 10
                        // Ideal:     1 0 0 0 1 0   0 1
                        // Tokens:     s=i=n=k a=b?l=e
                        DoubleList errors = new DoubleList ();
                        // The initial boundary shall have segmentability of 1.
                        errors.add (1.0 - segmentabilities.get (index));
                        for (int i = 0; i < ngramGlues.size (); i++) {
                            Optional <Boolean> segmentable = ngramGlues.get (i);
                            if (segmentable.isPresent ()) {
                                double segmentability = segmentabilities.get (index + 1 + i);
                                errors.add (segmentable.get () ? 1.0 - segmentability : segmentability);
                            }
                        }
                        // The final boundary shall have segmentability of 1.
                        errors.add (1.0 - segmentabilities.get (index + ngram.size ()));

                        // Arithmetic mean is not suitable because for long words,
                        // reaching inner segmentability of 0 is quite simple.
                        // double frequencyScore = 2.0 / Math.PI * Math.atan (entry.get1 () / 100.0);
                        return Tuple4.of (entry.get1 (), entry.get3 (), entry.get4 (), Mean.cubicMean (errors));
                    })
                )
                // Choose the n-gram with the minimum error.
                .minimumBy (Comparator.comparing (Tuple4 :: get4));
            })
            .filter (Optional :: isPresent)
            .map (Optional :: get);
        
        // The entries may be grouped by either unknown word (left) or
        // known word/lemma (right). The value does not contain lemma
        // which means that for known-words, we lost this information.
        // However, there is no simple solution; probably, the only
        // clean solution would be:
        // Map <Either <Word, Lemma>, Either <Tuple, List of Tuples>>
        // ... which is too complicated.
        Map <Either <String, String>, FunctionalList <Tuple3 <Long, ProbabilisticStringSegmentation, Double>>> groupedEntries = new HashMap <> ();
        correspondingEntries.forEach (entry -> {
            Tuple3 <Long, ProbabilisticStringSegmentation, Double> newEntry =
                Tuple3.of (entry.get1 (), entry.get3 (), entry.get4 ());

            // Lemmatize if required -- and possible.
            if (lemmatize && entry.get2 ().size () > 0) {
                entry.get2 ().forEach (lemma -> {
                    groupedEntries.merge (
                        Either.right (lemma),
                        FunctionalList.of (newEntry),
                        (x, y) -> {x.addAll (y); return x;}
                    );
                });
            }
            else {
                String word = entry.get3 ().elements ().toString ();
                groupedEntries.put (
                    entry.get2 ().isEmpty () ? Either.left  (word) : Either.right (word),
                    FunctionalList.of (newEntry)
                );
            }
        });

        Stream <Tuple4 <Long, Optional <String>, ProbabilisticStringSegmentation, Double>> optionalLemmaStream =
            FunctionalList.from (groupedEntries.entrySet ())
            .mapOptional (entry ->
                entry.getValue ()
                // Find the best matching word form. In fact, this is only
                // relevant to lemmata as the other lists are singletons.
                .minimumBy (Comparator.comparing (Tuple3 :: get3))
                .map (bestMatchingForm -> Tuple4.of (
                    // Sum the frequencies of individual forms.
                    entry.getValue ().mapToLong (Tuple3 :: get1).sum (),
                    entry.getKey ().safeRight ().map (known ->
                        known + (lemmatize == false ? "" : (" (" + entry.getValue ().size () + "×)"))
                    ),
                    // Optional.of (entry.getKey () + " (" + entry.getValue ().size () + "×)"),
                    bestMatchingForm.get2 (),
                    // Instead of the best (lowest) error, use the average error.
                    entry.getValue ().mapToDouble (Tuple3 :: get3).arithmeticMean ()
                ))
            )
            .stream ();
        
        if (maxResultSize.isPresent ()) {
            optionalLemmaStream =
                optionalLemmaStream
                // Before limiting, we must order the entries by frequency
                // (they are not ordered any more because of the map part).
                .sorted (Comparator.comparing (entry -> 0 - entry.get1 ()))
                .limit (maxResultSize.get ());
        }

        Collator collatorCs = Collator.getInstance (new Locale (GlobalSettings.LOCALE));
        TextFileUtils.writeLines (
            writer,
            optionalLemmaStream
            // Convert the error to string score so that we order only by the visible digits.
            .map (tuple -> tuple.map4 (error -> DoubleFormatter.POINT_6.format (1.0 - error)))
            // Order by score and word.
            .sorted (
                Comparator.comparing (
                    entry -> Pair.of (entry.get4 (), entry.get3 ().elements ().toString ()),
                    Pair.LexicographicalComparator.basedOn (Comparator.reverseOrder (), collatorCs)
                )
            )
            // Show: score + word/lemma + frequency + [ternary segmentation] + [decimal segmentation]
            .map (tuple ->
                tuple.get4 ()
                + "  " + (
                    lemmatize && ! tuple.get2 ().isEmpty () // mode.equals (MODE_LEMMATA)
                    ? tuple.get2 ().map (lemma -> " " + AlignmentUtils.toLeft (lemma, maxWordLength + 6)).orElseThrow ()
                    : (
                        (tuple.get2 ().isEmpty () ? "*" : " ")
                        + AlignmentUtils.toLeft (tuple.get3 ().elements ().toString (), maxWordLength + 6)
                    )
                )
                + "  " + AlignmentUtils.toRight (tuple.get1 ().toString (), 10)
                + "  " + (verbosity >= 1 ? AlignmentUtils.toLeft (tuple.get3 ().toStringTernary (), maxWordLength + 3) : "")
                // + "  " + (verbosity >= 1 ? AlignmentUtils.toLeft (tuple.get3 ().binarize50S ().unwords (), maxWordLength + 3) : "")
                + "  " + (verbosity >= 2 ? tuple.get3 ().toStringDecimal6 () : "")
            )
            .map (String :: trim)
        );
    }
}
