package com.lingea.segmenter.bpe;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.map.FrequencyHashMap;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.tuple.UniformPair;
import com.lingea.segmenter.Segmenter;
import com.lingea.segmenter.bpe.metric.AssociationMetric;
import com.lingea.segmenter.data.token.SequenceToken;
import com.lingea.segmenter.data.token.Token;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * See: Neural Machine Translation of Rare Words with Subword Units (2016).
 * 
 * @param <A> Type of the basic, atomic element (typically: byte or character).
 * 
 * @author  Josef Plch
 * @since   2018-05-03
 * @version 2020-12-07
 */
public class Bpe <A> implements Segmenter <A> {
    private final Map <UniformPair <Token <A>>, Long> commonFrequencies = new HashMap <> ();
    private final FunctionalList <UniformPair <Token <A>>> mergedPairs;
    private final AssociationMetric metric;
    private final int minFrequency;
    private int stepLimit = 999999;
    private final Map <Token <A>, Long> tokenFrequencies = new HashMap <> ();
    private final PairList <Word <A>, Long> wordsWithFrequencies = new PairList <> ();
    
    // Word must be an ArrayList to work efficiently.
    private static class Word <A> extends ArrayList <Token <A>> {
        public static <A> Word <A> uniatomicTokens (List <A> atoms) {
            Word <A> result = new Word <> ();
            for (A atom : atoms) {
                result.add (SequenceToken.fromAtoms (atom));
            }
            return result;
        }
    }
    
    protected Bpe (AssociationMetric metric, int minFrequency, FunctionalList <UniformPair <Token <A>>> mergedPairs) {
        this.mergedPairs = mergedPairs;
        this.metric = metric;
        this.minFrequency = minFrequency;
    }
    
    public static <A> Bpe <A> loadTrainData (
        AssociationMetric metric,
        int minFrequency,
        Stream <Pair <List <A>, Long>> trainData
    ) {
        Bpe <A> bpe = new Bpe <> (metric, minFrequency, new FunctionalList <> ());
        bpe.loadTrainData (trainData);
        return bpe;
    }
    
    public static <A> Bpe <A> loadTrainCorpus (
        AssociationMetric metric,
        int minFrequency,
        Stream <List <A>> trainData
    ) {
        return (
            loadTrainData (
                metric,
                minFrequency,
                FrequencyHashMap.forStream (trainData).toDescList ().stream ()
            )
        );
    }
    
    private void computeInitialFrequencies (Word <A> word, Long frequency) {
        Token <A> previousToken = null;
        for (Token <A> thisToken : word) {
            // Increment token frequency.
            tokenFrequencies.merge (thisToken, frequency, Long :: sum);
            
            // Increment token pair frequency.
            if (Objects.nonNull (previousToken)) {
                UniformPair <Token <A>> tokenPair = new UniformPair <> (previousToken, thisToken);
                commonFrequencies.merge (tokenPair, frequency, Long :: sum);
            }
            
            previousToken = thisToken;
        }
        wordsWithFrequencies.addPair (word, frequency);
    }
    
    public FunctionalList <UniformPair <Token <A>>> getMergedPairs () {
        return mergedPairs;
    }
    
    private double getScore (Token <A> x, Token <A> y) {
        Long fx = tokenFrequencies.get (x);
        Long fy = tokenFrequencies.get (y);
        Long fxy = commonFrequencies.getOrDefault (new UniformPair <> (x, y), 0L);
        return metric.apply (x.size (), y.size (), fx, fy, fxy);
    }
    
    public Map <Token <A>, Long> getTokenFrequencies () {
        return tokenFrequencies;
    }
    
    private UniformPair <Token <A>> findBestPair () {
        Double bestScore = Double.NEGATIVE_INFINITY;
        UniformPair <Token <A>> bestPair = null;
        for (Map.Entry <UniformPair <Token <A>>, Long> entry : commonFrequencies.entrySet ()) {
            if (entry.getValue () >= minFrequency) {
                UniformPair <Token <A>> tokenPair = entry.getKey ();
                Double score = getScore (tokenPair.get1 (), tokenPair.get2 ());
                if (score > bestScore) {
                    bestScore = score;
                    bestPair = tokenPair;
                }
            }
        }
        if (Objects.isNull (bestPair)) {
            throw new NoSuchElementException ("No pair found");
        }
        return bestPair;
    }
    
