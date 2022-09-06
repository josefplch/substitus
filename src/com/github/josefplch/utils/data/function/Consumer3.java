package com.github.josefplch.utils.data.function;

import com.github.josefplch.utils.data.tuple.Tuple3;
import java.util.function.Consumer;

/**
 * @param <A1> The type of the 1st argument to the consumer.
 * @param <A2> The type of the 2nd argument to the consumer.
 * @param <A3> The type of the 3rd argument to the consumer.
 * 
 * @author  Josef Plch
 * @since   2022-01-14
 * @version 2022-01-14
 */
@FunctionalInterface
public interface Consumer3 <A1, A2, A3> {
    public void accept (A1 a1, A2 a2, A3 a3);
    
    public default Consumer2 <A2, A3> accept1 (A1 a1) {
        return ((a2, a3) -> this.accept (a1, a2, a3));
    }
    
    public default Consumer2 <A1, A3> accept2 (A2 a2) {
        return ((a1, a3) -> this.accept (a1, a2, a3));
    }
    
    public default Consumer2 <A1, A2> accept3 (A3 a3) {
        return ((a1, a2) -> this.accept (a1, a2, a3));
    }
    
    public default Consumer <Tuple3 <A1, A2, A3>> curry () {
        return (tuple ->
            this.accept (
                tuple.get1 (),
                tuple.get2 (),
                tuple.get3 ()
            )
        );
    }
}
