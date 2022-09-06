package com.github.josefplch.utils.data.math.fraction;

import java.math.BigInteger;

/**
 * @author  Josef Plch
 * @since   2019-05-10
 * @version 2021-01-11
 */
public class LongFraction extends AbstractFraction <Long> {
    protected LongFraction (Long numerator, Long denominator) {
        super (numerator, denominator);
    }
    
    public static LongFraction of (long numerator, long denominator) {
        return new LongFraction (numerator, denominator);
    }
    
    public static LongFraction valueOf (String string) throws NumberFormatException {
        String [] parts = string.split ("/");
        if (parts.length != 2) {
            throw new NumberFormatException ("Illegal fraction: " + string);
        }
        else {
            return new LongFraction (
                Long.valueOf (parts [0]),
                Long.valueOf (parts [1])
            );
        }
    }
    
    @Override
    public LongFraction cancel () {
        long gcd =
            BigInteger.valueOf (numerator)
            .gcd (BigInteger.valueOf (denominator))
            .longValue ();
        return new LongFraction (numerator / gcd, denominator / gcd);
    }

    @Override
    public LongFraction sum (AbstractFraction <Long> other) {
        return (
            new LongFraction (
                this.numerator * other.denominator + other.numerator * this.denominator,
                this.denominator * other.denominator
            )
            .cancel ()
        );
    }
    
    @Override
    public LongFraction sumElementWise (AbstractFraction <Long> other) {
        return new LongFraction (this.numerator + other.numerator, this.denominator + other.denominator);
    }
    
    @Override
    public LongFraction swap () {
        return new LongFraction (denominator, numerator);
    }
}
