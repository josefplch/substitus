package com.github.josefplch.utils.data.set;

import com.github.josefplch.utils.data.Bifunctor;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.tuple.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @param <T1> Type of the 1st element.
 * @param <T2> Type of the 2nd element.
 * 
 * @author  Josef Plch
 * @since   2021-01-11
 * @version 2021-01-15
 */
public class PairSet <T1, T2> extends FunctionalSet <Pair <T1, T2>> implements Bifunctor <T1, T2> {
    public PairSet () {
    }
    
    protected PairSet (Collection <? extends Pair <T1, T2>> collection) {
        super (collection);
    }
    
    public static <T1, T2> PairSet <T1, T2> fromPairs (Collection <? extends Pair <T1, T2>> collection) {
        return new PairSet <> (collection);
    }
    
    @SafeVarargs
    public static <T1, T2> PairSet <T1, T2> ofPairs (Pair <T1, T2> ... elements) {
        return PairSet.fromPairs (Arrays.asList (elements));
    }
    
    public void addPair (T1 e1, T2 e2) {
        super.add (Pair.of (e1, e2));
    }
    
    @Override
    public <R1, R2> PairSet <R1, R2> bimap (Function <T1, R1> f1, Function <T2, R2> f2) {
        return super.mapToPair (pair -> pair.bimap (f1, f2));
    }
    
    @Override
    public PairSet <T1, T2> filter (Predicate <Pair <T1, T2>> predicate) {
        return super.filter (predicate, PairSet :: new);
    }
    
    public FunctionalSet <T1> firsts () {
        return this.firsts (FunctionalSet :: new);
    }
    
    public <L1 extends FunctionalSet <T1>> L1 firsts (Supplier <L1> constructor) {
        return super.map (Pair :: get1, constructor);
    }
    
    /**
     * Look up pair with the given key (first element).
     * 
     * @param key The key to search for.
     * @return    The first value with the given key (if any).
     */
    public Optional <T2> lookUp (T1 key) {
        return super.find (pair -> Objects.equals (pair.get1 (), key)).map (Pair :: get2);
    }
    
    @Override
    public <R> PairSet <R, T2> map1 (Function <T1, R> f) {
        return super.mapToPair (pair -> pair.map1 (f));
    }
    
    @Override
    public <R> PairSet <T1, R> map2 (Function <T2, R> f) {
        return super.mapToPair (pair -> pair.map2 (f));
    }
    
    public FunctionalSet <T2> seconds () {
        return this.seconds (FunctionalSet :: new);
    }
    
    public <L2 extends FunctionalSet <T2>> L2 seconds (Supplier <L2> constructor) {
        return super.map (Pair :: get2, constructor);
    }
    
    @Override
    public PairList <T1, T2> toList () {
        return super.toList (PairList :: new);
    }
}
