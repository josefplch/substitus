package com.github.josefplch.utils.data.list.number;

import com.github.josefplch.utils.data.foldable.number.LongFoldable;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.tuple.UniformPair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author  Josef Plch
 * @since   2018-10-11
 * @version 2022-01-19
 */
public class LongList extends NumberList <Long, Double> implements LongFoldable {
    public LongList () {
    }
    
    // Probably more efficient than the (iterable) constructor.
    public LongList (Collection <Long> collection) {
        super (collection);
    }
    
    public LongList (Iterable <Long> iterable) {
        super (iterable);
    }
    
    public static LongList interval (long from, long to) {
        return interval (from, to, 1);
    }
    
    public static LongList interval (long from, long to, long difference) {
        LongList result = new LongList ();
        for (Long n = from; n <= to; n += difference) {
            result.add (n);
        }
        return result;
    }
    
    public static LongList ofNumbers (Long ... numbers) {
        return new LongList (Arrays.asList (numbers));
    }
    
    @Override
    public LongList drop (int n) {
        return super.drop (n, LongList :: new);
    }
    
    @Override
    public LongList dropWhile (Predicate <Long> predicate) {
        return super.dropWhile (predicate, LongList :: new);
    }
    
    @Override
    public LongList filter (Predicate <Long> predicate) {
        return super.filter (predicate, LongList :: new);
    }
    
    @Override
    public FunctionalList <LongList> group () {
        return super.group (LongList :: new);
    }
    
    @Override
    public FunctionalList <LongList> groupBy (BiPredicate <Long, Long> predicate) {
        return super.groupBy (predicate, LongList :: new);
    }
    
    @Override
    public <K> FunctionalList <LongList> groupByKey (Function <? super Long, ? extends K> keyExtractor) {
        return this.groupByKey (keyExtractor, LongList :: new);
    }
    
    @Override
    public LongList intersperse (Long number) {
        return this.intersperse (number, LongList :: new);
    }
    
    @Override
    public UniformPair <LongList> partition (Predicate <Long> predicate) {
        return super.partition (predicate, LongList :: new);
    }
    
    @Override
    public LongList reverse () {
        return super.reverse (LongList :: new);
    }
    
    @Override
    public LongList shuffle () {
        return super.shuffle (LongList :: new);
    }
    
    @Override
    public LongList sortAsc () {
        return super.sortAsc (LongList :: new);
    }
    
    @Override
    public LongList sortBy (Comparator <? super Long> comparator) {
        return super.sortBy (comparator, LongList :: new);
    }
    
    @Override
    public LongList sortDesc () {
        return super.sortDesc (LongList :: new);
    }
    
    @Override
    public LongList subList (int fromIndex, int toIndex) {
        return super.subList (fromIndex, toIndex, LongList :: new);
    }
    
    @Override
    public FunctionalList <LongList> sublists () {
        return super.sublistsBy (this :: subList);
    }
    
    @Override
    public FunctionalList <LongList> sublists (int minLength, int maxLength) {
        return super.sublistsBy (minLength, maxLength, this :: subList);
    }
    
    @Override
    public LongList tail () throws IllegalStateException {
        return super.tail (LongList :: new);
    }
    
    @Override
    public LongList take (int n) {
        return super.take (n, LongList :: new);
    }
    
    @Override
    public LongList takeWhile (Predicate <Long> predicate) {
        return super.takeWhile (predicate, LongList :: new);
    }

    @Override
    public LongList toList () {
        return new LongList (delegate);
    }
}
