package com.lingea.segmenter.bpe.metric;

import java.util.Objects;

/**
 * @author  Josef Plch
 * @since   2018-05-04
 * @version 2018-06-11
 */
public class CommonFrequency implements AssociationMetric {
    @Override
    public double apply (int lx, int ly, long fx, long fy, long fxy) {
        return Long.valueOf (fxy).doubleValue ();
    }
    
    @Override
    public int hashCode () {
        return 445152607;
    }
    
    @Override
    public boolean equals (Object object) {
        return (Objects.nonNull (object) && (object instanceof CommonFrequency));
    }
    
    @Override
    public String toString () {
        return "CommonFrequency";
    }
}
