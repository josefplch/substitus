package com.github.josefplch.utils.data.foldable.number;

import com.github.josefplch.utils.data.function.Function2;
import com.github.josefplch.utils.data.list.number.FloatList;
import com.github.josefplch.utils.data.tuple.Pair;

/**
 * @author  Josef Plch
 * @since   2020-12-28
 * @version 2020-12-28
 */
public interface FloatFoldable extends NumberFoldable <Float, Float> {
    @Override
    public default Float arithmeticMean () {
        Function2 <Integer, Float, Float> f = (size, sum) -> sum / size;
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
    public default Float product () {
        return this.foldl ((x, y) -> x * y, 1.0F);
    }
    
    @Override
    public default Float sum () {
        return this.foldl (Float :: sum, 0.0F);
    }

    @Override
    public default FloatList toList () {
        return this.foldl (
            (res, x) -> {
                res.add (x);
                return res;
            },
            new FloatList ()
        );
    }
}
