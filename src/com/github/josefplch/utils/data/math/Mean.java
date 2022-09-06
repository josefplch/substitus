package com.github.josefplch.utils.data.math;

import java.util.Arrays;
import java.util.List;

/**
 * For two values, these inequalities hold:
 * Min <= HarM <= GeoM <= LogM <= AriM <= QuaM <= ConM <= Max
 * 
 * See:
 * https://en.wikipedia.org/wiki/Average
 * https://en.wikipedia.org/wiki/Mean
 * https://en.wikipedia.org/wiki/Generalized_mean
 * 
 * @author  Josef Plch
 * @since   2018-07-24
 * @version 2018-10-24
 */
public abstract class Mean {
    @SafeVarargs
    public static <A extends Number> double arithmeticMean (A ... xs) {
        return arithmeticMean (Arrays.asList (xs));
    }
    
    /**
     * Standard mean: (x1 + x2 + ... + xn) / n. Equivalent to powerMean (xs, 1).
     * 
     * @param <A> Type of input numbers.
     * @param xs  The numbers to compute the mean for.
     * @return    Arithmetic mean of the given numbers.
     */
    public static <A extends Number> double arithmeticMean (List <A> xs) {
        return powerMean (1, xs);
    }
    
    // Only for non-negative numbers!
    // The contraharmonic mean is a special case of the Lehmer mean where p = 2.
    public static double contraharmonicMean (double x, double y) {
        return ((x == y) ? x : (Math.pow (x, 2) + Math.pow (y, 2)) / (x + y));
    }
    
    @SafeVarargs
    public static <A extends Number> double cubicMean (A ... xs) {
        return cubicMean (Arrays.asList (xs));
    }
    
    /**
     * Special case of power mean (p = 3).
     * 
     * @param <A> Type of input numbers.
     * @param xs  The numbers to compute the mean for.
     * @return    Cubic mean of the given numbers.
     */
    public static <A extends Number> double cubicMean (List <A> xs) {
        return powerMean (3, xs);
    }
    
    @SafeVarargs
    public static <A extends Number> double geometricMean (A ... xs) {
        return geometricMean (Arrays.asList (xs));
    }
    
    /**
     * Geometric mean (x, y) = SQRT (x * y).
     * 
     * @param <A> Type of input numbers.
     * @param xs  The numbers to compute the mean for.
     * @return Geometric mean of the given numbers.
     */
    public static <A extends Number> double geometricMean (List <A> xs) {
        // We compute in log domain to avoid overflow/underflow.
        double sumOfLogs = 0.0;
        boolean anyIsZero = false;
        for (A x : xs) {
            // Beware of zero values.
            double value = x.doubleValue ();
            if (value < 0) {
                throw new IllegalArgumentException (
                    "Mean.geometricMean: Negative numbers are not allowed"
                    + " (" + x + ")."
                );
            }
            else if (value == 0) {
                anyIsZero = true;
            }
            else {
                sumOfLogs += Math.log (value);
            }
        }
        return (anyIsZero ? 0 : Math.exp (sumOfLogs / xs.size ()));
    }
    
    @SafeVarargs
    public static <A extends Number> double harmonicMean (A ... xs) {
        return harmonicMean (Arrays.asList (xs));
    }
    
    // Special case: harmonicMean (x, y) = 2 * x * y / (x + y).
    // Only for non-negative numbers!
    public static <A extends Number> double harmonicMean (List <A> xs) {
        return powerMean (-1, xs);
    }
    
    public static double logarithmicMean (double x, double y) {
        double result;
        if (x == 0 || y == 0) {
            result = 0;
        }
        else {
            double logX = Math.log (x);
            double logY = Math.log (y);
            
            // It is not sufficient to check equality of x and y, because for
            // very similar numbers, logarithm may be the same even if x and y
            // are not.
            if (logX == logY) {
                result = x;
            }
            else {
                result = (x - y) / (Math.log (x) - Math.log (y));
            }
        }
        return result;
    }
    
    /**
     * Also known as generalized mean. Special cases:
     * - harmonic mean (p = 0)
     * - arithmetic mean (p = 1)
     * - qudratic mean (p = 2)
     * - cubic mean (p = 3)
     * 
     * @param <A> Type of input numbers.
     * @param p   Power.
     * @param xs  The numbers to compute the mean for.
     * @return    Generalized mean of the given numbers.
     */
    public static <A extends Number> double powerMean (double p, List <A> xs) {
        if (p == 0) {
            throw new IllegalArgumentException (
                "Mean.powerMean: p must be non-zero. For zero p, use"
                + " geometricMean instead."
            );
        }
        double sum = 0;
        for (A x : xs) {
            sum += Math.pow (x.doubleValue (), p);
        }
        return Math.pow (sum / xs.size (), 1 / p);
    }
    
    // Aka root mean square. Special case of power mean (p = 2).
    @SafeVarargs
    public static <A extends Number> double quadraticMean (A ... xs) {
        return quadraticMean (Arrays.asList (xs));
    }
    
    /**
     * Also known as root mean square. Special case of power mean (p = 2).
     * 
     * @param <A> Type of input numbers.
     * @param xs  The numbers to compute the mean for.
     * @return    Quadratic mean of the given numbers.
     */
    public static <A extends Number> double quadraticMean (List <A> xs) {
        return powerMean (2, xs);
    }
}
