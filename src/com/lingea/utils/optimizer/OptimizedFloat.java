package com.lingea.utils.optimizer;

/**
 * @author  Josef Plch
 * @since   2018-05-23
 * @version 2018-05-26
 */
public class OptimizedFloat extends AbstractOptimizedValue <Float> {
    private OptimizedFloat (Float value) {
        super (value);
    }
    
    public static OptimizedFloat from (float value) {
        return new OptimizedFloat (value);
    }
    
    @Override
    public OptimizedFloat multiply (float factor) {
        return OptimizedFloat.from (factor * value ());
    }
}
