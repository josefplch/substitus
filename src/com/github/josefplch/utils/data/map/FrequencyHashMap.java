package com.github.josefplch.utils.data.map;

import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.tuple.Pair;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * @param <K> Type of key.
 * 
 * @author  Josef Plch
 * @since   2018-05-11
 * @version 2019-04-24
 */
public class FrequencyHashMap <K> extends HashMap <K, Long> implements FrequencyCounter <K> {
    public static <K> FrequencyHashMap <K> forStream (Stream <K> stream) {
        FrequencyHashMap <K> result = new FrequencyHashMap <> ();
        stream.forEach (a -> result.incrementFrequency (a));
        return result;
    }
    
    public static <K> FrequencyHashMap <K> of (Iterable <K> iterable) {
        FrequencyHashMap <K> result = new FrequencyHashMap <> ();
        for (K a : iterable) {
            result.incrementFrequency (a);
        }
        return result;
    }
    
    public static <K> FrequencyHashMap <K> ofCounted (Iterable <Pair <K, Long>> iterable) {
        FrequencyHashMap <K> result = new FrequencyHashMap <> ();
        for (Pair <K, Long> counted : iterable) {
            result.modifyFrequency (counted.get1 (), counted.get2 ());
        }
        return result;
    }
    
    @Override
    public Long get (Object key) {
        return super.getOrDefault (key, 0L);
    }
    
    @Override
    public void modifyFrequency (K key, Long difference) {
        this.merge (key, difference, Long :: sum);
    }
    
    @Override
    public PairList <K, Long> toAscList () {
        return (
            MapUtils.toList (this)
            .sortBy (Comparator.comparing (Pair :: get2))
        );
    }
    
    @Override
    public PairList <K, Long> toAscListBy (Comparator <K> keyComparator) {
        return (
            MapUtils.toList (this)
            .mapToPair (Pair :: swap)
            .sortBy (
                Pair.LexicographicalComparator.basedOn (
                    (x, y) -> x.compareTo (y),
                    keyComparator
                )
            )
            .mapToPair (Pair :: swap)
        );
    }
    
    @Override
    public PairList <K, Long> toDescList () {
        return (this.toAscList ().reverse ());
    }
    
    @Override
    public PairList <K, Long> toDescListBy (Comparator <K> keyComparator) {
        return (
            MapUtils.toList (this)
            .mapToPair (Pair :: swap)
            .sortBy (
                Pair.LexicographicalComparator.basedOn (
                    (x, y) -> y.compareTo (x),
                    keyComparator
                )
            )
            .mapToPair (Pair :: swap)
        );
    }
}
