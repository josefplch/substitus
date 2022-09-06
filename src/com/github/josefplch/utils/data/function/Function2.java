package com.github.josefplch.utils.data.function;

import com.github.josefplch.utils.data.tuple.Pair;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @param <T1> The type of the 1st argument to the function.
 * @param <T2> The type of the 2nd argument to the function.
 * @param <R>  the type of the result of the function.
 * 
 * @author  Josef Plch
 * @since   2019-05-13
 * @version 2020-11-11
 */
@FunctionalInterface
public interface Function2 <T1, T2, R> extends BiFunction <T1, T2, R> {
    @Override
    public default <R2> Function2 <T1, T2, R2> andThen (Function <? super R, ? extends R2> after) { 
        Objects.requireNonNull (after);
        return (
            (T1 a1, T2 a2) ->
            after.apply (this.apply (a1, a2))
        );
    }
    
    public default Function <T2, R> apply1 (T1 a1) {
        return (a2 -> this.apply (a1, a2));
    }
    
    public default Function <T1, R> apply2 (T2 a2) {
        return (a1 -> this.apply (a1, a2));
    }
    
    public default Function <Pair <T1, T2>, R> curry () {
        return (pair ->
            this.apply (
                pair.get1 (),
                pair.get2 ()
            )
        );
    }
}
