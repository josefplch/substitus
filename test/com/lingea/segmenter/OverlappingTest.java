package com.lingea.segmenter;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.lingea.segmenter.data.token.SequenceToken;
import com.lingea.segmenter.data.token.SingleAtomToken;
import com.lingea.segmenter.data.token.Token;
import com.lingea.segmenter.substitus.SubstitusTokenizer;

/**
 * @author  Josef Plch
 * @since   2019-02-28
 * @version 2019-11-29
 */
public abstract class OverlappingTest {
    public static void main (String [] args) {
        FunctionalList <Token <Character>> original =
            FunctionalList.of (
                SequenceToken.fromAtoms ('a', 'b', 'c'),
                SingleAtomToken.from ('d'),
                SingleAtomToken.from ('e'),
                SequenceToken.fromAtoms ('a', 'b'),
                SingleAtomToken.from ('c'),
                SingleAtomToken.from ('d'),
                SingleAtomToken.from ('e')
            );
        
        FunctionalList <SequenceToken <Character>> newSegmentation =
            FunctionalList.of (
                SequenceToken.fromAtoms ('a', 'b', 'c'),
                SequenceToken.fromAtoms ('d', 'e')
            );
        
        FunctionalList <Token <Character>> combined =
            SubstitusTokenizer.useOverlappingSegmentation (
                original,
                newSegmentation
            );
        
        System.out.println ("Original: " + original + " + " + newSegmentation);
        System.out.println ("Combined: " + combined);
    }
}
