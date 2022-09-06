package com.github.josefplch.utils.data.list.number;

import com.github.josefplch.utils.data.foldable.number.DoubleFoldable;
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
 * @since   2018-11-06
 * @version 2021-01-08
 */
public class DoubleList extends NumberList <Double, Double> implements DoubleFoldable {
    public DoubleList () {
    }
    
    // Probably more efficient than the (iterable) constructor.
    public DoubleList (Collection <Double> collection) {
        super (collection);
    }
    
    public DoubleList (Iterable <Double> iterable) {
        super (iterable);
    }
    
    public static DoubleList interval (double from, double to, double difference) {
        DoubleList result = new DoubleList ();
        for (Double x = from; x <= to; x += difference) {
            result.add (x);
        }
        return result;
    }
    
    public static DoubleList ofNumbers (Double ... numbers) {
        return new DoubleList (Arrays.asList (numbers));
    }
    
    @Override
    public DoubleList drop (int n) {
        return super.drop (n, DoubleList :: new);
    }
    
    @Override
    public DoubleList dropWhile (Predicate <Double> predicate) {
        return super.dropWhile (predicate, DoubleList :: new);
    }
    
    @Override
    public DoubleList filter (Predicate <Double> predicate) {
        return super.filter (predicate, DoubleList :: new);
    }
    
    @Override
    public FunctionalList <DoubleList> group () {
        return super.group (DoubleList :: new);
    }
    
    @Override
    public FunctionalList <DoubleList> groupBy (BiPredicate <Double, Double> predicate) {
        return super.groupBy (predicate, DoubleList :: new);
    }
    
    @Override
    public <K> FunctionalList <DoubleList> groupByKey (Function <? super Double, ? extends K> keyExtractor) {
        return this.groupByKey (keyExtractor, DoubleList :: new);
    }
    
    @Override
    public DoubleList intersperse (Double number) {
        return this.intersperse (number, DoubleList :: new);
    }
    
    @Override
    public UniformPair <DoubleList> partition (Predicate <Double> predicate) {
        return super.partition (predicate, DoubleList :: new);
    }
    
    @Override
    public DoubleList reverse () {
        return super.reverse (DoubleList :: new);
    }
    
    @Override
    public DoubleList shuffle () {
        return super.shuffle (DoubleList :: new);
    }
    
    @Override
    public DoubleList sortAsc () {
        return super.sortAsc (DoubleList :: new);
    }
    
    @Override
    public DoubleList sortBy (Comparator <? super Double> comparator) {
        return super.sortBy (comparator, DoubleList :: new);
    }
    
    @Override
    public DoubleList sortDesc () {
        return super.sortDesc (DoubleList :: new);
    }
    
    @Override
    public DoubleList subList (int fromIndex, int toIndex) {
        return super.subList (fromIndex, toIndex, DoubleList :: new);
    }
    
    @Override
    public FunctionalList <DoubleList> sublists () {
        return super.sublistsBy (this :: subList);
    }
    
    @Override
    public FunctionalList <DoubleList> sublists (int minLength, int maxLength) {
        return super.sublistsBy (minLength, maxLength, this :: subList);
    }
    
    @Override
    public DoubleList tail () throws IllegalStateException {
        return super.tail (DoubleList :: new);
    }
    
    @Override
    public DoubleList take (int n) {
        return super.take (n, DoubleList :: new);
    }
    
    @Override
    public DoubleList takeWhile (Predicate <Double> predicate) {
        return super.takeWhile (predicate, DoubleList :: new);
    }
    
    @Override
    public DoubleList toList () {
        return new DoubleList (delegate);
    }
}
