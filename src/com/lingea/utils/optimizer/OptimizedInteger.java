package com.lingea.utils.optimizer;

import com.github.josefplch.utils.data.math.NumberUtils;

/**
 * @author  Josef Plch
 * @since   2018-05-23
 * @version 2018-05-26
 */
public class OptimizedInteger extends AbstractOptimizedValue <Integer> {
    private OptimizedInteger (Integer value) {
        super (value);
    }
    
    public static OptimizedInteger from (int value) {
        return new OptimizedInteger (value);
    }
    
    @Override
    public OptimizedInteger multiply (float factor) {
        int multipliedValue = Math.round (factor * value ());
        int whenEqual = value () + NumberUtils.signum (factor);
        int newValue = (multipliedValue == value ()) ? whenEqual : multipliedValue;
        return OptimizedInteger.from (newValue);
    }
}
