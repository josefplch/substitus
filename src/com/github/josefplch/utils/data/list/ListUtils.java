package com.github.josefplch.utils.data.list;

import com.github.josefplch.utils.data.function.Function3;
import com.github.josefplch.utils.data.function.Function4;
import com.github.josefplch.utils.data.function.Function5;
import com.github.josefplch.utils.data.list.number.IntegerList;
import com.github.josefplch.utils.data.tuple.Tuple3;
import com.github.josefplch.utils.data.tuple.Tuple4;
import com.github.josefplch.utils.data.tuple.Tuple5;
import com.github.josefplch.utils.data.tuple.Tuple6;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Some Haskell-like functional operations for lists.
 * 
 * @author  Josef Plch
 * @since   2015-05-14
 * @version 2022-01-20
 */
public abstract class ListUtils {
    public static <A> FunctionalList <FunctionalList <A>> cartesianProduct (
        List <FunctionalList <A>> lists
    ) {
        return ListUtils.cartesianProduct (lists, FunctionalList :: new);
    }
    
    public static <A, LA extends List <A>> FunctionalList <LA> cartesianProduct (
        List <LA> lists,
        Supplier <LA> constructor
    ) {
        FunctionalList <LA> result = FunctionalList.of (constructor.get ());
        for (List <A> list : lists) {
            result =
                result.flatMap (resultPrefix ->
                    FunctionalList.from (list).map (x -> {
                        LA prefixCopy = constructor.get ();
                        prefixCopy.addAll (resultPrefix);
                        prefixCopy.add (x);
                        return prefixCopy;
                    })
                );
        }
        return result;
    }
    
    // For binary cartesian producet, see PairList.cartesianProduct.
    
    public static <T1, T2, T3> FunctionalList <Tuple3 <T1, T2, T3>> cartesianProduct (
        List <T1> list1,
        List <T2> list2,
        List <T3> list3
    ) {
        FunctionalList <Tuple3 <T1, T2, T3>> result = new FunctionalList <> ();
        for (T1 e1 : list1) {
            for (T2 e2 : list2) {
                for (T3 e3 : list3) {
                    result.add (Tuple3.of (e1, e2, e3));
                }
            }
        }
        return result;
    }
    
    public static <T1, T2, T3, T4> FunctionalList <Tuple4 <T1, T2, T3, T4>> cartesianProduct (
        List <T1> list1,
        List <T2> list2,
        List <T3> list3,
        List <T4> list4
    ) {
        FunctionalList <Tuple4 <T1, T2, T3, T4>> result = new FunctionalList <> ();
        for (T1 e1 : list1) {
            for (T2 e2 : list2) {
                for (T3 e3 : list3) {
                    for (T4 e4 : list4) {
                        result.add (Tuple4.of (e1, e2, e3, e4));
                    }
                }
            }
        }
        return result;
    }
    
