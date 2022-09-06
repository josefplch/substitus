package com.lingea.segmenter.substitus;

import com.github.josefplch.utils.data.function.NamedFunction;
import com.github.josefplch.utils.data.list.CharList;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.map.MapUtils;
import com.github.josefplch.utils.data.tuple.Tuple3;
import com.github.josefplch.utils.data.tuple.UniformPair;
import com.github.josefplch.utils.system.TextFileUtils;
import com.lingea.segmenter.GlobalSettings;
import com.lingea.segmenter.data.token.NgramPropertiesB;
import com.lingea.segmenter.data.token.ParametrizedNgram;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author  Josef Plch
 * @since   2019-05-11
 * @version 2021-01-14
 */
public class TokenTuner {
    private static final boolean DISCARD_ADJACENT_UNIGRAMS = true; // Recommended: true
    private static final boolean DISCARD_UNIGRAM_SEQUENCES = false; // Recommended: false
    // Group n-grams with the same characters and keep only one of them.
    // Problem in Czech: (k, ov) is much more frequent than kov.
    private static final boolean UNIQUE_BY_CHARACTERS = true; // Recommended: true
    private final int maxNgramLevel; // More is better: 1 << 2 < 3.
    private final int maxNgramLength; // 10. More is better.
    private final int maxTokenLength; // 10. More is better.
    private final int minNgramIwf; // Recommended: 5. Less is better: 3 > 5 >> 10.
    private final int minNgramTf; // 100.
    private final int minTokenIwf; // 1.
    private final int minTokenTf; // 1.
    
    public TokenTuner (
        int maxNgramLevel,
        int maxNgramLength,
        int minNgramTf,
        int minNgramIwf,
        int maxTokenLength,
        int minTokenTf,
        int minTokenIwf
    ) {
        this.maxNgramLevel = maxNgramLevel;
        this.maxNgramLength = maxNgramLength;
        this.minNgramTf = minNgramTf;
        this.minNgramIwf = minNgramIwf;
        this.maxTokenLength = maxTokenLength;
        this.minTokenTf = minTokenTf;
        this.minTokenIwf = minTokenIwf;
    }
    
    private FunctionalList <ParametrizedNgram> filterAndOrder (
        FunctionalList <ParametrizedNgram> ngrams,
        Function <ParametrizedNgram, UniformPair <Number>> scoreComputer
    ) {
        FunctionalList <ParametrizedNgram> result = filterUnigrams (filterByProperties (ngrams));
        
        Comparator <ParametrizedNgram> comparator =
            Comparator.comparing (
                tuple -> {
                    String text = tuple.get1 ().join ();
                    UniformPair <Double> scores = scoreComputer.apply (tuple).map (Number :: doubleValue);
                    Double key1 = scores.get1 ();
                    Double key2 = scores.get2 ();
                    String key3 = text;
                    // Hack for retrograde ordering.
                    if (key1 == 0 && key2 < 0) {
                        key3 = CharList.fromString (text).reverse ().toString ();
                    }
                    return Tuple3.of (0 - key1, 0 - key2, key3);
                },
                Tuple3.LexicographicalComparator.natural ()
            );
        
        if (UNIQUE_BY_CHARACTERS) {
            result =
                result
                .groupByKey (ngram -> ngram.getNgram ().join ())
                .mapOptional (group -> group.minimumBy (comparator));
        }
        
        return (result.sortBy (comparator));
    }

    private FunctionalList <ParametrizedNgram> filterByProperties (
        FunctionalList <ParametrizedNgram> ngrams
    ) {
        Map <String, NgramPropertiesB> unigrams = new HashMap <> ();
        for (ParametrizedNgram ngram : ngrams) {
            if (ngram.getNgram ().size () == 1) {
                unigrams.put (ngram.getNgram ().head (), ngram.getProperties ());
            }
        }
        return (
            ngrams.filter (ngram ->
                ngram.getNgram ().size () <= maxNgramLevel
                && ngram.getTfCount () >= minNgramTf
                && ngram.getIwfCount () >= minNgramIwf
                && ngram.getNgram ().join ().length () <= maxNgramLength
                && ngram.getNgram ().all (token -> token.length () <= maxTokenLength)
                && ngram.getNgram ().all (token -> unigrams.get (token).getTfCount () >= minTokenTf)
                && ngram.getNgram ().all (token -> unigrams.get (token).getIwfCount () >= minTokenIwf)
            )
        );
    }
    
    private static FunctionalList <ParametrizedNgram> filterUnigrams (
        FunctionalList <ParametrizedNgram> ngrams
    ) {
        return (
            ngrams
            // Min length filter: At least one token must have length > 1.
            .filter (ngram ->
                (DISCARD_UNIGRAM_SEQUENCES == false)
                || ngram.get1 ().any (token -> token.length () > 1)
            )
            // Adjacent unigrams filter.
            .filter (ngram ->
                (DISCARD_ADJACENT_UNIGRAMS == false)
                || ngram.get1 ().bigrams ().any (tokenBigram ->
                    tokenBigram.both (token ->
                        token.length () == 1
                    )
                ) == false
            )
        );
    }
    
