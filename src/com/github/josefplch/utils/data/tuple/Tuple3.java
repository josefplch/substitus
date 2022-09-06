package com.github.josefplch.utils.data.tuple;

import com.github.josefplch.utils.data.function.Function2;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Ordered triple, a tuple of three objects of mutually independent types.
 * 
 * Also known as: 3-tuple, treble, triad, triplet.
 * 
 * @param <T1> Type of the 1st element.
 * @param <T2> Type of the 2nd element.
 * @param <T3> Type of the 3rd element.
 * 
 * @author  Josef Plch
 * @since   2015-05-11
 * @version 2020-12-28
 */
public class Tuple3 <T1, T2, T3> implements Serializable {
    protected T1 e1;
    protected T2 e2;
    protected T3 e3;
    
    /**
     * Compare two 3-tuples using lexicographical ordering.
     * 
     * @param <T1> Type of the 1st element.
     * @param <T2> Type of the 2nd element.
     * @param <T3> Type of the 3rd element.
     */
    public static class LexicographicalComparator <T1, T2, T3> implements Comparator <Tuple3 <T1, T2, T3>> {
        private final Comparator <? super T1> comparator1;
        private final Comparator <? super T2> comparator2;
        private final Comparator <? super T3> comparator3;
        
        private LexicographicalComparator (
            Comparator <? super T1> comparator1,
            Comparator <? super T2> comparator2,
            Comparator <? super T3> comparator3
        ) {
            this.comparator1 = comparator1;
            this.comparator2 = comparator2;
            this.comparator3 = comparator3;
        }
        
        public static <T1, T2, T3> LexicographicalComparator <T1, T2, T3> basedOn (
            Comparator <? super T1> comparator1,
            Comparator <? super T2> comparator2,
            Comparator <? super T3> comparator3
        ) {
            return new LexicographicalComparator <> (
                comparator1,
                comparator2,
                comparator3
            );
        }
        
        /**
         * Lifted comparator based on natural ordering. All types must be
         * comparable.
         * 
         * @param <T1> Type of the 1st element must be comparable.
         * @param <T2> Type of the 2nd element must be comparable as well.
         * @param <T3> Type of the 3rd element must be comparable as well.
         * @return     Lexicographical comparator.
         */
        public static <
            T1 extends Comparable <T1>,
            T2 extends Comparable <T2>,
            T3 extends Comparable <T3>
        > LexicographicalComparator <T1, T2, T3> natural () {
            return new LexicographicalComparator <> (
                T1 :: compareTo,
                T2 :: compareTo,
                T3 :: compareTo
            );
        }
        
        @Override
        public int compare (Tuple3 <T1, T2, T3> tupleA, Tuple3 <T1, T2, T3> tupleB) {
            int result;
            int comparison1 = comparator1.compare (tupleA.get1 (), tupleB.get1 ());
            if (comparison1 != 0) {
                result = comparison1;
            }
            else {
                int comparison2 = comparator2.compare (tupleA.get2 (), tupleB.get2 ());
                if (comparison2 != 0) {
                    result = comparison2;
                }
                else {
                    result = comparator3.compare (tupleA.get3 (), tupleB.get3 ());
                }
            }
            return result;
        }
    }
    
    protected Tuple3 (T1 e1, T2 e2, T3 e3) {
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }
    
    public static <T1, T2, T3> Tuple3 <T1, T2, T3> of (T1 e1, T2 e2, T3 e3) {
        return new Tuple3 <> (e1, e2, e3);
    }
    
    @Override
    public boolean equals (Object object) {
        Boolean result;
        if (object == null || ! (object instanceof Tuple3)) {
            result = false;
        }
        else {
            final Tuple3 <?, ?, ?> other = (Tuple3 <?, ?, ?>) object;
            result =
                Objects.equals    (this.e1, other.e1)
                && Objects.equals (this.e2, other.e2)
                && Objects.equals (this.e3, other.e3);
        }
        return result;
    }
    
    /**
     * @return The 1st element of the tuple.
     */
    public T1 get1 () {
        return e1;
    }
    
    /**
     * @return The 2nd element of the tuple.
     */
    public T2 get2 () {
        return e2;
    }

    /**
     * @return The 3rd element of the tuple.
     */
    public T3 get3 () {
        return e3;
    }
    
    @Override
    public int hashCode () {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode (this.e1);
        hash = 97 * hash + Objects.hashCode (this.e2);
        hash = 97 * hash + Objects.hashCode (this.e3);
        return hash;
    }
    
    // 2 = number of arguments.
    public static <
        A1, A2, A3,
        B1, B2, B3,
        R1, R2, R3
    > Function2 <
        ? super Tuple3 <A1, A2, A3>,
        ? super Tuple3 <B1, B2, B3>,
        Tuple3 <R1, R2, R3>
    > lift2 (
        BiFunction <A1, B1, R1> f1,
        BiFunction <A2, B2, R2> f2,
        BiFunction <A3, B3, R3> f3
    ) {
        return ((a, b) ->
            Tuple3.of (
                f1.apply (a.e1, b.e1),
                f2.apply (a.e2, b.e2),
                f3.apply (a.e3, b.e3)
            )
        );
    }
    
    public <A> Tuple3 <A, T2, T3> map1 (Function <T1, A> f) {
        return Tuple3.of (f.apply (e1), e2, e3);
    }
    
    public <A> Tuple3 <T1, A, T3> map2 (Function <T2, A> f) {
        return Tuple3.of (e1, f.apply (e2), e3);
    }
    
    public <A> Tuple3 <T1, T2, A> map3 (Function <T3, A> f) {
        return Tuple3.of (e1, e2, f.apply (e3));
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
     * Set new value of the 3rd element.
     * 
     * @param element3 New element value.
     */
    public void set3 (T3 element3) {
        this.e3 = element3;
    }
    
    @Override
    public String toString () {
        return ("(" + e1 + ", " + e2 + ", " + e3 + ")");
    }
}
