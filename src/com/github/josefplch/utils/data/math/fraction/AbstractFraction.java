package com.github.josefplch.utils.data.math.fraction;

import java.util.Objects;

/**
 * @param <I> Integral type.
 * 
 * @author  Josef Plch
 * @since   2019-05-10
 * @version 2021-01-11
 */
public abstract class AbstractFraction <I extends Number> extends Number implements Comparable <AbstractFraction <I>> {
    protected I numerator;
    protected I denominator;
    
    /**
     * Create a new fraction.
     * 
     * @param numerator   Fraction numerator.
     * @param denominator Fraction denominator.
     */
    public AbstractFraction (I numerator, I denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }
    
    /**
     * Cancel / simplify the fraction.
     * 
     * @return Cancelled fraction.
     */
    public abstract AbstractFraction <I> cancel ();
    
    @Override
    public int compareTo (AbstractFraction <I> other) {
        return Double.compare (this.doubleValue (), other.doubleValue ());
    }
    
    @Override
    public double doubleValue () {
        return (numerator.doubleValue () / denominator.doubleValue ());
    }
    
    @Override
    public boolean equals (Object object) {
        Boolean result;
        if (object == null || ! (object instanceof AbstractFraction)) {
            result = false;
        }
        else {
            final AbstractFraction <?> other = ((AbstractFraction <?>) object).cancel ();
            final AbstractFraction <I> cancelled = this.cancel ();
            result =
                Objects.equals    (cancelled.numerator,   other.numerator)
                && Objects.equals (cancelled.denominator, other.denominator);
        }
        return result;
    }
    
    @Override
    public float floatValue () {
        return (numerator.floatValue () / denominator.floatValue ());
    }
    
    public I getDenominator () {
        return denominator;
    }
    
    public I getNumerator () {
        return numerator;
    }
    
    @Override
    public int hashCode () {
        AbstractFraction <I> cancelled = this.cancel ();
        int hash = 3;
        hash = 97 * hash + Objects.hashCode (cancelled.numerator);
        hash = 97 * hash + Objects.hashCode (cancelled.denominator);
        return hash;
    }
    
    @Override
    public int intValue () {
        return (numerator.intValue () / denominator.intValue ());
    }
    
    @Override
    public long longValue () {
        return (numerator.longValue () / denominator.longValue ());
    }
    
    public void setDenominator (I denominator) {
        this.denominator = denominator;
    }
    
    public void setNumerator (I numerator) {
        this.numerator = numerator;
    }
    
    public abstract AbstractFraction <I> sum (AbstractFraction <I> other);
    
    public abstract AbstractFraction <I> sumElementWise (AbstractFraction <I> other);
    
    public abstract AbstractFraction <I> swap ();
    
    @Override
    public String toString () {
        return (numerator + "/" + denominator);
    }
}
