package com.lingea.segmenter.bpe.metric;

import java.util.Objects;

/**
 * See: https://www.fi.muni.cz/~pary/ib047/p6.pdf
 * 
 * @author  Josef Plch
 * @since   2018-05-07
 * @version 2018-06-11
 */
public class MiScore implements AssociationMetric {
    @Override
    public double apply (int lx, int ly, long fx, long fy, long fxy) {
        return ((double) fxy) / (fx * fy);
    }
    
    @Override
    public int hashCode () {
        return 156640076;
    }

    @Override
    public boolean equals (Object object) {
        return (Objects.nonNull (object) && (object instanceof MiScore));
    }
    
    @Override
    public String toString () {
        return "MiScore";
    }
}
