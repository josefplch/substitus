package com.lingea.segmenter.substitus.data;

import com.github.josefplch.utils.data.math.Mean;
import com.github.josefplch.utils.data.tuple.Tuple5;

/**
 * @author  Josef Plch
 * @since   2018-07-09
 * @version 2019-12-20
 */
public class CompoundInfo extends Tuple5 <Integer, Long, Double, Double, Double> {
    public CompoundInfo (Integer length, Long actualFrequency, Double predictedFrequencyA, Double predictedFrequencyB, Double hypotheticalSimilarity) {
        super (length, actualFrequency, predictedFrequencyA, predictedFrequencyB, hypotheticalSimilarity);
    }
    
    public Integer getLength () {
        return e1;
    }
    
    public Long getActualFrequency () {
        return e2;
    }
    
    public Double getHSimilarity () {
        return e5;
    }
    
    public Double getPFrequencyA () {
        return e3;
    }
    
    public Double getPFrequencyB () {
        return e4;
    }
    
    public Double getPFrequencyM () {
        return Mean.geometricMean (e3, e4);
    }
    
    @Override
    public String toString () {
        return (
            "CompoundInfo ("
                + "Len "   + this.getLength ()
                + ", AF "  + this.getActualFrequency ()
                + ", PFA " + this.getPFrequencyA ()
                + ", PFB " + this.getPFrequencyB ()
                + ", PFM"  + this.getPFrequencyM ()
                + ", HS "  + this.getHSimilarity ()
            + ")"
        );
    }
}
