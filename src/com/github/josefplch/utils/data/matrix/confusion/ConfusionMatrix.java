package com.github.josefplch.utils.data.matrix.confusion;

/**
 * @param <N> Type of contained numbers.
 * 
 * @author  Josef Plch
 * @since   2018-07-02
 * @version 2018-09-20
 */
public interface ConfusionMatrix <N extends Number> {
    public default double getAccuracy () {
        return this.getWeightedAccuracy (1.0, 1.0);
    };
    
    /**
     * Matthews correlation coefficient:
     * 
     *                     TP * TN - FP * FN
     * MCC = ----------------------------------------------
     *       SQRT ((TP + FP) (TP + FN) (TN + FP) (TN + FN))
     * 
     * @return Matthews correlation coefficient.
     */
    public double getMcc ();
    
    public default double getPositiveFMeasure () {
        double precision = this.getPositivePrecision ();
        double recall    = this.getPositiveRecall ();
        return (2 * precision * recall) / (precision + recall);
    }
    
    public double getPositivePrecision ();
    
    public double getPositiveRecall ();
    
    public N getTP ();
    public N getFP ();
    public N getTN ();
    public N getFN ();
    
    public double getWeightedAccuracy (double wp, double wn);
    
    public void incrementTP (N value);
    public void incrementFP (N value);
    public void incrementTN (N value);
    public void incrementFN (N value);
    
    public void merge (ConfusionMatrix <N> other);
    
    public void setTP (N value);
    public void setFP (N value);
    public void setTN (N value);
    public void setFN (N value);
}
