package com.github.josefplch.utils.data.list;

import com.github.josefplch.utils.data.foldable.ComparableFoldable;
import com.github.josefplch.utils.data.set.ComparableSet;
import com.github.josefplch.utils.data.tuple.UniformPair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @param <A> The type of elements in the list.
 * 
 * @author  Josef Plch
 * @since   2018-10-11
 * @version 2022-01-19
 */
public class ComparableList <A extends Comparable <A>> extends FunctionalList <A> implements Comparable <ComparableList <A>>, ComparableFoldable <A> {
    public ComparableList () {
    }
    
    // Probably more efficient than the (iterable) constructor.
    public ComparableList (Collection <? extends A> collection) {
        super (collection);
    }
    
    public ComparableList (Iterable <? extends A> iterable) {
        super (iterable);
    }
    
    @SafeVarargs
    public static <A extends Comparable <A>> ComparableList <A> ofComparables (A ... elements) {
        return (new ComparableList <> (Arrays.asList (elements)));
    }
    
    @Override
    public int compareTo (ComparableList <A> other) {
        int result = 0;
        int thisSize = this.size ();
        int otherSize = other.size ();
        int i = 0;
        while (i < thisSize && i < otherSize) {
            int comparison = this.get (i).compareTo (other.get (i));
            if (comparison != 0) {
                result = comparison;
                break;
            }
            i++;
        }
        return (result != 0) ? result : (thisSize - otherSize);
    }
    
    @Override
    public ComparableList <A> drop (int n) {
        return super.drop (n, ComparableList :: new);
    }
    
    @Override
    public ComparableList <A> dropWhile (Predicate <A> predicate) {
        return super.dropWhile (predicate, ComparableList :: new);
    }
    
    @Override
    public ComparableList <A> filter (Predicate <A> predicate) {
        return super.filter (predicate, ComparableList :: new);
    }
    
    @Override
    public FunctionalList <? extends ComparableList <A>> group () {
        return super.group (ComparableList :: new);
    }
    
    @Override
    public FunctionalList <? extends ComparableList <A>> groupBy (BiPredicate <A, A> predicate) {
        return super.groupBy (predicate, ComparableList :: new);
    }
    
    @Override
    public <K> FunctionalList <? extends ComparableList <A>> groupByKey (Function <? super A, ? extends K> keyExtractor) {
        return this.groupByKey (keyExtractor, ComparableList :: new);
    }
    
    @Override
    public ComparableList <A> intersperse (A element) {
        return this.intersperse (element, ComparableList :: new);
    }
    
    @Override
    public UniformPair <? extends ComparableList <A>> partition (Predicate <A> predicate) {
        return super.partition (predicate, ComparableList :: new);
    }
    
    @Override
    public ComparableList <A> reverse () {
        return super.reverse (ComparableList :: new);
    }
    
    @Override
    public ComparableList <A> shuffle () {
        return super.shuffle (ComparableList :: new);
    }
    
    public ComparableList <A> sortAsc () {
        return this.sortAsc (ComparableList :: new);
    }
    
    protected <LA extends ComparableList <A>> LA sortAsc (Supplier <LA> constructor) {
        return super.sortBy (Comparator.naturalOrder (), constructor);
    }
    
    @Override
    public ComparableList <A> sortBy (Comparator <? super A> comparator) {
        return super.sortBy (comparator, ComparableList :: new);
    }
    
    public ComparableList <A> sortDesc () {
        return this.sortDesc (ComparableList :: new);
    }
    
    protected <LA extends ComparableList <A>> LA sortDesc (Supplier <LA> constructor) {
        return super.sortBy (Comparator.reverseOrder (), constructor);
    }
    
    @Override
    public ComparableList <A> subList (int fromIndex, int toIndex) {
        return super.subList (fromIndex, toIndex, ComparableList :: new);
    }
    
    @Override
    public FunctionalList <? extends ComparableList <A>> sublists () {
        return super.sublistsBy (this :: subList);
    }
    
    @Override
    public FunctionalList <? extends ComparableList <A>> sublists (int minLength, int maxLength) {
        return super.sublistsBy (minLength, maxLength, this :: subList);
    }
    
    @Override
    public ComparableList <A> tail () throws IllegalStateException {
        return super.tail (ComparableList :: new);
    }
    
    @Override
    public ComparableList <A> take (int n) {
        return super.take (n, ComparableList :: new);
    }
    
    @Override
    public ComparableList <A> takeWhile (Predicate <A> predicate) {
        return super.takeWhile (predicate, ComparableList :: new);
    }
    
    @Override
    public ComparableList <A> toList () {
        return new ComparableList <> (delegate);
    }
    
    @Override
    public ComparableSet <A> toSet () {
        return new ComparableSet <> (delegate);
    }
}
