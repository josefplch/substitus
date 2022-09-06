package com.github.josefplch.utils.data.math;

/**
 * @author  Josef Plch
 * @since   2018-05-26
 * @version 2021-01-08
 */
public abstract class NumberUtils {
    public static int signum (double number) {
        return (number > 0.0 ? 1 : (number == 0.0 ? 0 : -1));
    }
    
    public static int signum (float number) {
        return (number > 0.0F ? 1 : (number == 0.0F ? 0 : -1));
    }
    
    public static int signum (int number) {
        return (number > 0 ? 1 : (number == 0 ? 0 : -1));
    }
    
    public static int signum (long number) {
        return (number > 0L ? 1 : (number == 0L ? 0 : -1));
    }
}
