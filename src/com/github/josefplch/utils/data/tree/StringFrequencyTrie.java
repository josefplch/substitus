package com.github.josefplch.utils.data.tree;

import com.github.josefplch.utils.data.Either;
import com.github.josefplch.utils.data.list.CharList;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.set.PairSet;
import com.github.josefplch.utils.data.tuple.UniformPair;
import java.util.Optional;

/**
 * @author  Josef Plch
 * @since   2018-05-15
 * @version 2021-01-11
 */
public class StringFrequencyTrie extends FrequencyTrie <Character> {
    public StringFrequencyTrie () {
        super ();
    }
    
    public StringFrequencyTrie (int initialCapacity) {
        super (initialCapacity);
    }
    
    public void decrementFrequency (String key) {
        this.modifyFrequency (key, -1L);
    }
    
    public Optional <Either <Long, UniformPair <Long>>> get (String key) {
        return super.get (CharList.fromString (key));
    }
    
    public UniformPair <Long> getFrequencies (String key) {
        return super.getFrequencies (CharList.fromString (key));
    }
    
    public Long getPrefixFrequency (String key) {
        return super.getPrefixFrequency (CharList.fromString (key));
    }
    
    public Long getWordFrequency (String key) {
        return super.getFrequency (CharList.fromString (key));
    }
    
    public Optional <StringFrequencyTrie> getSubtrie (String key) {
        return (
            super.getSubtrie (CharList.fromString (key))
            .map (subtrie -> {
                StringFrequencyTrie result =
                    new StringFrequencyTrie (subtrie.getInitialCapacity ());
                subtrie.copyTo (result);
                return result;
            })
        );
    }
    
    public void incrementFrequency (String key) {
        this.modifyFrequency (key, 1L);
    }
    
    public void modifyFrequency (String key, Long difference) {
        super.modifyFrequency (CharList.fromString (key), difference);
    }
    
    @Override
    public PairSet <CharList, Either <Long, UniformPair <Long>>> nodeSet () {
        return super.nodeSet (CharList :: new);
    }
    
    public void put (String key, Either <Long, UniformPair <Long>> value) {
        super.put (CharList.fromString (key), value);
    }
    
    @Override
    public PairList <CharList, Long> toAscList () {
        return super.toAscList (CharList :: new);
    }
    
    @Override
    public PairList <CharList, Long> toDescList () {
        return super.toDescList (CharList :: new);
    }    
}
