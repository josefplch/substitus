package com.lingea.segmenter.substitus.data;

import com.github.josefplch.utils.data.tuple.Tuple4;
import com.github.josefplch.utils.data.math.Mean;
import com.lingea.segmenter.utils.ShowUtils;

/**
 * Scores of an affix, all in range 0.0 - 1.0:
 * - frequency score (FS)
 * - length score (LS)
 * - predictability score (PS)
 * - similarity (SS)
 * - mixed score
 * 
 * @author  Josef Plch
 * @since   2018-07-10
 * @version 2019-12-20
 */
public class AffixScores extends Tuple4 <Double, Double, Double, Double> {
    public AffixScores (Double frequencyScore, Double lengthScore, Double predictabilityScore, Double similarity) {
        super (frequencyScore, lengthScore, predictabilityScore, similarity);
    }
    
    public Double getFrequencyScore () {
        return super.get1 ();
    }
    
    public Double getLengthScore () {
        return super.get2 ();
    }
    
    public Double getPredictabilityScore () {
        return super.get3 ();
    }
    
    public Double getSimilarity () {
        return super.get4 ();
    }
    
    // TODO: Would it be better to compute the mean on cell level?
    // TODO: Is there any better mixing strategy?
    public Double getMixedScore () {
        return Mean.geometricMean (
            this.getFrequencyScore (),
            this.getLengthScore (),
            this.getPredictabilityScore (),
            this.getSimilarity ()
        );
    }
    
    @Override
    public String toString () {
        return (
            "("
                + "FS "   + ShowUtils.showDouble (this.getFrequencyScore ())
                + ", LS " + ShowUtils.showDouble (this.getLengthScore ())
                + ", PS " + ShowUtils.showDouble (this.getPredictabilityScore ())
                + ", SS " + ShowUtils.showDouble (this.getSimilarity ())
                + ", MS " + ShowUtils.showDouble (this.getMixedScore ())
            + ")"
        );
    }
}
