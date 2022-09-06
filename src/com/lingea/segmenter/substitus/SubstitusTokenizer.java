package com.lingea.segmenter.substitus;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.ListUtils;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.tuple.Pair;
import com.lingea.segmenter.Segmenter;
import com.lingea.segmenter.data.token.SequenceToken;
import com.lingea.segmenter.data.token.SingleAtomToken;
import com.lingea.segmenter.data.token.Token;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * @param <A> Type of the basic, atomic element (typically: byte or character).
 * 
 * @author  Josef Plch
 * @since   2018-10-31
 * @version 2021-01-06
 */
public class SubstitusTokenizer <A> implements Segmenter <A> {
    private final UnaryOperator <A> atomPreprocessor;
    // Because of effectivity, we also store lists the n-gram atoms. This way,
    // the algorithm runs about three times faster.
    private final PairList <FunctionalList <SequenceToken <A>>, FunctionalList <A>> knownNgrams;
    private final static boolean ENABLE_OVERLAPPING = true;
    
    // Input: List of token n-grams.
    public SubstitusTokenizer (UnaryOperator <A> atomPreprocessor, FunctionalList <FunctionalList <List <A>>> knownTokenSequences) {
        this.atomPreprocessor = atomPreprocessor;
        this.knownNgrams =
            knownTokenSequences.mapToPair (tokenNgram -> {
                FunctionalList <FunctionalList <A>> preprocessedNgram =
                    tokenNgram.map (token ->
                        FunctionalList.from (token).map (atomPreprocessor)
                    );
                return Pair.of (
                    preprocessedNgram.map (SequenceToken :: fromAtoms),
                    FunctionalList.concat (preprocessedNgram)
                );
            });
    }
    
    // TODO: Use atom preprocessor.
    @Override
    public FunctionalList <List <A>> segmentize (List <A> compound) {
        FunctionalList <A> preprocessed = FunctionalList.from (compound).map (atomPreprocessor);
        
        // Initialize: Each atom forms an individual token.
        FunctionalList <Token <A>> merged = preprocessed.map (SingleAtomToken :: from);
        
        for (Pair <FunctionalList <SequenceToken <A>>, FunctionalList <A>> ngramData : knownNgrams) {
            FunctionalList <A> ngramAtoms = ngramData.get2 ();
            // As applying is costy, we first check the presence.
            if (preprocessed.hasInfix (ngramAtoms)) {
                FunctionalList <SequenceToken <A>> ngram = ngramData.get1 ();
                // New solution, with overlapping: FM 67.050 % (A 89, M 60).
                if (ENABLE_OVERLAPPING) {
                    FunctionalList <Token <A>> old = merged;
                    merged = useOverlappingSegmentation (merged, ngram);
                    // System.err.println ("Merged:  " + old + " + " + ngram + " = " + merged);
                }
                // Old solution, without overlapping: FM 72.188 % (A 90, M 66).
                else {
                    merged =
                        merged.replaceSequence (
                            ngramAtoms.map (SingleAtomToken :: from),
                            ngram
                        );
                }
            }
        }
        
        FunctionalList <List <A>> tokens = merged.map (Token :: getAtoms);
        
        // Restore the original atoms (without preprocessing).
        FunctionalList <List <A>> result = new FunctionalList <> ();
        int lastIndex = 0;
        for (List <A> token : tokens) {
            int newIndex = lastIndex + token.size ();
            result.add (compound.subList (lastIndex, newIndex));
            lastIndex = newIndex;
        }
        
        return result;
    }
    
    /**
     * Apply the segmentation on compatible positions, overlapping is possible.
     * 
     * Example:
     * [abc, d, e, ab, c, d, e] + [abc, cd] = [abc, de, ab, c, d, e]
     * 
     * @param <A> Type of the basic, atomic element (typically: byte or character).
     * @param originalSegmentation
     * @param newSegmentation
     * @return 
     */
    public static <A> FunctionalList <Token <A>> useOverlappingSegmentation (
        FunctionalList <Token <A>> originalSegmentation,
        FunctionalList <SequenceToken <A>> newSegmentation
    ) {
        FunctionalList <Token <A>> result = new FunctionalList <> ();
        
        // Test the compatibility at all positions.
        int ti = 0;
        while (ti < originalSegmentation.size ()) {
            // Test compatibility at this position, checking token by token.
            boolean isApplicableHere = true;
            boolean anyTokenIsNotMerged = false;
            int checkedOriginalTokens = 0;
            for (SequenceToken <A> newToken : newSegmentation) {
                FunctionalList <Token <A>> uncheckedTokens = originalSegmentation.drop (ti + checkedOriginalTokens);
                // System.err.println ("#" + ti + "/" + checkedOriginalTokens + ": uncheckedTokens = " + uncheckedTokens);
                
                // It means there was only prefix of the new segmentation and we
                // have already reached the end of the sequence.
                // Bug fix 2019-01-03: Condition "checkedOriginalTokens >= uncheckedTokens.size ()"
                // replaced with "uncheckedTokens.isEmpty ()"
                if (uncheckedTokens.isEmpty ()) {
                    isApplicableHere = false;
                    // Examples:
                    // "in" causes that [ng, in] cannot be used in [engineer, i, n, g]
                    // "form" causes that [n, form] cannot be used in [inform, atio, n]
                    // "e" causes that [nd, e] cannot be used in [u, n, d, er, s, t, a, nd]
                    // System.err.println ("#" + ti + ": Bad token A: \"" + newToken + "\" causes that " + newSegmentation + " cannot be used in " + originalSegmentation);
                    break;
                }
                
                // Already merged (the same sequence token).
                if (Objects.equals (newToken, uncheckedTokens.head ())) {
                    checkedOriginalTokens += 1;
                }
                // Not merged yet (a sequence of single atom tokens).
                else if (
                    uncheckedTokens.size () >= newToken.size ()
                    && ListUtils.zipWith (Objects :: equals, newToken.map (SingleAtomToken :: from), uncheckedTokens).all (x -> x)
                ) {
                    anyTokenIsNotMerged = true;
                    checkedOriginalTokens += newToken.size ();
                }
                else {
                    // System.err.println ("#" + ti + ": Bad token B: \"" + newToken + "\" causes that " + newSegmentation + " cannot be used in " + originalSegmentation);
                    isApplicableHere = false;
                    break;
                }
                
                // System.err.println ("checkedTokens: " + checkedTokens);
            }
            // System.err.println (suffix + " -> " + isApplicableHere);
            
            if (isApplicableHere) {
                if (anyTokenIsNotMerged) {
                    // System.err.println ("#" + ti + ": merging " + newSegmentation + " in " + originalSegmentation);
                }
                result.addAll (newSegmentation);
                // System.err.println ("#" + ti + ": result = " + result);
                ti += checkedOriginalTokens;
            }
            else {
                // System.err.println ("#" + i + ": XXXXXXX " + originalSegmentation + " + " + newSegmentation);
                result.add (originalSegmentation.get (ti));
                ti++;
            }
        }
        
        return result;
    }
}
