package com.github.josefplch.utils.data.function;

import java.util.Objects;

/**
 * Constant bifunction is a function which always returns the same value.
 * 
 * @param <T1> Type of the 1st argument.
 * @param <T2> Type of the 2nd argument.
 * @param <R> Type of the result.
 * 
 * @author  Josef Plch
 * @since   2018-05-15
 * @version 2019-05-13
 */
public final class ConstantFunction2 <T1, T2, R> implements Function2 <T1, T2, R> {
    private final R value;
    
    private ConstantFunction2 (R value) {
        this.value = value;
    }
    
    public static <T1, T2, R> ConstantFunction2 <T1, T2, R> forValue (R value) {
        return new ConstantFunction2 <> (value);
    }
    
    @Override
    public R apply (T1 parameter1, T2 parameter2) {
        return value;
    }
    
    @Override
    public boolean equals (Object object) {
        boolean result;
        if (object == null || ! (object instanceof ConstantFunction2)) {
            result = false;
        }
        else {
            final ConstantFunction2 <?, ?, ?> other = (ConstantFunction2 <?, ?, ?>) object;
            result = Objects.equals (this.value, other.value);
        }
        return result;
    }

    @Override
    public int hashCode () {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode (this.value);
        return hash;
    }
}
