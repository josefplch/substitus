package com.github.josefplch.utils.data.tuple;

import com.github.josefplch.utils.data.Bifunctor;
import com.github.josefplch.utils.data.function.Function2;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Ordered pair, a tuple of two objects of mutually independent types.
 * 
 * Also known as: 2-tuple, couple, duad, dual, twin.
 * 
 * @param <T1> Type of the 1st element.
 * @param <T2> Type of the 2nd element.
 * 
 * @author  Josef Plch
 * @since   2015-04-28
 * @version 2020-12-28
 */
public class Pair <T1, T2> implements Bifunctor <T1, T2>, Serializable {
    protected T1 e1;
    protected T2 e2;
    
    /**
     * Compare two pairs using lexicographical ordering.
     * 
     * @param <T1> Type of the 1st element.
     * @param <T2> Type of the 2nd element.
     */
    public static class LexicographicalComparator <T1, T2> implements Comparator <Pair <T1, T2>> {
        private final Comparator <? super T1> comparator1;
        private final Comparator <? super T2> comparator2;
        
        private LexicographicalComparator (
            Comparator <? super T1> comparator1,
            Comparator <? super T2> comparator2
        ) {
            this.comparator1 = comparator1;
            this.comparator2 = comparator2;
        }
        
        public static <T1, T2> LexicographicalComparator <T1, T2> basedOn (
            Comparator <? super T1> comparator1,
            Comparator <? super T2> comparator2
        ) {
            return new LexicographicalComparator <> (
                comparator1,
                comparator2
            );
        }
        
        /**
         * Lifted comparator based on natural ordering. Both types must be
         * comparable.
         * 
         * @param <T1> Type of the 1st element must be comparable.
         * @param <T2> Type of the 2nd element must be comparable as well.
         * @return     Lexicographical comparator.
         */
        public static <
            T1 extends Comparable <T1>,
            T2 extends Comparable <T2>
        > LexicographicalComparator <T1, T2> natural () {
            return new LexicographicalComparator <> (
                T1 :: compareTo,
                T2 :: compareTo
            );
        }
        
        @Override
        public int compare (Pair <T1, T2> pairA, Pair <T1, T2> pairB) {
            int result;
            int comparison1 = comparator1.compare (pairA.get1 (), pairB.get1 ());
            if (comparison1 != 0) {
                result = comparison1;
            }
            else {
                result = comparator2.compare (pairA.get2 (), pairB.get2 ());
            }
            return result;
        }
    }
    
    protected Pair (T1 e1, T2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }
    
    // Shallow copy constructor.
    public Pair (Pair <T1, T2> other) {
        if (Objects.isNull (other)) {
            throw new NullPointerException ("Null pair");
        }
        else {
            this.e1 = other.e1;
            this.e2 = other.e2;
        }
    }
    
    public static <T1, T2> Pair <T1, T2> of (T1 e1, T2 e2) {
        return new Pair <> (e1, e2);
    }
    
    @Override
    public <R1, R2> Pair <R1, R2> bimap (Function <T1, R1> f1, Function <T2, R2> f2) {
        return Pair.of (f1.apply (e1), f2.apply (e2));
    }
    
    @Override
    public boolean equals (Object object) {
        Boolean result;
        if (object == null || ! (object instanceof Pair)) {
            result = false;
        }
        else {
            final Pair <?, ?> other = (Pair <?, ?>) object;
            result =
                Objects.equals    (this.e1, other.e1)
                && Objects.equals (this.e2, other.e2);
        }
        return result;
    }
    
    /**
     * @return Get the 1st element.
     */
    public T1 get1 () {
        return e1;
    }
    
    /**
     * @return Get the 2nd element.
     */
    public T2 get2 () {
        return e2;
    }
    
    @Override
    public int hashCode () {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode (this.e1);
        hash = 97 * hash + Objects.hashCode (this.e2);
        return hash;
    }
    
    // 2 = number of arguments.
    public static <
        A1, A2,
        B1, B2,
        R1, R2
    > Function2 <
        ? super Pair <A1, A2>,
        ? super Pair <B1, B2>,
        Pair <R1, R2>
    > lift2 (
        BiFunction <A1, B1, R1> f1,
        BiFunction <A2, B2, R2> f2
    ) {
        return ((a, b) ->
            Pair.of (
                f1.apply (a.e1, b.e1),
                f2.apply (a.e2, b.e2)
            )
        );
    }
    
    @Override
    public <A> Pair <A, T2> map1 (Function <T1, A> f) {
        return Pair.of (f.apply (e1), e2);
    }
    
    @Override
    public <A> Pair <T1, A> map2 (Function <T2, A> f) {
        return Pair.of (e1, f.apply (e2));
    }
    
    /**
     * Set new value of the 1st element.
     * 
     * @param element1 New element value.
     */
    public void set1 (T1 element1) {
        this.e1 = element1;
    }

    /**
     * Set new value of the 2nd element.
     * 
     * @param element2 New element value.
     */
    public void set2 (T2 element2) {
        this.e2 = element2;
    }
    
    /**
     * Create a new pair with swapped elements.
     * 
     * @return A new pair with swapped elements.
     */
    public Pair <T2, T1> swap () {
        return Pair.of (e2, e1);
    }
    
    @Override
    public String toString () {
        return ("(" + e1 + ", " + e2 + ")");
    }
}
