package com.github.josefplch.utils.data.set;

import com.github.josefplch.utils.data.foldable.ComparableFoldable;
import com.github.josefplch.utils.data.list.ComparableList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @param <A> The type of elements in the set.
 * 
 * @author  Josef Plch
 * @since   2020-12-23
 * @version 2022-01-19
 */
public class ComparableSet <A extends Comparable <A>> extends FunctionalSet <A> implements ComparableFoldable <A> {
    public ComparableSet () {
    }
    
    public ComparableSet (Collection <? extends A> collection) {
        super (collection);
    }
    
    @SafeVarargs
    public static <A extends Comparable <A>> ComparableSet <A> ofComparable (A ... elements) {
        return new ComparableSet <> (Arrays.asList (elements));
    }
    
    @Override
    public ComparableSet <A> filter (Predicate <A> predicate) {
        return super.filter (predicate, ComparableSet :: new);
    }
    
    @Override
    public ComparableList <A> toList () {
        return new ComparableList <> (delegate);
    }
}
