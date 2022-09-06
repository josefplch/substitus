package com.github.josefplch.utils.data.list;

import com.github.josefplch.utils.data.Bifunctor;
import com.github.josefplch.utils.data.map.FunctionalMap;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.tuple.UniformPair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * List of pairs.
 * 
 * @param <T1> Type of the 1st element.
 * @param <T2> Type of the 2nd element.
 * 
 * @author  Josef Plch
 * @since   2019-03-22
 * @version 2021-01-18
 */
public class PairList <T1, T2> extends FunctionalList <Pair <T1, T2>> implements Bifunctor <T1, T2> {
    public PairList () {
        super ();
    }
    
    // Probably more efficient than the (iterable) constructor.
    public PairList (Collection <? extends Pair <T1, T2>> collection) {
        super (collection);
    }
    
    public PairList (Iterable <? extends Pair <T1, T2>> iterable) {
        super (iterable);
    }
    
    public static <A, B> PairList <A, B> cartesianProduct (List <A> listA, List <B> listB) {
        PairList <A, B> result = new PairList <> ();
        for (A a : listA) {
            for (B b : listB) {
                result.addPair (a, b);
            }
        }
        return result;
    }
    
    @SafeVarargs
    public static <T1, T2> PairList <T1, T2> ofPairs (Pair <T1, T2> ... pairs) {
        return (new PairList <> (Arrays.asList (pairs)));
    }
    
    /**
     * Zip takes two lists and returns a list of corresponding pairs. If one
     * input list is short, excess elements of the longer list are discarded.
     * 
     * @param <A> Arbitrary type.
     * @param <B> Arbitrary type.
     * 
     * @param listA List of type A to provide first pair element.
     * @param listB List of type B to provide second pair element.
     * @return List of corresponding pairs.
     */
    public static <A, B> PairList <A, B> zip (List <A> listA, List <B> listB) {
        return ListUtils.zipWith (Pair :: of, listA, listB, PairList :: new);
    }
    
    public void addPair (T1 e1, T2 e2) {
        super.add (Pair.of (e1, e2));
    }

    @Override
    public <R1, R2> PairList <R1, R2> bimap (Function <T1, R1> f1, Function <T2, R2> f2) {
        return super.mapToPair (pair -> pair.bimap (f1, f2));
    }
    
    public boolean containsKey (T1 key) {
        return this.lookUp (key).isPresent ();
    }
    
    @Override
    public PairList <T1, T2> drop (int n) {
        return super.drop (n, PairList :: new);
    }
    
    @Override
    public PairList <T1, T2> dropWhile (Predicate <Pair <T1, T2>> predicate) {
        return super.dropWhile (predicate, PairList :: new);
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
    public PairList <T1, T2> filter (Predicate <Pair <T1, T2>> predicate) {
        return super.filter (predicate, PairList :: new);
    }
    
    public FunctionalList <T1> firsts () {
        return this.firsts (FunctionalList :: new);
    }
    
    public <L1 extends FunctionalList <T1>> L1 firsts (Supplier <L1> constructor) {
        return super.map (Pair :: get1, constructor);
    }
    
    @Override
    public FunctionalList <? extends PairList <T1, T2>> group () {
        return super.group (PairList :: new);
    }
    
    @Override
    public FunctionalList <? extends PairList <T1, T2>> groupBy (BiPredicate <Pair <T1, T2>, Pair <T1, T2>> predicate) {
        return super.groupBy (predicate, PairList :: new);
    }
    
    @Override
    public <K> FunctionalList <? extends PairList <T1, T2>> groupByKey (Function <? super Pair <T1, T2>, ? extends K> keyExtractor) {
        return this.groupByKey (keyExtractor, PairList :: new);
    }
    
    @Override
    public PairList <T1, T2> intersperse (Pair <T1, T2> element) {
        return this.intersperse (element, PairList :: new);
    }
    
    @Override
    public <R> PairList <R, T2> map1 (Function <T1, R> f) {
        return super.mapToPair (pair -> pair.map1 (f));
    }
    
    @Override
    public <R> PairList <T1, R> map2 (Function <T2, R> f) {
        return super.mapToPair (pair -> pair.map2 (f));
    }
    
    @Override
    public UniformPair <? extends PairList <T1, T2>> partition (Predicate <Pair <T1, T2>> predicate) {
        return super.partition (predicate, PairList :: new);
    }
    
    @Override
    public PairList <T1, T2> reverse () {
        return super.reverse (PairList :: new);
    }
    
    public FunctionalList <T2> seconds () {
        return this.seconds (FunctionalList :: new);
    }
    
    public <L2 extends FunctionalList <T2>> L2 seconds (Supplier <L2> constructor) {
        return super.map (Pair :: get2, constructor);
    }
    
    @Override
    public PairList <T1, T2> shuffle () {
        return super.shuffle (PairList :: new);
    }
    
    @Override
    public PairList <T1, T2> sortBy (Comparator <? super Pair <T1, T2>> comparator) {
        return super.sortBy (comparator, PairList :: new);
    }
    
    @Override
    public PairList <T1, T2> subList (int fromIndex, int toIndex) {
        return super.subList (fromIndex, toIndex, PairList :: new);
    }
    
    @Override
    public FunctionalList <PairList <T1, T2>> sublists () {
        return super.sublistsBy (this :: subList);
    }
    
    @Override
    public FunctionalList <PairList <T1, T2>> sublists (int minLength, int maxLength) {
        return super.sublistsBy (minLength, maxLength, this :: subList);
    }
    
    public PairList <T2, T1> swap () {
        return super.mapToPair (Pair :: swap);
    }
    
    @Override
    public PairList <T1, T2> tail () throws IllegalStateException {
        return super.tail (PairList :: new);
    }
    
    @Override
    public PairList <T1, T2> take (int n) {
        return super.take (n, PairList :: new);
    }
    
    @Override
    public PairList <T1, T2> takeWhile (Predicate <Pair <T1, T2>> predicate) {
        return super.takeWhile (predicate, PairList :: new);
    }
    
    public FunctionalMap <T1, T2> toMap () throws IllegalStateException {
        FunctionalMap <T1, T2> result = new FunctionalMap <> ();
        for (Pair <T1, T2> pair : delegate) {
            T1 key = pair.get1 ();
            if (result.containsKey (key)) {
                throw new IllegalStateException ("The list contains non-unique pair key: " + key);
            }
            result.put (key, pair.get2 ());
        }
        return result;
    }
    
    public Pair <? extends FunctionalList <T1>, ? extends FunctionalList <T2>> unzip () {
        return this.unzip (FunctionalList :: new, FunctionalList :: new);
    }
    
    /**
     * Transform the list into a list of first components and a list of second
     * components.
     * 
     * See also: EitherList.partitionEithers
     * 
     * @param <L1> Type of the first list.
     * @param <L2> Type of the second list.
     * @param constructor1 Constructor of the first list.
     * @param constructor2 Constructor of the second list.
     * @return Two lists.
     */
    public <L1 extends FunctionalList <T1>, L2 extends FunctionalList <T2>> Pair <L1, L2> unzip (
        Supplier <L1> constructor1,
        Supplier <L2> constructor2
    ) {
        L1 list1 = constructor1.get ();
        L2 list2 = constructor2.get ();
        for (Pair <T1, T2> pair : this) {
            list1.add (pair.get1 ());
            list2.add (pair.get2 ());
        }
        return Pair.of (list1, list2);
    }
}
