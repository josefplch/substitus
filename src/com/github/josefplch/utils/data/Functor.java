package com.github.josefplch.utils.data;

import java.util.function.Function;

/**
 * Haskell-inspired interface. It could be @FunctionalInterface, but it is not
 * useful at all.
 * 
 * Note: For technical reasons, the mapping functions return generic functor
 * instead of specific one. Thus, the type of the resuls should be overriden.
 * 
 * See:
 * https://dzone.com/articles/functor-and-monad-examples-in-plain-java
 * 
 * Note: There is no nice way of describing higher structures (applicative
 * functors or monads) in Java.
 * 
 * @param <A> Type of the contained element(s).
 * 
 * @author  Josef Plch
 * @since   2018-04-25
 * @version 2021-01-14
 */
public interface Functor <A> {
    /**
     * map id == id <br>
     * map (f . g)  == (map f) . (map g)
     * 
     * @param <B> New type of the contained element(s).
     * @param f Function to be applied to the contained element(s).
     * @return Transformed functor.
     */
    public <B> Functor <B> map (Function <A, B> f);
}
