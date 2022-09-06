package com.github.josefplch.utils.data.foldable;

import com.github.josefplch.utils.data.function.ConstantFunction2;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.map.FunctionalMap;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Data structures that can be folded. Haskell-inspired interface, similar to
 * Java's Stream.
 * 
 * @param <A> Type of contained elements.
 * 
 * @author  Josef Plch
 * @since   2018-05-03
 * @version 2021-01-15
 */
public interface Foldable <A> {
    public default boolean all (Predicate <A> predicate) {
        return this.foldl ((res, x) -> res && predicate.test (x), true);
    }
    
    public static boolean and (Foldable <Boolean> foldable) {
        return foldable.foldl (Boolean :: logicalAnd, true);
    }    
    
    public default boolean any (Predicate <A> predicate) {
        return this.foldl ((res, x) -> res || predicate.test (x), false);
    }
    
    public default int count (A element) {
        return this.countIf (x -> Objects.equals (x, element));
    }
    
    public default FunctionalMap <A, Integer> countAll () {
        return this.foldl (
            (map, x) -> {
                map.merge (x, 1, Integer :: sum);
                return map;
            },
            new FunctionalMap <> ()
        );
    }
    
    public default int countIf (Predicate <A> predicate) {
        return this.foldl (
            (res, x) -> predicate.test (x) ? (res + 1) : res,
            0
        );
    }
    
    public default boolean elem (A e) {
        return this.foldl ((res, x) -> res || Objects.equals (e, x), false);
    }
    
    public default Optional <A> find (Predicate <A> predicate) {
        return Optional.ofNullable (
            this.foldl (
                (res, x) -> Objects.isNull (res) && predicate.test (x) ? x : res,
                null
            )
        );
    }
    
    public <B> B foldl (BiFunction <B, A, B> f, B defaultValue);
    
    public <B> B foldr (BiFunction <A, B, B> f, B defaultValue);
    
    public default boolean isEmpty () {
        return this.foldl (ConstantFunction2.forValue (false), true);
    }
    
    public default Optional <A> maximumBy (Comparator <A> comparator) {
        return this.foldl (
            (res, x) -> Optional.of (! res.isPresent () ? x : (comparator.compare (res.get (), x) >= 0 ? res.get () : x)),
            Optional.empty ()
        );
    }
    
    public default A maximumBy (Comparator <A> comparator, A defaultValue) {
        return this.foldl (
            (res, x) -> comparator.compare (res, x) >= 0 ? res : x,
            defaultValue
        );
    }
    
    public default Optional <A> minimumBy (Comparator <A> comparator) {
        return this.foldl (
            (res, x) -> Optional.of (! res.isPresent () ? x : (comparator.compare (res.get (), x) <= 0 ? res.get () : x)),
            Optional.empty ()
        );
    }
    
    public default A minimumBy (Comparator <A> comparator, A defaultValue) {
        return this.foldl (
            (res, x) -> comparator.compare (res, x) <= 0 ? res : x,
            defaultValue
        );
    }
    
    public default boolean none (Predicate <A> predicate) {
        return (! this.any (predicate));
    }
    
    public static boolean or (Foldable <Boolean> foldable) {
        return foldable.foldl (Boolean :: logicalOr, false);
    }
    
    // In Haskell, the function is named "length".
    public default int size () {
        return this.foldl ((res, x) -> res + 1, 0);
    }
    
    /**
     * Transform the object to a list.
     * 
     * @return List representation of this object.
     */
    public default FunctionalList <A> toList () {
        return this.toList (FunctionalList :: new);
    }
    
    public default <LA extends FunctionalList <A>> LA toList (Supplier <LA> constructor) {
        return this.foldl (
            (res, x) -> {
                res.add (x);
                return res;
            },
            constructor.get ()
        );
    }
}
