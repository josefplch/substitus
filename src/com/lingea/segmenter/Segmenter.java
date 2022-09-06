package com.lingea.segmenter;

import java.util.List;

/**
 * @param <A> Type of the basic, atomic element (typically: byte or character).
 * 
 * @author  Josef Plch
 * @since   2018-05-28
 * @version 2019-10-02
 */
public interface Segmenter <A> {
    /**
     * Split the input sequence to chunks.
     * 
     * @param sequence The sequence being split.
     * @return         List of segments of the original sequence.
     */
    public List <List <A>> segmentize (List <A> sequence);
}
