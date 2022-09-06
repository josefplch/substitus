package com.github.josefplch.utils.data.foldable.number;

import com.github.josefplch.utils.data.function.Function2;
import com.github.josefplch.utils.data.list.number.DoubleList;
import com.github.josefplch.utils.data.tuple.Pair;

/**
 * @author  Josef Plch
 * @since   2020-12-28
 * @version 2020-12-28
 */
public interface DoubleFoldable extends NumberFoldable <Double, Double> {
    @Override
    public default Double arithmeticMean () {
        Function2 <Integer, Double, Double> f = (size, sum) -> sum / size;
        return (
            f.curry ().apply (
                this.foldl (
                    (res, x) -> res.bimap (size -> size + 1, sum -> sum + x),
                    Pair.of (0, 0.0)
                )
            )
        );
    }
    
    @Override
    public default Double product () {
        return this.foldl ((x, y) -> x * y, 1.0);
    }
    
    @Override
    public default Double sum () {
        return this.foldl (Double :: sum, 0.0);
    }

    @Override
    public default DoubleList toList () {
        return this.foldl (
            (res, x) -> {
                res.add (x);
                return res;
            },
            new DoubleList ()
        );
    }
}
