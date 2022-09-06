package com.github.josefplch.utils.data.set.number;

import com.github.josefplch.utils.data.foldable.number.NumberFoldable;
import com.github.josefplch.utils.data.list.number.NumberList;
import com.github.josefplch.utils.data.set.ComparableSet;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @param <N> The type of contained numbers.
 * @param <F> The corresponding fractional type.
 * 
 * @author  Josef Plch
 * @since   2020-12-28
 * @version 2020-12-28
 */
public abstract class NumberSet <N extends Number & Comparable <N>, F extends Number> extends ComparableSet <N> implements NumberFoldable <N, F> {
    public NumberSet () {
    }
    
    public NumberSet (Collection <? extends N> collection) {
        super (collection);
    }
    
    @Override
    public abstract NumberSet <N, F> filter (Predicate <N> predicate);
    
    @Override
    public abstract NumberList <N, F> toList ();
}
