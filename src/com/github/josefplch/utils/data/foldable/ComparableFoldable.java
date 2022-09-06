package com.github.josefplch.utils.data.foldable;

import com.github.josefplch.utils.data.ComparableUtils;
import com.github.josefplch.utils.data.list.ComparableList;
import java.util.Optional;

/**
 * @param <A> Type of contained elements, it must be comparable.
 * 
 * @author  Josef Plch
 * @since   2019-03-29
 * @version 2022-01-19
 */
public interface ComparableFoldable <A extends Comparable <A>> extends Foldable <A> {
    public default Optional <A> maximum () {
        return this.foldl (
            (res, x) -> Optional.of (! res.isPresent () ? x : ComparableUtils.max (res.get (), x)),
            Optional.empty ()
        );
    }
    
    public default A maximum (A defaultValue) {
        return this.foldl (ComparableUtils :: max, defaultValue);
    }
    
    public default Optional <A> minimum () {
        return this.foldl (
            (res, x) -> Optional.of (! res.isPresent () ? x : ComparableUtils.min (res.get (), x)),
            Optional.empty ()
        );
    }
    
    public default A minimum (A defaultValue) {
        return this.foldl (ComparableUtils :: min, defaultValue);
    }
    
    @Override
    public default ComparableList <A> toList () {
        return this.toList (ComparableList :: new);
    }
}
