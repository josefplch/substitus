package com.lingea.segmenter.data.token;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @param <A> Type of the basic, atomic element (typically: byte or character).
 * 
 * @author  Josef Plch
 * @since   2018-10-10
 * @version 2018-11-21
 */
public class SingleAtomToken <A> implements Token <A> {
    private final A atom;
    
    private SingleAtomToken (A atom) {
        this.atom = atom;
    }
    
    public static <A> SingleAtomToken <A> from (A atom) {
        return new SingleAtomToken <> (atom);
    }
    
    @Override
    public boolean equals (Object object) {
        boolean result;
        if (object == null || ! (object instanceof SingleAtomToken)) {
            result = false;
        }
        else {
            final SingleAtomToken <?> other = (SingleAtomToken <?>) object;
            result = Objects.equals (this.atom, other.atom);
        }
        return result;
    }
    
    @Override
    public List <A> getAtoms () {
        return Arrays.asList (atom);
    }
    
    @Override
    public int hashCode () {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode (this.atom);
        return hash;
    }
    
    @Override
    public int size () {
        return 1;
    }
    
    @Override
    public String toString () {
        return (Objects.toString (atom));
    }
}
