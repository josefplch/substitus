package com.github.josefplch.utils.data.tuple;

import com.github.josefplch.utils.data.Functor;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Ordered pair of two objects of the same type.
 * 
 * @param <A> Type of pair elements.
 * 
 * @author  Josef Plch
 * @since   2015-04-28
 * @version 2020-12-23
 */
public class UniformPair <A> extends Pair <A, A> implements Functor <A> {
    public UniformPair (A element1, A element2) {
        super (element1, element2);
    }
    
    public UniformPair (Pair <A, A> other) {
        super (other);
    }
    
    public static <A> UniformPair <A> from (List <A> list) {
        if (list.size () != 2) {
            throw new IllegalArgumentException ("The list does not contain two elements: " + list);
        }
        else {
            return new UniformPair <> (list.get (0), list.get (1));
        }
    }
    
    public static <T, U, R> BiFunction <? super Pair <T, T>, ? super Pair <U, U>, UniformPair <R>> biliftUniform (
        BiFunction <T, U, R> f,
        BiFunction <T, U, R> g
    ) {
        return ((pairT, pairU) -> new UniformPair <> (f.apply (pairT.e1, pairU.e1), g.apply (pairT.e2, pairU.e2)));
    }
   
    public <B> UniformPair <B> bimapUniform (Function <A, B> f1, Function <A, B> f2) {
        return new UniformPair <> (f1.apply (e1), f2.apply (e2));
    }
    
    public boolean both (Predicate <A> predicate) {
        return (predicate.test (super.e1) && predicate.test (super.e2));
    }
    
    public static <T, U, R> BiFunction <? super Pair <T, T>, ? super Pair <U, U>, UniformPair <R>> lift (
        BiFunction <T, U, R> f
    ) {
        return UniformPair.biliftUniform (f, f);
    }
    
    @Override
    public <B> UniformPair <B> map (Function <A, B> f) {
        return new UniformPair <> (f.apply (e1), f.apply (e2));
    }
    
    public UniformPair <A> mapOperator1 (Function <A, A> f) {
        return new UniformPair <> (f.apply (e1), e2);
    }
    
    public UniformPair <A> mapOperator2 (Function <A, A> f) {
        return new UniformPair <> (e1, f.apply (e2));
    }
    
    public boolean neither (Predicate <A> predicate) {
        return (! predicate.test (super.e1) && ! predicate.test (super.e2));
    }
    
    @Override
    public UniformPair <A> swap () {
        return new UniformPair <> (super.e2, super.e1);
    }
}
