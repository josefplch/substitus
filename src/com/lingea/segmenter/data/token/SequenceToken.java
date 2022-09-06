package com.lingea.segmenter.data.token;

import com.github.josefplch.utils.data.list.FunctionalList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @param <A> Type of the basic, atomic element (typically: byte or character).
 * 
 * @author  Josef Plch
 * @since   2018-10-10
 * @version 2019-03-11
 */
public class SequenceToken <A> extends FunctionalList <A> implements Token <A> {
    public SequenceToken () {
    }
    
    protected SequenceToken (Collection <? extends A> collection) {
        super (collection);
    }
    
    public static <A> SequenceToken <A> fromAtoms (Collection <A> atoms) {
        return new SequenceToken <> (atoms);
    }
    
    @SafeVarargs
    public static <A> SequenceToken <A> fromAtoms (A ... atoms) {
        return new SequenceToken <> (Arrays.asList (atoms));
    }
    
    @SafeVarargs
    public static <A> SequenceToken <A> merge (Token <A> ... tokens) {
        SequenceToken <A> result = new SequenceToken <> ();
        for (Token <A> token : tokens) {
            result.addAll (token.getAtoms ());
        }
        return result;
    }
    
    @Override
    public FunctionalList <A> getAtoms () {
        return this;
    }

    @Override
    public String toString () {
        return super.toString ("[]");
    }
}
