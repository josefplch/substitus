package com.github.josefplch.utils.system;

import com.github.josefplch.utils.data.math.fraction.LongFraction;
import com.github.josefplch.utils.data.string.DoubleFormatter;

/**
 * @author  Josef Plch
 * @since   2021-01-14
 * @version 2021-01-14
 */
public abstract class MemoryUtils {
    private static LongFraction memoryUsage (long divisor) {
        System.gc ();
        Runtime runtime = Runtime.getRuntime ();
        long usedMemory = (runtime.totalMemory () - runtime.freeMemory ()) / divisor;
        long maxMemory = runtime.maxMemory () / divisor;
        return LongFraction.of (usedMemory, maxMemory);
    }
    
    public static LongFraction memoryUsageMB () {
        return memoryUsage (1024 * 1024);
    }
    
    public static String memoryUsageMessageMB () {
        LongFraction usage = memoryUsageMB ();
        return (
            usage.getNumerator () + " / " + usage.getDenominator () + " MB"
            + " (" + DoubleFormatter.POINT_1.format (100.0 * usage.doubleValue ()) + " %)"
        );
    }
}