    public static <T1, T2, T3, T4, T5> FunctionalList <Tuple5 <T1, T2, T3, T4, T5>> cartesianProduct (
        List <T1> list1,
        List <T2> list2,
        List <T3> list3,
        List <T4> list4,
        List <T5> list5
    ) {
        FunctionalList <Tuple5 <T1, T2, T3, T4, T5>> result = new FunctionalList <> ();
        for (T1 e1 : list1) {
            for (T2 e2 : list2) {
                for (T3 e3 : list3) {
                    for (T4 e4 : list4) {
                        for (T5 e5 : list5) {
                            result.add (Tuple5.of (e1, e2, e3, e4, e5));
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public static <T1, T2, T3, T4, T5, T6> FunctionalList <Tuple6 <T1, T2, T3, T4, T5, T6>> cartesianProduct (
        List <T1> list1,
        List <T2> list2,
        List <T3> list3,
        List <T4> list4,
        List <T5> list5,
        List <T6> list6
    ) {
        FunctionalList <Tuple6 <T1, T2, T3, T4, T5, T6>> result = new FunctionalList <> ();
        for (T1 e1 : list1) {
            for (T2 e2 : list2) {
                for (T3 e3 : list3) {
                    for (T4 e4 : list4) {
                        for (T5 e5 : list5) {
                            for (T6 e6 : list6) {
                                result.add (Tuple6.of (e1, e2, e3, e4, e5, e6));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public static <T1, T2, T3> FunctionalList <Tuple3 <T1, T2, T3>> zip3 (
        List <T1> list1,
        List <T2> list2,
        List <T3> list3
    ) {
        return ListUtils.zipWith3 (Tuple3 :: of, list1, list2, list3);
    }
    
    public static <T1, T2, T3, T4> FunctionalList <Tuple4 <T1, T2, T3, T4>> zip4 (
        List <T1> list1,
        List <T2> list2,
        List <T3> list3,
        List <T4> list4
    ) {
        return ListUtils.zipWith4 (Tuple4 :: of, list1, list2, list3, list4);
    }
    
    public static <T1, T2, T3> FunctionalList <T3> zipWith (
        BiFunction <T1, T2, T3> f,
        List <T1> list1,
        List <T2> list2
    ) {
        return ListUtils.zipWith (f, list1, list2, FunctionalList :: new);
    }
    
    public static <T1, T2, R, LR extends FunctionalList <R>> LR zipWith (
        BiFunction <T1, T2, R> f,
        List <T1> list1,
        List <T2> list2,
        Supplier <LR> constructor
    ) {
        LR result = constructor.get ();
        int resultLength = Math.min (list1.size (), list2.size ());
        for (int i = 0; i < resultLength; i++) {
            result.add (f.apply (list1.get (i), list2.get (i)));
        }
        return result;
    }
    
    public static <T1, T2, T3, R> FunctionalList <R> zipWith3 (
        Function3 <T1, T2, T3, R> f,
        List <T1> list1,
        List <T2> list2,
        List <T3> list3
    ) {
        return zipWith3 (f, list1, list2, list3, FunctionalList :: new);
    }
    
    public static <T1, T2, T3, R, LR extends FunctionalList <R>> LR zipWith3 (
        Function3 <T1, T2, T3, R> f,
        List <T1> list1,
        List <T2> list2,
        List <T3> list3,
        Supplier <LR> constructor
    ) {
        LR result = constructor.get ();
        int resultLength = IntegerList.ofNumbers (list1.size (), list2.size (), list3.size ()).minimum (0);
        for (int i = 0; i < resultLength; i++) {
            result.add (f.apply (list1.get (i), list2.get (i), list3.get (i)));
        }
        return result;
    }
    
    public static <T1, T2, T3, T4, R> FunctionalList <R> zipWith4 (
        Function4 <T1, T2, T3, T4, R> f,
        List <T1> list1,
        List <T2> list2,
        List <T3> list3,
        List <T4> list4
    ) {
        return zipWith4 (f, list1, list2, list3, list4, FunctionalList :: new);
    }
    
    public static <T1, T2, T3, T4, R, LR extends FunctionalList <R>> LR zipWith4 (
        Function4 <T1, T2, T3, T4, R> f,
        List <T1> list1,
        List <T2> list2,
        List <T3> list3,
        List <T4> list4,
        Supplier <LR> constructor
    ) {
        LR result = constructor.get ();
        int resultLength = IntegerList.ofNumbers (list1.size (), list2.size (), list3.size (), list4.size ()).minimum (0);
        for (int i = 0; i < resultLength; i++) {
            result.add (f.apply (list1.get (i), list2.get (i), list3.get (i), list4.get (i)));
        }
        return result;
    }
    
    public static <T1, T2, T3, T4, T5, R> FunctionalList <R> zipWith5 (
        Function5 <T1, T2, T3, T4, T5, R> f,
        List <T1> list1,
        List <T2> list2,
        List <T3> list3,
        List <T4> list4,
        List <T5> list5
    ) {
        return zipWith5 (f, list1, list2, list3, list4, list5, FunctionalList :: new);
    }
    
    public static <T1, T2, T3, T4, T5, R, LR extends FunctionalList <R>> LR zipWith5 (
        Function5 <T1, T2, T3, T4, T5, R> f,
        List <T1> list1,
        List <T2> list2,
        List <T3> list3,
        List <T4> list4,
        List <T5> list5,
        Supplier <LR> constructor
    ) {
        LR result = constructor.get ();
        int resultLength = IntegerList.ofNumbers (list1.size (), list2.size (), list3.size (), list4.size (), list5.size ()).minimum (0);
        for (int i = 0; i < resultLength; i++) {
            result.add (f.apply (list1.get (i), list2.get (i), list3.get (i), list4.get (i), list5.get (i)));
        }
        return result;
    }
}
