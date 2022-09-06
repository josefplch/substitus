package com.lingea.segmenter;

import com.lingea.segmenter.data.ProbabilisticSegmentation;
import com.lingea.segmenter.data.ProbabilisticStringSegmentation;

/**
 * @author  Josef Plch
 * @since   2019-11-25
 * @version 2019-11-26
 */
public abstract class ProbabilisticStringSegmentationTest {
    public static void main (String [] args) {
        ProbabilisticSegmentation segmentation =
            ProbabilisticStringSegmentation.readDouble (
                "h 0.05 a 0.23 v 0.87 e"
            );
        System.out.println (segmentation.elements ());
        System.out.println (segmentation.toString ());
        System.out.println (segmentation.normalize (0.2));
        System.out.println (segmentation.maxProbability ());
    }
}
