package com.github.josefplch.utils.data.map;

import com.github.josefplch.utils.data.list.PairList;
import java.util.Comparator;

/**
 * @param <K> Type of key.
 * 
 * @author  Josef Plch
 * @since   2018-05-18
 * @version 2021-01-15
 */
public interface FrequencyCounter <K> {
    public default void decrementFrequency (K key) {
        this.modifyFrequency (key, -1L);
    }
    
    public default void incrementFrequency (K key) {
        this.modifyFrequency (key, 1L);
    }
    
    /**
     * This method shall be overriden due to performance reasons.
     * 
     * @param key
     * @param difference
     */
    public void modifyFrequency (K key, Long difference);
    
    public PairList <? extends K, Long> toAscList ();
    
    public PairList <? extends K, Long> toAscListBy (Comparator <K> keyComparator);
    
    public PairList <? extends K, Long> toDescList ();
    
    public PairList <? extends K, Long> toDescListBy (Comparator <K> keyComparator);
}