    public void loadTrainData (Stream <Pair <List <A>, Long>> trainData) {
        trainData.forEach (entry -> {
            this.computeInitialFrequencies (
                Word.uniatomicTokens (entry.get1 ()),
                entry.get2 ()
            );
        });
    }
    
    public void makeStep () {
        UniformPair <Token <A>> bestPair = findBestPair ();
        mergedPairs.add (bestPair);
        
        Token <A> bestToken1 = bestPair.get1 ();
        Token <A> bestToken2 = bestPair.get2 ();
        Token <A> mergedToken = SequenceToken.merge (bestToken1, bestToken2);
        
        for (Pair <Word <A>, Long> wordWithFrequency : wordsWithFrequencies) {
            Word <A> word = wordWithFrequency.get1 ();
            Long wordFrequency = wordWithFrequency.get2 ();
            
            // Iterate the elements in descending order, starting at the
            // penultimate element (to enable creating a pair).
            int i = word.size () - 2;
            while (i >= 0) {
                if (word.get (i).equals (bestToken1) && word.get (i + 1).equals (bestToken2)) {
                    word.remove (i);
                    word.remove (i);
                    word.add (i, mergedToken);
                    
                    // Update token frequencies.
                    tokenFrequencies.merge (bestToken1,  - wordFrequency, Long :: sum);
                    tokenFrequencies.merge (bestToken2,  - wordFrequency, Long :: sum);
                    tokenFrequencies.merge (mergedToken, + wordFrequency, Long :: sum);

                    // Update token pair frequencies.
                    if (i > 0) {
                        Token <A> leftToken = word.get (i - 1);
                        UniformPair <Token <A>> oldLeftPair = new UniformPair <> (leftToken, bestToken1);
                        UniformPair <Token <A>> newLeftPair = new UniformPair <> (leftToken, mergedToken);
                        commonFrequencies.merge (oldLeftPair, - wordFrequency, Long :: sum);
                        commonFrequencies.merge (newLeftPair, + wordFrequency, Long :: sum);
                    }
                    if (i < word.size () - 1) {
                        Token <A> rightToken = word.get (i + 1);
                        UniformPair <Token <A>> oldRightPair = new UniformPair <> (bestToken2, rightToken);
                        UniformPair <Token <A>> newRightPair = new UniformPair <> (mergedToken, rightToken);
                        commonFrequencies.merge (oldRightPair, - wordFrequency, Long :: sum);
                        commonFrequencies.merge (newRightPair, + wordFrequency, Long :: sum);
                    }
                }
                i--;
            }
        }
        commonFrequencies.remove (bestPair);
    }
    
    public int makeSteps (int n) {
        int dotSize = 100;
        int lineSize = 80;
        System.out.println ("Computing (each dot = " + dotSize + " steps):");
        int step;
        for (step = 1; step <= n; step++) {
            if (step % dotSize == 0) {
                System.out.print (".");
                if (step % (lineSize * dotSize) == 0) {
                    System.out.println ();
                }
            }
            try {
                makeStep ();
            }
            catch (NoSuchElementException exception) {
                break;
            }
        }
        System.out.println ();
        return (step - 1);
    }
    
    private static <A> void mergeTokenPair (UniformPair <Token <A>> pairToMerge, List <Token <A>> list) {
        Token <A> token1 = pairToMerge.get1 ();
        Token <A> token2 = pairToMerge.get2 ();
        Token <A> mergedToken = SequenceToken.merge (token1, token2);
        // System.out.println ("Merging " + token1 + " + " + token2);
        
        // Iterate the elements in descending order, starting at the
        // penultimate element (to enable creating a pair).
        int i = list.size () - 2;
        while (i >= 0) {
            if (list.get (i).equals (token1) && list.get (i + 1).equals (token2)) {
                list.remove (i);
                list.remove (i);
                list.add (i, mergedToken);
            }
            i--;
        }
    }
    
    @Override
    public List <List <A>> segmentize (List <A> atoms) {
        FunctionalList <Token <A>> sequence = new FunctionalList <> ();
        for (A atom : atoms) {
            sequence.add (SequenceToken.fromAtoms (atom));
        }
        for (UniformPair <Token <A>> pairToMerge : mergedPairs.take (stepLimit)) {
            mergeTokenPair (pairToMerge, sequence);
        }
        return (new ArrayList <> (sequence.map (Token :: getAtoms)));
    }
    
    public void setStepLimit (int stepLimit) {
        this.stepLimit = stepLimit;
    }
}
