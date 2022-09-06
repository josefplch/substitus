package com.github.josefplch.utils.data.foldable;

import com.github.josefplch.utils.data.list.StringList;

/**
 * @author  Josef Plch
 * @since   2021-01-14
 * @version 2022-01-19
 */
public interface StringFoldable extends ComparableFoldable <String> {
    /*
    public default long lengthMax () {
        return this.foldl ((min, string) -> Math.max (min, string.length ()), Integer.MIN_VALUE);
    }
    
    public default long lengthMin () {
        return this.foldl ((min, string) -> Math.min (min, string.length ()), Integer.MAX_VALUE);
    }
    */
    public default long lengthSum () {
        return this.foldl ((sum, string) -> sum + string.length (), 0L);
    }
    
    @Override
    public default StringList toList () {
        return this.toList (StringList :: new);
    }
}
