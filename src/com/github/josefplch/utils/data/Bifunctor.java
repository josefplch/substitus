package com.github.josefplch.utils.data;

import java.util.function.Function;

/**
 * Haskell-inspired interface.
 * 
 * Note: For technical reasons, the mapping functions return generic bifunctor
 * instead of specific one. Therefore, type of the resuls should be overriden.
 * (This is why there is no default implementation.)
 * 
 * Read:
 * https://dzone.com/articles/functor-and-monad-examples-in-plain-java
 * 
 * @param <A> Type of the first element.
 * @param <B> Type of the second element.
 * 
 * @author  Josef Plch
 * @since   2018-04-25
 * @version 2018-05-13
 */
public interface Bifunctor <A, B> {
    /**
     * Map over both arguments at the same time. <br>
     * 
     * bimap (f, g) == (map1 f) . (map2 g)
     * 
     * @param <C> New type of the first element.
     * @param <D> New type of the second element.
     * @param f Function to be applied to the first element.
     * @param g Function to be applied to the second element.
     * @return Transformed bifunctor.
     */
    public <C, D> Bifunctor <C, D> bimap (Function <A, C> f, Function <B, D> g);
    
    /**
     * Map covariantly over the first argument.
     * 
     * @param <C> New type of the first element.
     * @param f Function to be applied to the first element.
     * @return Transformed bifunctor.
     */
    public <C> Bifunctor <C, B> map1 (Function <A, C> f);
    
    /**
     * Map covariantly over the second argument.
     * 
     * @param <C> New type of the second element.
     * @param f Function to be applied to the second element.
     * @return Transformed bifunctor.
     */
    public <C> Bifunctor <A, C> map2 (Function <B, C> f);
}
