package com.github.josefplch.utils.data.set;

import com.github.josefplch.utils.data.foldable.StringFoldable;
import com.github.josefplch.utils.data.list.StringList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author  Josef Plch
 * @since   2020-12-03
 * @version 2022-01-19
 */
public class StringSet extends ComparableSet <String> implements StringFoldable {
    public StringSet () {
        super ();
    }
    
    public StringSet (Collection <String> collection) {
        super (collection);
    }
    
    public static StringSet ofStrings (String ... strings) {
        return (new StringSet (Arrays.asList (strings)));
    }
    
    @Override
    public StringSet filter (Predicate <String> predicate) {
        return super.filter (predicate, StringSet :: new);
    }
    
    public StringSet filterNonBlank () {
        return this.filter (s -> ! s.isBlank ());
    }
    
    public StringSet filterNonEmpty () {
        return this.filter (s -> ! s.isEmpty ());
    }
    
    @Override
    public StringList toList () {
        return new StringList (delegate);
    }
}
