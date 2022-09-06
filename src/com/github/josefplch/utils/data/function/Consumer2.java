package com.github.josefplch.utils.data.function;

import com.github.josefplch.utils.data.tuple.Pair;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @param <A1> The type of the 1st argument to the consumer.
 * @param <A2> The type of the 2nd argument to the consumer.
 * 
 * @author  Josef Plch
 * @since   2022-01-14
 * @version 2022-01-14
 */
@FunctionalInterface
public interface Consumer2 <A1, A2> extends BiConsumer <A1, A2> {
    public default Consumer <A2> accept1 (A1 a1) {
        return (a2 -> this.accept (a1, a2));
    }
    
    public default Consumer <A1> accept2 (A2 a2) {
        return (a1 -> this.accept (a1, a2));
    }
    
    public default Consumer <Pair <A1, A2>> curry () {
        return (pair ->
            this.accept (
                pair.get1 (),
                pair.get2 ()
            )
        );
    }
}
