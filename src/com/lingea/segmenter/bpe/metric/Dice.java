package com.lingea.segmenter.bpe.metric;

import java.util.Objects;

/**
 * See: https://www.fi.muni.cz/~pary/ib047/p6.pdf
 * 
 * @author  Josef Plch
 * @since   2018-05-08
 * @version 2018-06-11
 */
public class Dice implements AssociationMetric {
    @Override
    public double apply (int lx, int ly, long fx, long fy, long fxy) {
        return ((double) (2 * fxy)) / (fx + fy);
    }
    
    @Override
    public int hashCode () {
        return 559574706;
    }
    
    @Override
    public boolean equals (Object object) {
        return (Objects.nonNull (object) && (object instanceof Dice));
    }
    
    @Override
    public String toString () {
        return "Dice";
    }
}
