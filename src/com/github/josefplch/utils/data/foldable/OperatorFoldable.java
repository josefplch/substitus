package com.github.josefplch.utils.data.foldable;

import java.util.function.UnaryOperator;

/**
 * @param <A> Type of contained elements.
 * 
 * @author  Josef Plch
 * @since   2021-01-04
 * @version 2021-01-04
 */
public interface OperatorFoldable <A> extends Foldable <UnaryOperator <A>> {
    public default UnaryOperator <A> composeL () {
        return this.foldl ((f, g) -> (x -> g.apply (f.apply (x))), x -> x);
    }
    
    public default UnaryOperator <A> composeR () {
        return this.foldl ((f, g) -> (x -> f.apply (g.apply (x))), x -> x);
    }
}
