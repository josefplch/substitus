package com.github.josefplch.utils.data.list.number;

import com.github.josefplch.utils.data.foldable.number.NumberFoldable;
import com.github.josefplch.utils.data.list.ComparableList;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.tuple.UniformPair;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @param <N> The type of contained numbers.
 * @param <F> The corresponding fractional type.
 * 
 * @author  Josef Plch
 * @since   2018-10-11
 * @version 2022-01-19
 */
public abstract class NumberList <N extends Number & Comparable <N>, F extends Number> extends ComparableList <N> implements NumberFoldable <N, F> {
    public NumberList () {
    }
    
    public NumberList (Collection <? extends N> collection) {
        super (collection);
    }
    
    // Probably more efficient than the (iterable) constructor.
    public NumberList (Iterable <? extends N> iterable) {
        super (iterable);
    }
    
    @Override
    public abstract NumberList <N, F> drop (int n);
    
    @Override
    public abstract NumberList <N, F> dropWhile (Predicate <N> predicate);
    
    @Override
    public abstract NumberList <N, F> filter (Predicate <N> predicate);
    
    @Override
    public abstract FunctionalList <? extends NumberList <N, F>> group ();
    
    @Override
    public abstract FunctionalList <? extends NumberList <N, F>> groupBy (BiPredicate <N, N> predicate);
    
    @Override
    public abstract <K> FunctionalList <? extends NumberList <N, F>> groupByKey (Function <? super N, ? extends K> keyExtractor);
    
    @Override
    public abstract NumberList <N, F> intersperse (N number);
    
    @Override
    public abstract UniformPair <? extends NumberList <N, F>> partition (Predicate <N> predicate);
    
    @Override
    public abstract NumberList <N, F> reverse ();
    
    @Override
    public abstract NumberList <N, F> shuffle ();
    
    @Override
    public abstract NumberList <N, F> sortAsc ();
    
    @Override
    public abstract NumberList <N, F> sortBy (Comparator <? super N> comparator);
    
    @Override
    public abstract NumberList <N, F> sortDesc ();
    
    @Override
    public abstract NumberList <N, F> subList (int fromIndex, int toIndex);
    
    @Override
    public abstract FunctionalList <? extends NumberList <N, F>> sublists () ;
    
    @Override
    public abstract FunctionalList <? extends NumberList <N, F>> sublists (int minLength, int maxLength);
    
    @Override
    public abstract NumberList <N, F> tail () throws IllegalStateException;
    
    @Override
    public abstract NumberList <N, F> take (int n);
    
    @Override
    public abstract NumberList <N, F> takeWhile (Predicate <N> predicate);
    
    @Override
    public abstract NumberList <N, F> toList ();
}
