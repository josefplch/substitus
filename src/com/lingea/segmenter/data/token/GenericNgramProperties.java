package com.lingea.segmenter.data.token;

import com.github.josefplch.utils.data.math.fraction.LongFraction;
import com.github.josefplch.utils.data.tuple.Tuple3;
import com.github.josefplch.utils.data.tuple.UniformPair;

/**
 * Characteristics of token n-gram: TF (frequency), IWF (productivity), and some
 * usage examples.
 * 
 * @author  Josef Plch
 * @since   2019-11-21
 * @version 2021-01-12
 */
public abstract class GenericNgramProperties <T> extends Tuple3 <LongFraction, LongFraction, UniformPair <T>> {
    public GenericNgramProperties (LongFraction tf, LongFraction iwf, UniformPair <T> examples) {
        super (tf, iwf, examples);
    }
    
    private double fixRatio (LongFraction fraction) {
        return (reliability (e2.getDenominator ()) * fraction.doubleValue ());
    }
    
    public UniformPair <T> getExamples () {
        return e3;
    }
    
    public Double getFixedIwfRatio () {
        return fixRatio (e2);
    }
    
    public Double getFixedTfRatio () {
        return fixRatio (e1);
    }
    
    public LongFraction getIwf () {
        return e2;
    }
    
    public Long getIwfCount () {
        return e2.getNumerator ();
    }
    
    public Double getIwfRatio () {
        return e2.doubleValue ();
    }
    
    public LongFraction getTf () {
        return e1;
    }
    
    public Long getTfCount () {
        return e1.getNumerator ();
    }
    
    public Double getTfRatio () {
        return e1.doubleValue ();
    }
    
    // Idea: The more occurences, the more reliable the information is.
    // E.g. 500/1000 it much more reliable than 1/2.
    // f = 1 ...... 0.60
    // f = 2 ...... 0.72
    // f = 10 ..... 0.87
    // f = 100 .... 0.96
    // f = 1000 ... 0.99
    // For details, see the file "analysis_of_reliability".
    private static double reliability (long denominator) {
        return (1 - 0.4 / Math.sqrt (denominator));
    }
}
