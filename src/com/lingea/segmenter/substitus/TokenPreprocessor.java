package com.lingea.segmenter.substitus;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.map.MapUtils;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.map.FrequencyHashMap;
import com.github.josefplch.utils.data.math.fraction.LongFraction;
import com.github.josefplch.utils.data.set.FunctionalSet;
import com.github.josefplch.utils.data.tuple.UniformPair;
import com.lingea.segmenter.data.token.ParametrizedNgram;
import com.lingea.segmenter.GlobalSettings;
import com.lingea.segmenter.data.ProbabilisticStringSegmentation;
import com.lingea.segmenter.data.token.NgramPropertiesA;
import com.lingea.segmenter.substitus.data.SegmentationFileReader;
import com.lingea.segmenter.utils.SegmentationUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author  Josef Plch
 * @since   2018-08-13
 * @version 2021-01-18
 */
public class TokenPreprocessor {
    private static final Function <ProbabilisticStringSegmentation, StringList> BINARIZE_50 =
        ProbabilisticStringSegmentation :: binarize50S;
    private static final Function <ProbabilisticStringSegmentation, StringList> BINARIZE_LONG_A =
        x -> x.binarizeIfS ((len, p) -> p >= 15 / Math.pow (len, 2.0));
    private static final Function <ProbabilisticStringSegmentation, StringList> BINARIZE_LONG_B =
        x -> x.binarizeIfS ((len, p) -> p >= 0.5 - 0.5 * Math.tanh (len / 3 - 2));
    private static final Function <ProbabilisticStringSegmentation, StringList> SURE_60 =
        segmentation -> segmentation.sureTokensS (0.40, 0.60);
    private static final Function <ProbabilisticStringSegmentation, StringList> SURE_75 =
        segmentation -> segmentation.sureTokensS (0.25, 0.75);
    private static final Function <ProbabilisticStringSegmentation, StringList> SURE_90 =
        segmentation -> segmentation.sureTokensS (0.10, 0.90);
    
    private final UniformPair <Boolean> addBoundaries;
    private final Function <ProbabilisticStringSegmentation, StringList> binarizer = BINARIZE_50;
    private final int maxExamples;
    private final int maxNgramLevel; // Recommended: 3
    // Very long strings are too difficult to decompose.
    // Solution: Ignore them, they are probably garbage anyway :-)
    private final int maxWordLength; // Recommended: at most 50
    private final int minNgramTf; // Recommended: 100
    private final double threshold;
    
    public TokenPreprocessor (int maxWordLength, int maxNgramLevel, int minNgramTf, UniformPair <Boolean> addBoundaries, double threshold, int maxExamples) {
        this.threshold = threshold;
        this.maxWordLength = maxWordLength;
        this.maxNgramLevel = maxNgramLevel;
        this.minNgramTf = minNgramTf;
        this.addBoundaries = addBoundaries;
        this.maxExamples = maxExamples;
    }
    
    private StringList binarize (ProbabilisticStringSegmentation segmentation) {
        StringList result = new StringList ();
        if (addBoundaries.get1 ()) {
            result.add (GlobalSettings.WORD_OPENING_TAG);
        }
        result.addAll (binarizer.apply (segmentation));
        if (addBoundaries.get2 ()) {
            result.add (GlobalSettings.WORD_CLOSING_TAG);
        }
        return result;
    }
    
    // Note: The result n-grams are not unique.
    private PairList <StringList, Long> expandToNgrams (PairList <StringList, Long> tokenizedWords) throws IOException {
        return (
            tokenizedWords
            // Get all token sublists up to the permitted length.
            .flatMap (
                tokenizedWordAndFrequency ->
                    tokenizedWordAndFrequency.get1 ().sublists (1, maxNgramLevel)
                    .map (ngram -> Pair.of (ngram, tokenizedWordAndFrequency.get2 ())),
                PairList :: new
            )
        );
    }
    
    public void findTokenNgrams (BufferedReader frequencyListReader, Optional <Integer> frequencyListLimit, Writer writer) throws IOException {
        FunctionalList <ParametrizedNgram> ngrams =
            findTokenNgrams (
                frequencyListReader,
                frequencyListLimit
            );
        System.err.println (
            "N-grams: " + ngrams.size ()
            + ", unigrams: " + ngrams.countIf (ngram -> ngram.getNgram ().size () == 1)
        );
        
        System.err.println ("Writing the output ...");
        for (ParametrizedNgram ngram : ngrams) {
            writer.write (ngram.serialize () + "\n");
        }
        writer.flush ();
    }
    