    public static FunctionalList <NamedFunction <ParametrizedNgram, UniformPair <Number>>> orderingFunctions () {
        // TODO 2020.
        return (
            FunctionalList.of (
                /*
                // Math.random (), // Random shuffle: F-score =~ 0.60
                NamedFunction.create (
                    "alpha", ngram -> new UniformPair <> (0, 1)
                ),
                NamedFunction.create (
                    "alpha_retrograde", ngram -> new UniformPair <> (0, -1)
                ),
                NamedFunction.create (
                    "frequency",
                    ngram -> new UniformPair <> (ngram.getTfCount (), 0)
                ),
                NamedFunction.create (
                    "productivity",
                    ngram -> new UniformPair <> (ngram.getIwfCount (), 0)
                ),
                */
                NamedFunction.create (
                    "length_and_frequency",
                    ngram -> new UniformPair <> (ngram.getLength (), ngram.getTfCount ())
                ),
                NamedFunction.create (
                    "length_and_productivity",
                    ngram -> new UniformPair <> (ngram.getLength (), ngram.getIwfCount ())
                ),
                NamedFunction.create (
                    "length_and_fixed_tf_ratio",
                    ngram -> new UniformPair <> (ngram.getLength (), ngram.getFixedTfRatio ())
                ),
                NamedFunction.create (
                    "length_and_fixed_iwf_ratio",
                    ngram -> new UniformPair <> (ngram.getLength (), ngram.getFixedIwfRatio ())
                ),
                /*
                NamedFunction.create (
                    "exp_length_mult_frequency",
                    ngram -> new UniformPair <> (Math.pow (2.0, ngram.getLength ()) * ngram.getTfCount (), 0)
                ),
                NamedFunction.create (
                    "exp_length_mult_productivity",
                    ngram -> new UniformPair <> (Math.pow (2.0, ngram.getLength ()) * ngram.getIwfCount (), 0)
                ),
                */
                NamedFunction.create (
                    "exp_length_mult_fixed_tf_ratio",
                    ngram -> new UniformPair <> (Math.pow (2.0, ngram.getLength ()) * ngram.getFixedTfRatio (), 0)
                ),
                // CS+DE+EN ***
                NamedFunction.create (
                    "exp_length_mult_fixed_iwf_ratio",
                    ngram -> new UniformPair <> (Math.pow (2.0, ngram.getLength ()) * ngram.getFixedIwfRatio (), 0)
                )
            )
        );
    }
    
    public static FunctionalList <ParametrizedNgram> readFile (BufferedReader reader) throws IOException {
        return (
            TextFileUtils.readLineList (reader)
            .map (ParametrizedNgram :: parse)
        );
    }
    
    public static FunctionalList <ParametrizedNgram> readFile (String inputFilePath) throws IOException {
        return (
            TextFileUtils.readLineList (inputFilePath)
            .map (ParametrizedNgram :: parse)
        );
    }
    
    public void tuneFile (BufferedReader inputReader, String outputDirectory, String outputFilesPrefix) throws IOException {
        System.err.println ("Reading the input file ...");
        FunctionalList <ParametrizedNgram> ngrams = readFile (inputReader);
        tuneNgrams (ngrams, outputDirectory, outputFilesPrefix);
    }
    
    public void tuneFiles (StringList inputFilePaths, String outputDirectory, String outputFilesPrefix) throws IOException {
        Map <StringList, NgramPropertiesB> ngramMap = new HashMap <> ();
        for (String inputFilePath : inputFilePaths) {
            Files.lines (Paths.get (inputFilePath)).forEach (line -> {
                ParametrizedNgram ngram = ParametrizedNgram.parse (line);
                ngramMap.merge (
                    ngram.getNgram (),
                    ngram.getProperties (),
                    NgramPropertiesB :: combine
                );
            });
        }
        FunctionalList <ParametrizedNgram> ngrams =
            MapUtils.toList (ngramMap).map (p ->
                new ParametrizedNgram (p.get1 (), p.get2 ())
            );
        tuneNgrams (ngrams, outputDirectory, outputFilesPrefix);
    }
    
    public void tuneNgrams (FunctionalList <ParametrizedNgram> ngrams, String outputDirectory, String outputFilesPrefix) throws IOException {
        System.err.println (
            "Original n-grams: " + ngrams.size ()
            + ", original unigrams: " + ngrams.countIf (ngram -> ngram.getNgram ().size () == 1)
        );
        
        for (NamedFunction <ParametrizedNgram, UniformPair <Number>> function : orderingFunctions ()) {
            System.err.println ("* Tuning tokens with function \"" + function.getName () + "\" ...");
            FunctionalList <ParametrizedNgram> tunedNgrams = filterAndOrder (ngrams, function);
            int numberOfUnigrams = tunedNgrams.countIf (ngram -> ngram.getNgram ().size () == 1);
            System.err.println (
                "  Tuned n-grams: " + tunedNgrams.size ()
                + ", tuned unigrams: " + numberOfUnigrams
            );
            
            TextFileUtils.writeLines (
                outputDirectory + "/" + outputFilesPrefix
                    /*
                    + "_filtered"
                        + "_n" + maxNgramLevel
                        + "_t" + maxTokenSize
                        + "_len" + maxNgramSize
                        + "_f" + minNgramTf
                        + "_p" + minNgramIwf
                        + "_" + (REMOVE_DUPLICITE ? "unq" : "dpl")
                    */
                    + "_ordered_" + function.getName ()
                    + "_u" + numberOfUnigrams
                    + "." + GlobalSettings.EXTENSION_TNL,
                tunedNgrams.mapToString (ParametrizedNgram :: serialize)
            );
        }
    }
}
