package com.lingea.segmenter;

import com.lingea.segmenter.data.ProbabilisticStringSegmentation;
import java.util.List;

/**
 * @author  Josef Plch
 * @since   2019-03-19
 * @version 2019-11-27
 */
public interface ProbabilisticStringSegmenter extends ProbabilisticSegmenter <Character>, StringSegmenter {
    @Override
    public ProbabilisticStringSegmentation segmentizeP (List <Character> sequence);
    
    public ProbabilisticStringSegmentation segmentizeP (String string);
}
