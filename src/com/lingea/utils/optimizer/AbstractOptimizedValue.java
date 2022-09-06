package com.lingea.utils.optimizer;

import java.util.Objects;

/**
 * @author  Josef Plch
 * @since   2018-05-26
 * @version 2018-05-26
 */
public abstract class AbstractOptimizedValue <A> implements OptimizedValue <A> {
    private final A value;

    protected AbstractOptimizedValue (A value) {
        this.value = value;
    }
    
    @Override
    public int hashCode () {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode (this.value);
        return hash;
    }
    
    @Override
    public boolean equals (Object object) {
        boolean result;
        if (object == null || ! (object instanceof AbstractOptimizedValue)) {
            result = false;
        }
        else {
            AbstractOptimizedValue <?> other = (AbstractOptimizedValue <?>) object;
            result = Objects.equals (this.value, other.value);
        }
        return result;
    }
    
    @Override
    public String toString () {
        return value.toString ();
    }
    
    @Override
    public A value () {
        return value;
    }
}
