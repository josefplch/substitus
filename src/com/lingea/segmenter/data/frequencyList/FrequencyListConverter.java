package com.lingea.segmenter.data.frequencyList;

import com.github.josefplch.utils.data.OptionalUtils;
import com.github.josefplch.utils.data.list.CharList;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.map.FunctionalMap;
import com.github.josefplch.utils.data.nlp.Tokenizer;
import com.github.josefplch.utils.data.set.StringSet;
import com.github.josefplch.utils.data.string.StringUtils;
import com.github.josefplch.utils.data.tuple.Pair;
import com.lingea.segmenter.Application;
import com.lingea.segmenter.GlobalSettings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author  Josef Plch
 * @since   2019-11-13
 * @version 2020-01-11
 */
public abstract class FrequencyListConverter {
    public static void convert (
        BufferedReader reader,
        Optional <Integer> frequencyListLimit,
        UnaryOperator <Character> caseConverter,
        boolean onlyWords,
        Optional <Double> minVariantShare,
        Optional <Integer> maxResultSize,
        Writer writer
    ) throws IOException {
        convertInternal (
            reader,
            line -> {
                FrequencyListEntry entry =
                    FrequencyListEntry.read (
                        CharList.fromString (line).mapToChar (caseConverter).toString ()
                    );
                FunctionalList <FrequencyListEntry> result = new FunctionalList <> ();
                if (onlyWords == false) {
                    result.add (entry);
                }
                else {
                    String trimmedWord = Tokenizer.trimChars (entry.getWord (), false, false, true);
                    if (Tokenizer.isWord (trimmedWord, true, true, false)) {
                        result.add (new FrequencyListEntry (entry.getFrequency (), trimmedWord, entry.getLemmata ()));
                    }
                }
                return result;
            },
            frequencyListLimit,
            minVariantShare,
            maxResultSize,
            writer
        );
    }
    
    private static void convertInternal (
        BufferedReader reader,
        Function <String, FunctionalList <FrequencyListEntry>> lineReader,
        Optional <Integer> maxWordLength,
        Optional <Double> minVariantShare,
        Optional <Integer> maxResultSize,
        Writer writer
    ) throws IOException {
        FunctionalMap <String, Pair <Long, StringSet>> entries = new FunctionalMap <> ();
        FunctionalMap <String, Long> deaccentedFrequency = new FunctionalMap <> ();
        int ignoredAccentVariants = 0;
        int ignoredLongWords = 0;
        int knownWords = 0;
        String line;
        int lineNumber = 0;
        while ((line = reader.readLine ()) != null) {
            if (lineNumber % (100_000) == 0) {
                System.err.println ("Reading line #" + (lineNumber / 1_000) + "k, current list size: " + entries.size ());
            }
            try {
                for (FrequencyListEntry entry : lineReader.apply (line)) {
                    String word = entry.getWord ();
                    // Ignore loo long words.
                    if (maxWordLength.map (maxLength -> word.length () <= maxLength).orElse (true)) {
                        // Ignore a new word if we reached the maximum number of results.
                        if (maxResultSize.map (maxSize -> entries.size () < maxSize || entries.containsKey (word)).orElse (true)) {
                            String deaccentedWord = StringUtils.removeDiacritics (word);
                            if (
                                OptionalUtils.lift2 (
                                    // The word must be either known or frequent enough in comparison to already seen variants.
                                    (alreadySeen, minShare) -> entry.getLemmata ().size () > 0 || entry.getFrequency () >= minShare * alreadySeen,
                                    deaccentedFrequency.getOptional (deaccentedWord),
                                    minVariantShare
                                )
                                .orElse (true)
                            ) {
                                deaccentedFrequency.merge (deaccentedWord, entry.getFrequency (), Long :: sum);
                                entries.merge (
                                    word,
                                    Pair.of (entry.getFrequency (), entry.getLemmata ()),
                                    (v1, v2) -> {
                                        StringSet union = new StringSet ();
                                        union.addAll (v1.get2 ());
                                        union.addAll (v2.get2 ());
                                        return Pair.of (v1.get1 () + v2.get1 (), union);
                                    }
                                );
                                if (entry.getLemmata ().size () > 0) {
                                    knownWords++;
                                }
                            }
                            else {
                                if (Application.DEBUG) {
                                    System.err.println ("Ignoring entry " + entry + " as accent variant.");
                                }
                                ignoredAccentVariants++;
                            }
                        }
                    }
                    else {
                        if (Application.DEBUG) {
                            System.err.println ("Ignoring entry " + entry + " because of length.");
                        }
                        ignoredLongWords++;
                    }
                }
            }
            catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException ("Illegal line #" + (lineNumber + 1), exception);
            }
            lineNumber++;
        }
        System.err.println (
            "Resulting entries: " + entries.size ()
            + " (" + knownWords + " known, " + (entries.size () - knownWords) + " unknown)"
        );
        System.err.println (
            "Ignored entries: " + ignoredLongWords + " too long"
            + ", " + ignoredAccentVariants + " accent variants"
        );
        
        System.err.println ("Ordering ...");
        FunctionalList <FrequencyListEntry> ordered =
            entries
            .toList ()
            .mapToComparable (entry -> new FrequencyListEntry (entry.get2 ().get1 (), entry.get1 (), entry.get2 ().get2 ()))
            .sortAsc ();
        
        System.err.println ("Writing ...");
        for (FrequencyListEntry entry : ordered) {
            writer.append (
                entry.getFrequency ()
                + "\t" + entry.getWord ()
                + (entry.getLemmata ().isEmpty () ? "" : ("\t" + entry.getLemmataAsc ().join (String.valueOf (GlobalSettings.LEMMA_DELIMITER))))
                + "\n"
            );
        }
    }
    
    public static void tokenizedToCaseSensitive (
        BufferedReader reader,
        Optional <Integer> maxWordLength,
        Optional <Integer> maxResultSize,
        Writer writer
    ) throws IOException {
        convertInternal (
            reader,
            line -> StringList.split ("[ \t]+", line).map (word -> new FrequencyListEntry (1L, word, new StringSet ())),
            maxResultSize,
            Optional.empty (),
            maxWordLength,
            writer
        );
    }
}
