package com.github.josefplch.utils.data.foldable.number;

import com.github.josefplch.utils.data.function.Function2;
import com.github.josefplch.utils.data.list.number.LongList;
import com.github.josefplch.utils.data.tuple.Pair;

/**
 * @author  Josef Plch
 * @since   2020-12-28
 * @version 2020-12-28
 */
public interface LongFoldable extends NumberFoldable <Long, Double> {
    @Override
    public default Double arithmeticMean () {
        Function2 <Integer, Double, Double> f = (size, sum) -> 1.0 * sum / size;
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
    public default Long product () {
        return this.foldl ((x, y) -> x * y, 1L);
    }
    
    @Override
    public default Long sum () {
        return this.foldl (Long :: sum, 0L);
    }
    
    @Override
    public default LongList toList () {
        return this.foldl (
            (res, x) -> {
                res.add (x);
                return res;
            },
            new LongList ()
        );
    }
}
