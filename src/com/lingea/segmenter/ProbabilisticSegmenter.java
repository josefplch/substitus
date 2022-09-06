package com.lingea.segmenter;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.lingea.segmenter.data.ProbabilisticSegmentation;
import java.util.List;

/**
 * @param <A> Type of the basic, atomic element (typically: byte or character).
 * 
 * @author  Josef Plch
 * @since   2019-03-19
 * @version 2020-10-08
 */
public interface ProbabilisticSegmenter <A> extends Segmenter <A> {
    @Override
    public default List <List <A>> segmentize (List <A> sequence) {
        return this.segmentizeP (sequence).binarize50 ().map (FunctionalList :: from);
    }
    
    /**
     * Compute probability of each possible split of the input sequence.
     * 
     * @param sequence The sequence being split.
     * @return         Probabilistic segmentation of the sequence.
     */
    public ProbabilisticSegmentation <A> segmentizeP (List <A> sequence);
}
