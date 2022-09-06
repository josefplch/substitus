package com.lingea.utils.optimizer;

/**
 * @author  Josef Plch
 * @since   2018-05-23
 * @version 2018-05-26
 */
public class OptimizedDouble extends AbstractOptimizedValue <Double> {
    private OptimizedDouble (Double value) {
        super (value);
    }
    
    public static OptimizedDouble from (double value) {
        return new OptimizedDouble (value);
    }

    @Override
    public OptimizedDouble multiply (float factor) {
        return OptimizedDouble.from (factor * value ());
    }
}
