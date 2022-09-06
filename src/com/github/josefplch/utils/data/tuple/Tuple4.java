package com.github.josefplch.utils.data.tuple;

import com.github.josefplch.utils.data.function.Function2;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Ordered quadruple, a tuple of four objects of mutually independent types.
 * 
 * Also known as: 4-tuple, quad, tetrad.
 * 
 * @param <T1> Type of the 1st element.
 * @param <T2> Type of the 2nd element.
 * @param <T3> Type of the 3rd element.
 * @param <T4> Type of the 4th element.
 * 
 * @author  Josef Plch
 * @since   2018-04-18
 * @version 2020-12-28
 */
public class Tuple4 <T1, T2, T3, T4> implements Serializable {
    protected T1 e1;
    protected T2 e2;
    protected T3 e3;
    protected T4 e4;
    
    /**
     * Compare two 4-tuples using lexicographical ordering.
     * 
     * @param <T1> Type of the 1st element.
     * @param <T2> Type of the 2nd element.
     * @param <T3> Type of the 3rd element.
     * @param <T4> Type of the 4th element.
     */
    public static class LexicographicalComparator <T1, T2, T3, T4> implements Comparator <Tuple4 <T1, T2, T3, T4>> {
        private final Comparator <? super T1> comparator1;
        private final Comparator <? super T2> comparator2;
        private final Comparator <? super T3> comparator3;
        private final Comparator <? super T4> comparator4;
        
        private LexicographicalComparator (
            Comparator <? super T1> comparator1,
            Comparator <? super T2> comparator2,
            Comparator <? super T3> comparator3,
            Comparator <? super T4> comparator4
        ) {
            this.comparator1 = comparator1;
            this.comparator2 = comparator2;
            this.comparator3 = comparator3;
            this.comparator4 = comparator4;
        }
        
        public static <T1, T2, T3, T4> LexicographicalComparator <T1, T2, T3, T4> basedOn (
            Comparator <? super T1> comparator1,
            Comparator <? super T2> comparator2,
            Comparator <? super T3> comparator3,
            Comparator <? super T4> comparator4
        ) {
            return new LexicographicalComparator <> (
                comparator1,
                comparator2,
                comparator3,
                comparator4
            );
        }
        
        /**
         * Lifted comparator based on natural ordering. All types must be
         * comparable.
         * 
         * @param <T1> Type of the 1st element must be comparable.
         * @param <T2> Type of the 2nd element must be comparable as well.
         * @param <T3> Type of the 3rd element must be comparable as well.
         * @param <T4> Type of the 4th element must be comparable as well.
         * @return     Lexicographical comparator.
         */
        public static <
            T1 extends Comparable <T1>,
            T2 extends Comparable <T2>,
            T3 extends Comparable <T3>,
            T4 extends Comparable <T4>
        > LexicographicalComparator <T1, T2, T3, T4> natural () {
            return new LexicographicalComparator <> (
                T1 :: compareTo,
                T2 :: compareTo,
                T3 :: compareTo,
                T4 :: compareTo
            );
        }
        
        @Override
        public int compare (
            Tuple4 <T1, T2, T3, T4> tupleA,
            Tuple4 <T1, T2, T3, T4> tupleB
        ) {
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
                    int comparison3 = comparator3.compare (tupleA.get3 (), tupleB.get3 ());
                    if (comparison3 != 0) {
                        result = comparison3;
                    }
                    else {
                        result = comparator4.compare (tupleA.get4 (), tupleB.get4 ());
                    }
                }
            }
            return result;
        }
    }
    
    protected Tuple4 (T1 e1, T2 e2, T3 e3, T4 e4) {
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
        this.e4 = e4;
    }
    
    public static <T1, T2, T3, T4> Tuple4 <T1, T2, T3, T4> of (T1 e1, T2 e2, T3 e3, T4 e4) {
        return new Tuple4 <> (e1, e2, e3, e4);
    }
    
    @Override
    public boolean equals (Object object) {
        Boolean result;
        if (object == null || ! (object instanceof Tuple4)) {
            result = false;
        }
        else {
            final Tuple4 <?, ?, ?, ?> other = (Tuple4 <?, ?, ?, ?>) object;
            result =
                Objects.equals    (this.e1, other.e1)
                && Objects.equals (this.e2, other.e2)
                && Objects.equals (this.e3, other.e3)
                && Objects.equals (this.e4, other.e4);
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
    
    /**
     * @return The 4th element of the tuple.
     */
    public T4 get4 () {
        return e4;
    }
    
    @Override
    public int hashCode () {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode (this.e1);
        hash = 97 * hash + Objects.hashCode (this.e2);
        hash = 97 * hash + Objects.hashCode (this.e3);
        hash = 97 * hash + Objects.hashCode (this.e4);
        return hash;
    }
    
    // 2 = number of arguments.
    public static <
        A1, A2, A3, A4,
        B1, B2, B3, B4,
        R1, R2, R3, R4
    > Function2 <
        ? super Tuple4 <A1, A2, A3, A4>,
        ? super Tuple4 <B1, B2, B3, B4>,
        Tuple4 <R1, R2, R3, R4>
    > lift2 (
        BiFunction <A1, B1, R1> f1,
        BiFunction <A2, B2, R2> f2,
        BiFunction <A3, B3, R3> f3,
        BiFunction <A4, B4, R4> f4
    ) {
        return ((a, b) ->
            Tuple4.of (
                f1.apply (a.e1, b.e1),
                f2.apply (a.e2, b.e2),
                f3.apply (a.e3, b.e3),
                f4.apply (a.e4, b.e4)
            )
        );
    }
    
    public <A> Tuple4 <A, T2, T3, T4> map1 (Function <T1, A> f) {
        return Tuple4.of (f.apply (e1), e2, e3, e4);
    }
    
    public <A> Tuple4 <T1, A, T3, T4> map2 (Function <T2, A> f) {
        return Tuple4.of (e1, f.apply (e2), e3, e4);
    }
    
    public <A> Tuple4 <T1, T2, A, T4> map3 (Function <T3, A> f) {
        return Tuple4.of (e1, e2, f.apply (e3), e4);
    }
    
    public <A> Tuple4 <T1, T2, T3, A> map4 (Function <T4, A> f) {
        return Tuple4.of (e1, e2, e3, f.apply (e4));
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
    
    /**
     * Set new value of the 4th element.
     * 
     * @param element4 New element value.
     */
    public void set4 (T4 element4) {
        this.e4 = element4;
    }
    
    @Override
    public String toString () {
        return ("(" + e1 + ", " + e2 + ", " + e3 + ", " + e4 + ")");
    }
}
