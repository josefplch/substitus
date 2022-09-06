package com.lingea.segmenter.bpe.metric;

/**
 * @author  Josef Plch
 * @since   2018-05-04
 * @version 2018-05-13
 */
public interface AssociationMetric {
    public double apply (int lx, int ly, long fx, long fy, long fxy);
}