    public FunctionalList <ParametrizedNgram> findTokenNgrams (BufferedReader frequencyListReader, Optional <Integer> frequencyListLimit) throws IOException {
        System.err.println ("Loading segmentations ...");
        // We need to keep the probabilistic version of segmentations since we
        // will use it in n-gram examples.
        PairList <ProbabilisticStringSegmentation, Long> segmentedWords =
            SegmentationFileReader.readList (frequencyListReader, frequencyListLimit)
            .mapToPair (line -> Pair.of (line.get4 ().normalize (threshold), line.get1 ()));
        System.err.println ("Loaded words: " + segmentedWords.size ());
        
        // First, we compute only frequencies (NF), because we will use them
        // for filtering.
        // TODO: Wouldn't it be better to filter based on token frequencies?
        System.err.println ("Counting n-gram frequencies ...");
        Map <StringList, Long> ngramFrequencies =
            FrequencyHashMap.ofCounted (
                expandToNgrams (
                    segmentedWords.map1 (this :: binarize)
                )
            );
        
        System.err.println ("Computing n-gram properties ...");
        Map <StringList, NgramPropertiesA> ngramProperties = new HashMap <> ();
        int wordIndex = 0;
        long lastTime = 0;
        for (Pair <ProbabilisticStringSegmentation, Long> pair : segmentedWords) {
            StringList tokenizedWord = binarize (pair.get1 ());
            Long frequency = pair.get2 ();
            
            int thousand = 1_000;
            if (wordIndex % 10_000 == 0) {
                long time = System.currentTimeMillis ();
                String message =
                    "* Processing word"
                    + " " + (wordIndex / thousand) + "k"
                    + "/" + (segmentedWords.size () / thousand) + "k";
                
                if (wordIndex > 0) {
                    long seconds = (time - lastTime) / 1_000;
                    message += " (previous batch: " + seconds + " s)";
                }
                
                System.err.println (message);
                lastTime = time;
            }
            String word = tokenizedWord.join ();
            // Ignore long words.
            if (word.length () <= maxWordLength) {
                // System.err.println (word + " (length " + word.length () + "):");
                long decompositionStart = System.nanoTime ();
                // (n-gram level + 1) splits are needed to achieve precise
                // results. Caution: the computation complexity grows rapidly
                // with growing n-gram level.
                FunctionalList <StringList> decompositions =
                    SegmentationUtils.decompose (
                        word,
                        Optional.of (maxNgramLevel + 1)
                    );
                if (System.nanoTime () - decompositionStart > Math.pow (10, 9)) {
                    System.err.println ("WARNING: Slow decomposition: " + word);
                }
                // System.err.println (" " + decompositions.size () + " decompositions");
                
                FunctionalSet <StringList> chunkNgrams =
                    decompositions.flatMap (
                        decomposition -> decomposition.sublists (1, maxNgramLevel)
                    )
                    // Remove duplicite elements.
                    .toSet ();
                
                for (StringList ngram : chunkNgrams) {
                    // We are interested only in known n-grams (it saves lots of memory).
                    if (ngramFrequencies.getOrDefault (ngram, 0L) >= minNgramTf) {
                        boolean isInAnalysis = tokenizedWord.hasInfix (ngram);
                        PairList <ProbabilisticStringSegmentation, Long> example = PairList.ofPairs (pair);
                        PairList <ProbabilisticStringSegmentation, Long> nothing = PairList.ofPairs ();
                        
                        ngramProperties.merge (
                            ngram,
                            new NgramPropertiesA (
                                LongFraction.of (isInAnalysis ? frequency : 0L, frequency),
                                LongFraction.of (isInAnalysis ? 1L : 0L, 1L),
                                new UniformPair <> (isInAnalysis ? example : nothing, isInAnalysis ? nothing : example)
                            ),
                            (oldValue, newValue) -> NgramPropertiesA.combine (oldValue, newValue, maxExamples)
                        );
                    }
                }
            }
            wordIndex++;
        }
        
        System.err.println ("Generating n-gram list ...");
        FunctionalList <ParametrizedNgram> ngramsList =
            MapUtils.toList (ngramProperties)
            .map (pair ->
                new ParametrizedNgram (
                    pair.get1 (),
                    pair.get2 ().binarizeExamples ()
                )
            )
            .sortBy (
                Comparator.comparing (ngram -> ngram.getNgram ().join ())
            );
        
        return ngramsList;
    }
}
