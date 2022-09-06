package com.github.josefplch.utils.data.foldable.number;

import com.github.josefplch.utils.data.function.Function2;
import com.github.josefplch.utils.data.list.number.IntegerList;
import com.github.josefplch.utils.data.tuple.Pair;

/**
 * @author  Josef Plch
 * @since   2020-12-28
 * @version 2020-12-28
 */
public interface IntegerFoldable extends NumberFoldable <Integer, Float> {
    @Override
    public default Float arithmeticMean () {
        Function2 <Integer, Float, Float> f = (size, sum) -> 1.0F * sum / size;
        return (
            f.curry ().apply (
                this.foldl (
                    (res, x) -> res.bimap (size -> size + 1, sum -> sum + x),
                    Pair.of (0, 0.0F)
                )
            )
        );
    }
    
    @Override
    public default Integer product () {
        return this.foldl ((x, y) -> x * y, 1);
    }
    
    @Override
    public default Integer sum () {
        return this.foldl (Integer :: sum, 0);
    }
    
    @Override
    public default IntegerList toList () {
        return this.foldl (
            (res, x) -> {
                res.add (x);
                return res;
            },
            new IntegerList ()
        );
    }
}
