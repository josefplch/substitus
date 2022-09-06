package com.github.josefplch.utils.data;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author  Josef Plch
 * @since   2015-06-14
 * @version 2020-11-12
 */
public abstract class ComparableUtils {
    private static <A, K, CK extends Comparator <? super K>> A genericMax (A x, A y, Function <A, K> keyExtractor, CK keyComparator, BiFunction <Integer, Integer, Boolean> comparisonType) {
        A result;
        if (x == null || y == null) {
            result = (x == null) ? y : x;
        }
        else {
            K keyX = keyExtractor.apply (x);
            K keyY = keyExtractor.apply (y);
            if (keyX == null || keyY == null) {
                result = (keyX == null) ? y : x;
            }
            else {
                result = comparisonType.apply (keyComparator.compare (keyY, keyX), 0) ? y : x;
            }
        }
        return result;
    }
    
    public static <A extends Comparable <A>> A max (A x, A y) {
        return ComparableUtils.maxComparing (x, y, Function.identity ());
    }
    
    public static <A, CA extends Comparator <? super A>> A maxBy (A x, A y, CA comparator) {
        return comparator.compare (x, y) >= 0 ? x : y;
    }
    
    public static <A, K extends Comparable <? super K>> A maxComparing (A x, A y, Function <A, K> keyExtractor) {
        return ComparableUtils.maxComparing (x, y, keyExtractor, Comparator.naturalOrder ());
    }
    
    public static <A, K> A maxComparing (A x, A y, Function <A, K> keyExtractor, Comparator <? super K> keyComparator) {
        return ComparableUtils.genericMax (x, y, keyExtractor, keyComparator, (n1, n2) -> n1 > n2);
    }
    
    public static <A extends Comparable <A>> A min (A x, A y) {
        return ComparableUtils.minComparing (x, y, Function.identity ());
    }
    
    public static <A, CA extends Comparator <? super A>> A minBy (A x, A y, CA comparator) {
        return comparator.compare (x, y) <= 0 ? x : y;
    }
    
    public static <A, K extends Comparable <? super K>> A minComparing (A x, A y, Function <A, K> keyExtractor) {
        return ComparableUtils.minComparing (x, y, keyExtractor, Comparator.naturalOrder ());
    }
    
    public static <A, K> A minComparing (A x, A y, Function <A, K> keyExtractor, Comparator <? super K> keyComparator) {
        return ComparableUtils.genericMax (x, y, keyExtractor, keyComparator, (n1, n2) -> n1 < n2);
    }
}
