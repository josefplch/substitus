package com.github.josefplch.utils.data;

import com.github.josefplch.utils.data.list.FunctionalList;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * @author  Josef Plch
 * @since   2018-11-07
 * @version 2021-01-18
 */
public abstract class OptionalUtils {
    public static <A> FunctionalList <A> asList (Optional <A> optional) {
        return optional.map (FunctionalList :: of).orElse (FunctionalList.of ());
    }
    
    public static <A> FunctionalList <A> catOptionals (
        FunctionalList <Optional <A>> list
    ) {
        return list.filter (Optional :: isPresent).map (Optional :: get);
    }
    
    public static <A> Optional <A> join (Optional <Optional <A>> a) {
        return a.orElse (Optional.empty ());
    }
    
    public static <A, B, C> Optional <C> lift2  (
        BiFunction <A, B, C> f,
        Optional <A> a,
        Optional <B> b
    ) {
        Optional <C> result;
        if (! a.isPresent () || ! b.isPresent ()) {
            result = Optional.empty ();
        }
        else {
            result = Optional.of (f.apply (a.get (), b.get ()));
        }
        return result;
    }
}
