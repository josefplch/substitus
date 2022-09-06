package com.github.josefplch.utils.data.list.number;

import com.github.josefplch.utils.data.foldable.number.IntegerFoldable;
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
public class IntegerList extends NumberList <Integer, Float> implements IntegerFoldable {
    public IntegerList () {
    }
    
    // Probably more efficient than the (iterable) constructor.
    public IntegerList (Collection <Integer> collection) {
        super (collection);
    }
    
    public IntegerList (Iterable <Integer> iterable) {
        super (iterable);
    }
    
    public static IntegerList interval (int from, int to) {
        return interval (from, to, 1);
    }
    
    public static IntegerList interval (int from, int to, int difference) {
        IntegerList result = new IntegerList ();
        for (Integer n = from; n <= to; n += difference) {
            result.add (n);
        }
        return result;
    }
    
    public static IntegerList ofNumbers (Integer ... numbers) {
        return new IntegerList (Arrays.asList (numbers));
    }
    
    @Override
    public IntegerList drop (int n) {
        return super.drop (n, IntegerList :: new);
    }
    
    @Override
    public IntegerList dropWhile (Predicate <Integer> predicate) {
        return super.dropWhile (predicate, IntegerList :: new);
    }
    
    @Override
    public IntegerList filter (Predicate <Integer> predicate) {
        return super.filter (predicate, IntegerList :: new);
    }
    
    @Override
    public FunctionalList <IntegerList> group () {
        return super.group (IntegerList :: new);
    }
    
    @Override
    public FunctionalList <IntegerList> groupBy (BiPredicate <Integer, Integer> predicate) {
        return super.groupBy (predicate, IntegerList :: new);
    }
    
    @Override
    public <K> FunctionalList <IntegerList> groupByKey (Function <? super Integer, ? extends K> keyExtractor) {
        return this.groupByKey (keyExtractor, IntegerList :: new);
    }
    
    @Override
    public IntegerList intersperse (Integer number) {
        return this.intersperse (number, IntegerList :: new);
    }
    
    @Override
    public UniformPair <IntegerList> partition (Predicate <Integer> predicate) {
        return super.partition (predicate, IntegerList :: new);
    }
    
    @Override
    public IntegerList reverse () {
        return super.reverse (IntegerList :: new);
    }
    
    @Override
    public IntegerList shuffle () {
        return super.shuffle (IntegerList :: new);
    }
    
    @Override
    public IntegerList sortAsc () {
        return super.sortAsc (IntegerList :: new);
    }
    
    @Override
    public IntegerList sortBy (Comparator <? super Integer> comparator) {
        return super.sortBy (comparator, IntegerList :: new);
    }
    
    @Override
    public IntegerList sortDesc () {
        return super.sortDesc (IntegerList :: new);
    }
    
    @Override
    public IntegerList subList (int fromIndex, int toIndex) {
        return super.subList (fromIndex, toIndex, IntegerList :: new);
    }
    
    @Override
    public FunctionalList <IntegerList> sublists () {
        return super.sublistsBy (this :: subList);
    }
    
    @Override
    public FunctionalList <IntegerList> sublists (int minLength, int maxLength) {
        return super.sublistsBy (minLength, maxLength, this :: subList);
    }
    
    @Override
    public IntegerList tail () throws IllegalStateException {
        return super.tail (IntegerList :: new);
    }
    
    @Override
    public IntegerList take (int n) {
        return super.take (n, IntegerList :: new);
    }
    
    @Override
    public IntegerList takeWhile (Predicate <Integer> predicate) {
        return super.takeWhile (predicate, IntegerList :: new);
    }
    
    @Override
    public IntegerList toList () {
        return new IntegerList (delegate);
    }
}
