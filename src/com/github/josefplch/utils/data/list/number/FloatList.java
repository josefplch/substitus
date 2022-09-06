package com.github.josefplch.utils.data.list.number;

import com.github.josefplch.utils.data.foldable.number.FloatFoldable;
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
 * @version 2022-01-19
 */
public class FloatList extends NumberList <Float, Float> implements FloatFoldable {
    public FloatList () {
    }
    
    // Probably more efficient than the (iterable) constructor.
    public FloatList (Collection <Float> collection) {
        super (collection);
    }
    
    public FloatList (Iterable <Float> iterable) {
        super (iterable);
    }
    
    public static FloatList interval (float from, float to, float difference) {
        FloatList result = new FloatList ();
        for (Float x = from; x <= to; x += difference) {
            result.add (x);
        }
        return result;
    }
    
    public static FloatList ofNumbers (Float ... numbers) {
        return new FloatList (Arrays.asList (numbers));
    }
    
    @Override
    public FloatList drop (int n) {
        return super.drop (n, FloatList :: new);
    }
    
    @Override
    public FloatList dropWhile (Predicate <Float> predicate) {
        return super.dropWhile (predicate, FloatList :: new);
    }
    
    @Override
    public FloatList filter (Predicate <Float> predicate) {
        return super.filter (predicate, FloatList :: new);
    }
    
    @Override
    public FunctionalList <FloatList> group () {
        return super.group (FloatList :: new);
    }
    
    @Override
    public FunctionalList <FloatList> groupBy (BiPredicate <Float, Float> predicate) {
        return super.groupBy (predicate, FloatList :: new);
    }
    
    @Override
    public <K> FunctionalList <FloatList> groupByKey (Function <? super Float, ? extends K> keyExtractor) {
        return this.groupByKey (keyExtractor, FloatList :: new);
    }
    
    @Override
    public FloatList intersperse (Float number) {
        return this.intersperse (number, FloatList :: new);
    }
    
    @Override
    public UniformPair <FloatList> partition (Predicate <Float> predicate) {
        return super.partition (predicate, FloatList :: new);
    }
    
    @Override
    public FloatList reverse () {
        return super.reverse (FloatList :: new);
    }
    
    @Override
    public FloatList shuffle () {
        return super.shuffle (FloatList :: new);
    }
    
    @Override
    public FloatList sortAsc () {
        return super.sortAsc (FloatList :: new);
    }
    
    @Override
    public FloatList sortBy (Comparator <? super Float> comparator) {
        return super.sortBy (comparator, FloatList :: new);
    }
    
    @Override
    public FloatList sortDesc () {
        return super.sortDesc (FloatList :: new);
    }
    
    @Override
    public FloatList subList (int fromIndex, int toIndex) {
        return super.subList (fromIndex, toIndex, FloatList :: new);
    }
    
    @Override
    public FunctionalList <FloatList> sublists () {
        return super.sublistsBy (this :: subList);
    }
    
    @Override
    public FunctionalList <FloatList> sublists (int minLength, int maxLength) {
        return super.sublistsBy (minLength, maxLength, this :: subList);
    }
    
    @Override
    public FloatList tail () throws IllegalStateException {
        return super.tail (FloatList :: new);
    }
    
    @Override
    public FloatList take (int n) {
        return super.take (n, FloatList :: new);
    }
    
    @Override
    public FloatList takeWhile (Predicate <Float> predicate) {
        return super.takeWhile (predicate, FloatList :: new);
    }
    
    @Override
    public FloatList toList () {
        return new FloatList (delegate);
    }
}
