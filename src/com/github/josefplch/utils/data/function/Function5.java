package com.github.josefplch.utils.data.function;

import com.github.josefplch.utils.data.tuple.Tuple5;
import java.util.Objects;
import java.util.function.Function;

/**
 * @param <T1> The type of the 1st argument to the function.
 * @param <T2> The type of the 2nd argument to the function.
 * @param <T3> The type of the 3rd argument to the function.
 * @param <T4> The type of the 4th argument to the function.
 * @param <T5> The type of the 5th argument to the function.
 * @param <R>  the type of the result of the function.
 * 
 * @author  Josef Plch
 * @since   2019-05-13
 * @version 2020-11-11
 */
@FunctionalInterface
public interface Function5 <T1, T2, T3, T4, T5, R> {
    public default <R2> Function5 <T1, T2, T3, T4, T5, R2> andThen (Function <? super R, ? extends R2> after) { 
        Objects.requireNonNull (after); 
        return (
            (T1 a1, T2 a2, T3 a3, T4 a4, T5 a5) ->
            after.apply (this.apply (a1, a2, a3, a4, a5))
        );
    }
    
    public R apply (T1 a1, T2 a2, T3 a3, T4 a4, T5 a5);
    
    public default Function4 <T2, T3, T4, T5, R> apply1 (T1 a1) {
        return ((a2, a3, a4, a5) -> this.apply (a1, a2, a3, a4, a5));
    }
    
    public default Function4 <T1, T3, T4, T5, R> apply2 (T2 a2) {
        return ((a1, a3, a4, a5) -> this.apply (a1, a2, a3, a4, a5));
    }
    
    public default Function4 <T1, T2, T4, T5, R> apply3 (T3 a3) {
        return ((a1, a2, a4, a5) -> this.apply (a1, a2, a3, a4, a5));
    }
    
    public default Function4 <T1, T2, T3, T5, R> apply4 (T4 a4) {
        return ((a1, a2, a3, a5) -> this.apply (a1, a2, a3, a4, a5));
    }
    
    public default Function4 <T1, T2, T3, T4, R> apply5 (T5 a5) {
        return ((a1, a2, a3, a4) -> this.apply (a1, a2, a3, a4, a5));
    }
    
    public default Function <Tuple5 <T1, T2, T3, T4, T5>, R> curry () {
        return (tuple ->
            this.apply (
                tuple.get1 (),
                tuple.get2 (),
                tuple.get3 (),
                tuple.get4 (),
                tuple.get5 ()
            )
        );
    }
}
