package com.github.josefplch.utils.data.function;

import com.github.josefplch.utils.data.tuple.Pair;
import java.util.function.Function;

/**
 * @param <T> The type of the argument to the function.
 * @param <R>  the type of the result of the function.
 * 
 * @author  Josef Plch
 * @since   2019-05-16
 * @version 2019-05-16
 */
public class NamedFunction <T, R> extends Pair <String, Function <T, R>> implements Function <T, R> {
    public NamedFunction (String name, Function <T, R> delegate) {
        super (name, delegate);
    }
    
    public static <T, R> NamedFunction <T, R> create (String name, Function <T, R> delegate) {
        return new NamedFunction <> (name, delegate);
    }
    
    @Override
    public R apply (T t) {
        return e2.apply (t);
    }
    
    public String getName () {
        return e1;
    }
    
    public NamedFunction <T, R> mapOnFunction (Function <Function <T, R>, Function <T, R>> f) {
        return new NamedFunction <> (e1, f.apply (e2));
    }
    
    public NamedFunction <T, R> mapOnName (Function <String, String> f) {
        return new NamedFunction <> (f.apply (e1), e2);
    }
}
