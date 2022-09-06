package com.github.josefplch.utils.data.tuple;

import com.github.josefplch.utils.data.function.Function2;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Ordered sextuple, a tuple of six objects of mutually independent types.
 * 
 * Also known as: 6-tuple, hextuple.
 * 
 * @param <T1> Type of the 1st element.
 * @param <T2> Type of the 2nd element.
 * @param <T3> Type of the 3rd element.
 * @param <T4> Type of the 4th element.
 * @param <T5> Type of the 5th element.
 * @param <T6> Type of the 6th element.
 * 
 * @author  Josef Plch
 * @since   2020-09-24
 * @version 2020-12-28
 */
public class Tuple6 <T1, T2, T3, T4, T5, T6> implements Serializable {
    protected T1 e1;
    protected T2 e2;
    protected T3 e3;
    protected T4 e4;
    protected T5 e5;
    protected T6 e6;
    
    /**
     * Compare two 6-tuples using lexicographical ordering.
     * 
     * @param <T1> Type of the 1st element.
     * @param <T2> Type of the 2nd element.
     * @param <T3> Type of the 3rd element.
     * @param <T4> Type of the 4th element.
     * @param <T5> Type of the 5th element.
     * @param <T6> Type of the 6th element.
     */
    public static class LexicographicalComparator <T1, T2, T3, T4, T5, T6> implements Comparator <Tuple6 <T1, T2, T3, T4, T5, T6>> {
        private final Comparator <? super T1> comparator1;
        private final Comparator <? super T2> comparator2;
        private final Comparator <? super T3> comparator3;
        private final Comparator <? super T4> comparator4;
        private final Comparator <? super T5> comparator5;
        private final Comparator <? super T6> comparator6;
        
        private LexicographicalComparator (
            Comparator <? super T1> comparator1,
            Comparator <? super T2> comparator2,
            Comparator <? super T3> comparator3,
            Comparator <? super T4> comparator4,
            Comparator <? super T5> comparator5,
            Comparator <? super T6> comparator6
        ) {
            this.comparator1 = comparator1;
            this.comparator2 = comparator2;
            this.comparator3 = comparator3;
            this.comparator4 = comparator4;
            this.comparator5 = comparator5;
            this.comparator6 = comparator6;
        }
        
