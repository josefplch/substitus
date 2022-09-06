package com.lingea.utils.optimizer;

import com.github.josefplch.utils.data.tuple.Tuple5;

/**
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @param <T4>
 * @param <T5>
 * 
 * @author  Josef Plch
 * @since   2018-06-07
 * @version 2018-06-07
 */
public class OptimizedArguments5 <T1, T2, T3, T4, T5> extends Tuple5 <
    OptimizedValue <T1>,
    OptimizedValue <T2>,
    OptimizedValue <T3>,
    OptimizedValue <T4>,
    OptimizedValue <T5>
> {
    public OptimizedArguments5 (
        OptimizedValue <T1> a1,
        OptimizedValue <T2> a2,
        OptimizedValue <T3> a3,
        OptimizedValue <T4> a4,
        OptimizedValue <T5> a5
    ) {
        super (a1, a2, a3, a4, a5);
    }
}
