package com.lingea.segmenter.bpe.metric;

import java.util.Objects;

/**
 * @author  Josef Plch
 * @since   2018-05-08
 * @version 2018-06-11
 */
public class ModifiedMiScore implements AssociationMetric {
    private final AssociationMetric miScoreMetric = new MiScore ();
    
    @Override
    public double apply (int lx, int ly, long fx, long fy, long fxy) {
        double miScore = miScoreMetric.apply (lx, ly, fx, fy, fxy);
        return (fxy * miScore);
    }
    
    @Override
    public int hashCode () {
        return 382120846;
    }
    
    @Override
    public boolean equals (Object object) {
        return (Objects.nonNull (object) && (object instanceof ModifiedMiScore));
    }
    
    @Override
    public String toString () {
        return "ModifiedMiScore";
    }
}
