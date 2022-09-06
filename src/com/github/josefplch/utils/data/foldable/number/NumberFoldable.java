package com.github.josefplch.utils.data.foldable.number;

import com.github.josefplch.utils.data.foldable.ComparableFoldable;
import com.github.josefplch.utils.data.list.number.NumberList;

/**
 * @param <N> The type of contained numbers.
 * @param <F> The corresponding fractional type.
 * 
 * @author  Josef Plch
 * @since   2020-12-28
 * @version 2022-01-19
 */
public interface NumberFoldable <N extends Number & Comparable <N>, F extends Number> extends ComparableFoldable <N> {
    public F arithmeticMean ();
    
    public N product ();
    
    public N sum ();
    
    @Override
    public NumberList <N, F> toList ();
}
