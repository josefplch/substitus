package com.github.josefplch.utils.data.set.number;

import com.github.josefplch.utils.data.foldable.number.LongFoldable;
import com.github.josefplch.utils.data.list.number.LongList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author  Josef Plch
 * @since   2020-12-28
 * @version 2022-01-19
 */
public class LongSet extends NumberSet <Long, Double> implements LongFoldable {
    public LongSet () {
    }
    
    public LongSet (Collection <Long> collection) {
        super (collection);
    }
    
    public static LongSet interval (long from, long to) {
        return interval (from, to, 1);
    }
    
    public static LongSet interval (long from, long to, long difference) {
        LongSet result = new LongSet ();
        for (long n = from; n <= to; n += difference) {
            result.add (n);
        }
        return result;
    }
    
    public static LongSet ofNumbers (Long ... numbers) {
        return new LongSet (Arrays.asList (numbers));
    }
    
    @Override
    public LongSet filter (Predicate <Long> predicate) {
        return super.filter (predicate, LongSet :: new);
    }
    
    @Override
    public LongList toList () {
        return new LongList (delegate);
    }
}
