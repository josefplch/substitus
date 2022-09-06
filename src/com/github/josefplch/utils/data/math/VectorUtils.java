package com.github.josefplch.utils.data.math;

import java.util.List;

/**
 * @author  Josef Plch
 * @since   2018-05-16
 * @version 2018-09-20
 */
public abstract class VectorUtils {
    public static <A extends Number> double angleInDegrees (List <A> u, List <A> v) throws ArithmeticException {
        return radToDeg (angleInRadians (u, v));
    }
    
    // TODO: Due to rounding, a very small angle can be returned even for identical vectors.
    public static <A extends Number> double angleInRadians (List <A> u, List <A> v) throws ArithmeticException {
        double sizeU = size (u);
        double sizeV = size (v);
        if (sizeU == 0 || sizeV == 0) {
            throw new ArithmeticException ("It is not possible to compute an angle for null vector.");
        }
        else {
            double cosAlpha = dotProduct (u, v) / (sizeU * sizeV);
            
            // Due to rounding, the cosinus may be higher than 1.
            // Example: cos ([5, 2], [10, 4]) == 1.0000000000000002
            cosAlpha = Math.min (cosAlpha, 1.0);
            
            return Math.acos (cosAlpha);
        }
    }
    
    public static <A extends Number> double dotProduct (List <A> u, List <A> v) {
        if (u.size () != v.size ()) {
            throw new IllegalArgumentException (
                "Vectors differ in size (" + u.size () + ", " + v.size () + ")."
            );
        }
        else {
            double result = 0;
            for (int i = 0; i < u.size (); i++) {
                result += u.get (i).doubleValue () * v.get (i).doubleValue ();
            }
            return result;
        }
    }
    
    public static double radToDeg (double alpha) {
        return (alpha * 180 / Math.PI);
    }
    
    public static <N extends Number> double size (List <N> u) {
        double sum = 0;
        for (Number uk : u) {
            double ukValue = uk.doubleValue ();
            sum += ukValue * ukValue;
        }
        return Math.sqrt (sum);
    }
}