        public static <T1, T2, T3, T4, T5, T6> LexicographicalComparator <T1, T2, T3, T4, T5, T6> basedOn (
            Comparator <? super T1> comparator1,
            Comparator <? super T2> comparator2,
            Comparator <? super T3> comparator3,
            Comparator <? super T4> comparator4,
            Comparator <? super T5> comparator5,
            Comparator <? super T6> comparator6
        ) {
            return new LexicographicalComparator <> (
                comparator1,
                comparator2,
                comparator3,
                comparator4,
                comparator5,
                comparator6
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
         * @param <T5> Type of the 5th element must be comparable as well.
         * @param <T6> Type of the 6th element must be comparable as well.
         * @return     Lexicographical comparator.
         */
        public static <
            T1 extends Comparable <T1>,
            T2 extends Comparable <T2>,
            T3 extends Comparable <T3>,
            T4 extends Comparable <T4>,
            T5 extends Comparable <T5>,
            T6 extends Comparable <T6>
        > LexicographicalComparator <T1, T2, T3, T4, T5, T6> natural () {
            return new LexicographicalComparator <> (
                T1 :: compareTo,
                T2 :: compareTo,
                T3 :: compareTo,
                T4 :: compareTo,
                T5 :: compareTo,
                T6 :: compareTo
            );
        }
        
        @Override
        public int compare (
            Tuple6 <T1, T2, T3, T4, T5, T6> tupleA,
            Tuple6 <T1, T2, T3, T4, T5, T6> tupleB
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
                        int comparison4 = comparator4.compare (tupleA.get4 (), tupleB.get4 ());
                        if (comparison4 != 0) {
                            result = comparison4;
                        }
                        else {
                            int comparison5 = comparator5.compare (tupleA.get5 (), tupleB.get5 ());
                            if (comparison5 != 0) {
                                result = comparison5;
                            }
                            else {
                                result = comparator6.compare (tupleA.get6 (), tupleB.get6 ());
                            }
                        }
                    }
                }
            }
            return result;
        }
    }
    
    protected Tuple6 (T1 e1, T2 e2, T3 e3, T4 e4, T5 e5, T6 e6) {
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
        this.e4 = e4;
        this.e5 = e5;
        this.e6 = e6;
    }
    
    public static <T1, T2, T3, T4, T5, T6> Tuple6 <T1, T2, T3, T4, T5, T6> of (T1 e1, T2 e2, T3 e3, T4 e4, T5 e5, T6 e6) {
        return new Tuple6 <> (e1, e2, e3, e4, e5, e6);
    }
    
    @Override
    public boolean equals (Object object) {
        Boolean result;
        if (object == null || ! (object instanceof Tuple6)) {
            result = false;
        }
        else {
            final Tuple6 <?, ?, ?, ?, ?, ?> other = (Tuple6 <?, ?, ?, ?, ?, ?>) object;
            result =
                Objects.equals    (this.e1, other.e1)
                && Objects.equals (this.e2, other.e2)
                && Objects.equals (this.e3, other.e3)
                && Objects.equals (this.e4, other.e4)
                && Objects.equals (this.e5, other.e5)
                && Objects.equals (this.e6, other.e6);
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
    
    /**
     * @return The 5th element of the tuple.
     */
    public T5 get5 () {
        return e5;
    }
    
    /**
     * @return The 6th element of the tuple.
     */
    public T6 get6 () {
        return e6;
    }
    
    @Override
    public int hashCode () {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode (this.e1);
        hash = 97 * hash + Objects.hashCode (this.e2);
        hash = 97 * hash + Objects.hashCode (this.e3);
        hash = 97 * hash + Objects.hashCode (this.e4);
        hash = 97 * hash + Objects.hashCode (this.e5);
        hash = 97 * hash + Objects.hashCode (this.e6);
        return hash;
    }
    
    // 2 = number of arguments.
    public static <
        A1, A2, A3, A4, A5, A6,
        B1, B2, B3, B4, B5, B6,
        R1, R2, R3, R4, R5, R6
    > Function2 <
        ? super Tuple6 <A1, A2, A3, A4, A5, A6>,
        ? super Tuple6 <B1, B2, B3, B4, B5, B6>,
        Tuple6 <R1, R2, R3, R4, R5, R6>
    > lift2 (
        BiFunction <A1, B1, R1> f1,
        BiFunction <A2, B2, R2> f2,
        BiFunction <A3, B3, R3> f3,
        BiFunction <A4, B4, R4> f4,
        BiFunction <A5, B5, R5> f5,
        BiFunction <A6, B6, R6> f6
    ) {
        return ((a, b) ->
            Tuple6.of (
                f1.apply (a.e1, b.e1),
                f2.apply (a.e2, b.e2),
                f3.apply (a.e3, b.e3),
                f4.apply (a.e4, b.e4),
                f5.apply (a.e5, b.e5),
                f6.apply (a.e6, b.e6)
            )
        );
    }

    public <A> Tuple6 <A, T2, T3, T4, T5, T6> map1 (Function <T1, A> f) {
        return Tuple6.of (f.apply (e1), e2, e3, e4, e5, e6);
    }
    
    public <A> Tuple6 <T1, A, T3, T4, T5, T6> map2 (Function <T2, A> f) {
        return Tuple6.of (e1, f.apply (e2), e3, e4, e5, e6);
    }
    
    public <A> Tuple6 <T1, T2, A, T4, T5, T6> map3 (Function <T3, A> f) {
        return Tuple6.of (e1, e2, f.apply (e3), e4, e5, e6);
    }
    
    public <A> Tuple6 <T1, T2, T3, A, T5, T6> map4 (Function <T4, A> f) {
        return Tuple6.of (e1, e2, e3, f.apply (e4), e5, e6);
    }
    
    public <A> Tuple6 <T1, T2, T3, T4, A, T6> map5 (Function <T5, A> f) {
        return Tuple6.of (e1, e2, e3, e4, f.apply (e5), e6);
    }
    
    public <A> Tuple6 <T1, T2, T3, T4, T5, A> map6 (Function <T6, A> f) {
        return Tuple6.of (e1, e2, e3, e4, e5, f.apply (e6));
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
    
    /**
     * Set new value of the 5th element.
     * 
     * @param element5 New element value.
     */
    public void set5 (T5 element5) {
        this.e5 = element5;
    }
    
    /**
     * Set new value of the 6th element.
     * 
     * @param element6 New element value.
     */
    public void set6 (T6 element6) {
        this.e6 = element6;
    }
    
    @Override
    public String toString () {
        return ("(" + e1 + ", " + e2 + ", " + e3 + ", " + e4 + ", " + e5 + ", " + e6 + ")");
    }
}
