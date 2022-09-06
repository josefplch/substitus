package com.lingea.segmenter.substitus.data;

import com.github.josefplch.utils.data.string.DoubleFormatter;
import com.github.josefplch.utils.data.tuple.Pair;
import java.util.Comparator;

/**
 * Settings with threshold, used only in MultiTestEvaluator.
 * 
 * @author  Josef Plch
 * @since   2019-04-09
 * @version 2019-12-17
 */
public class SubstitusSetting4 extends Pair <SubstitusSetting3, Double> implements Comparable <SubstitusSetting4> {
    private static final Comparator <Pair <SubstitusSetting3, Double>> COMPARATOR = Pair.LexicographicalComparator.natural ();
    
    public SubstitusSetting4 (SubstitusSetting3 setting4, Double threshold) {
        super (setting4, threshold);
    }
    
    public SubstitusSetting4 (
        Integer minAffixFrequency,
        Integer kMostFrequent,
        Integer squareSize,
        Double threshold
    ) {
        super (
            new SubstitusSetting3 (
                minAffixFrequency,
                kMostFrequent,
                squareSize
            ),
            threshold
        );
    }
    
    @Override
    public int compareTo (SubstitusSetting4 other) {
        return COMPARATOR.compare (this, other);
    }
    
    public Integer getMinWordFrequency () {
        return super.get1 ().getMinWordFrequency ();
    }

    public Integer getKMostFrequent () {
        return super.get1 ().getKMostFrequent ();
    }
    
    public Integer getSquareSize () {
        return super.get1 ().getSquareSize ();
    }

    public Double getThreshold () {
        return super.get2 ();
    }

    @Override
    public String toString () {
        return (
            "("
            + getMinWordFrequency ()
            + ", " + getKMostFrequent ()
            + ", " + getSquareSize ()
            + ", " + DoubleFormatter.POINT_3.format (getThreshold ())
            + ")"
        );
    }
}
