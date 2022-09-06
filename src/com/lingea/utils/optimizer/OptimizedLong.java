package com.lingea.utils.optimizer;

import com.github.josefplch.utils.data.math.NumberUtils;

/**
 * @author  Josef Plch
 * @since   2018-05-23
 * @version 2018-05-26
 */
public class OptimizedLong extends AbstractOptimizedValue <Long> {
    private OptimizedLong (long value) {
        super (value);
    }
    
    public static OptimizedLong from (long value) {
        return new OptimizedLong (value);
    }
    
    @Override
    public OptimizedLong multiply (float factor) {
        long multipliedValue = Math.round (factor * value ());
        long whenEqual = value () + NumberUtils.signum (factor);
        long newValue = (multipliedValue == value ()) ? whenEqual : multipliedValue;
        return OptimizedLong.from (newValue);
    }
}
