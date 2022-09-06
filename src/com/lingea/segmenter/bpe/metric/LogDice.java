package com.lingea.segmenter.bpe.metric;

import java.util.Objects;

/**
 * See: https://www.fi.muni.cz/~pary/ib047/p6.pdf
 * 
 * @author  Josef Plch
 * @since   2018-05-04
 * @version 2018-06-11
 */
public class LogDice implements AssociationMetric {
    private final AssociationMetric diceMetric = new Dice ();
    
    @Override
    public double apply (int lx, int ly, long fx, long fy, long fxy) {
        double dice = diceMetric.apply (lx, ly, fx, fy, fxy);
        return (14 + Math.log (dice));
    }

    @Override
    public int hashCode () {
        return 1964776022;
    }

    @Override
    public boolean equals (Object object) {
        return (Objects.nonNull (object) && (object instanceof LogDice));
    }
    
    @Override
    public String toString () {
        return "LogDice";
    }
}
