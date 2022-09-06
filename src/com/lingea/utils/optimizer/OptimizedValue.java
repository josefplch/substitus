package com.lingea.utils.optimizer;

/**
 * @param <A>
 * 
 * @author  Josef Plch
 * @since   2018-05-23
 * @version 2018-05-26
 */
public interface OptimizedValue <A> {
    public OptimizedValue <A> multiply (float factor);
    
    public A value ();
}
