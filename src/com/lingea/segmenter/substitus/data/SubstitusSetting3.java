package com.lingea.segmenter.substitus.data;

import com.github.josefplch.utils.data.tuple.Tuple3;
import java.util.Comparator;

/**
 * Settings without threshold.
 * 
 * @author  Josef Plch
 * @since   2019-04-09
 * @version 2019-12-17
 */
public class SubstitusSetting3 extends Tuple3 <Integer, Integer, Integer> implements Comparable <SubstitusSetting3> {
    private static final Comparator <Tuple3 <Integer, Integer, Integer>> COMPARATOR = Tuple3.LexicographicalComparator.natural ();
    
    public SubstitusSetting3 (
        Integer minAffixFrequency,
        Integer kMostFrequent,
        Integer squareSize
    ) {
        super (
            minAffixFrequency,
            kMostFrequent,
            squareSize
        );
    }
    
    @Override
    public int compareTo (SubstitusSetting3 other) {
        return COMPARATOR.compare (this, other);
    }
    
    // Serialized form: "f1,k64,s8".
    public static SubstitusSetting3 read (String serialized) {
        String [] parts = serialized.split (",");
        return (
            new SubstitusSetting3 (
                Integer.parseInt (parts [0].substring (1)),
                Integer.parseInt (parts [1].substring (1)),
                Integer.parseInt (parts [2].substring (1))
            )
        );
    }
    
    public Integer getKMostFrequent () {
        return super.get2 ();
    }
    
    public Integer getMinWordFrequency () {
        return super.get1 ();
    }

    public Integer getSquareSize () {
        return super.get3 ();
    }
}
