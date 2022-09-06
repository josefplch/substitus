package com.lingea.segmenter.substitus.data;

import com.github.josefplch.utils.system.BinarySerializer;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.tree.FrequencyTrie;
import com.github.josefplch.utils.data.tuple.UniformPair;
import java.io.IOException;

/**
 * Pair of prefix and suffix trie. Each entry is stored twice.
 * 
 * @param <A> Type of the basic, atomic element (typically: byte or character).
 * 
 * @author  Josef Plch
 * @since   2019-04-03
 * @version 2021-01-19
 */
public class FrequencyTriePair <A> extends UniformPair <FrequencyTrie <A>> {
    public FrequencyTriePair () {
        super (new FrequencyTrie <> (), new FrequencyTrie <> ());
    }
    
    public FrequencyTriePair (int initialCapacity) {
        super (
            new FrequencyTrie <> (initialCapacity),
            new FrequencyTrie <> (initialCapacity)
        );
    }
    
    public static <A> FrequencyTriePair <A> deserialize (String filePath) throws ClassNotFoundException, IOException {
        return BinarySerializer.deserializeFast (filePath);
    }
    
    public Long getFrequency (FunctionalList <A> sequence) {
        return e1.getFrequency (sequence);
    }
    
    public Long getPrefixFrequency (FunctionalList <A> prefix) {
        return e1.getPrefixFrequency (prefix);
    }
    
    public FrequencyTrie <A> getPrefixTrie () {
        return e1;
    }
    
    public Long getSuffixFrequency (FunctionalList <A> suffix) {
        return e2.getPrefixFrequency (suffix.reverse ());
    }
    
    public FrequencyTrie <A> getSuffixTrie () {
        return e2;
    }
    
    public void rememberCounted (FunctionalList <A> sequence, Long frequency) {
        e1.modifyFrequency (sequence, frequency);
        e2.modifyFrequency (sequence.reverse (), frequency);
    }
    
    public long totalSequencesCount () {
        return this.getPrefixFrequency (FunctionalList.of ());
    }
    
    public int uniqueSequencesCount () {
        return e1.size ();
    }
}
