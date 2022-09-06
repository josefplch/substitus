package com.github.josefplch.utils.data.matrix.confusion;

/**
 * @author  Josef Plch
 * @since   2018-07-02
 * @version 2018-09-20
 */
public class IntegerConfusionMatrix extends AbstractConfusionMatrix <Integer> {
    public IntegerConfusionMatrix () {
        super (0, 0, 0, 0);
    }
    
    @Override
    public double getMcc () {
        int tp = this.getTP ();
        int tn = this.getTN ();
        int fp = this.getFP ();
        int fn = this.getFN ();
        
        // It is safer to use double, the values may be too high.
        double numerator = tp * tn - fp * fn;
        double denominator = 1.0 * (tp + fp) * (tp + fn) * (tn + fp) * (tn + fn);
        return (numerator / Math.sqrt (denominator));
    }
    
    @Override
    public double getPositivePrecision () {
        return (1.0 * this.getTP () / (this.getTP () + this.getFP ()));
    }
    
    @Override
    public double getPositiveRecall () {
        return (1.0 * this.getTP () / (this.getTP () + this.getFN ()));
    }
    
    @Override
    public double getWeightedAccuracy (double wp, double wn) {
        int tp = this.getTP ();
        int fp = this.getFP ();
        int tn = this.getTN ();
        int fn = this.getFN ();
        return (wp * tp + wn * tn) / (wp * (tp + fp) + wn * (tn + fn));
    }
    
    @Override
    public void incrementTP (Integer value) {
        this.setTP (this.getTP () + value);
    }
    
    @Override
    public void incrementFP (Integer value) {
        this.setFP (this.getFP () + value);
    }
    
    @Override
    public void incrementFN (Integer value) {
        this.setFN (this.getFN () + value);
    }
    
    @Override
    public void incrementTN (Integer value) {
        this.setTN (this.getTN () + value);
    }
    
    @Override
    public void merge (ConfusionMatrix <Integer> other) {
        this.incrementTP (other.getTP ());
        this.incrementFP (other.getFP ());
        this.incrementTN (other.getTN ());
        this.incrementFN (other.getFN ());
    }
    
    @Override
    public String toString () {
        return (
            "("
                + this.getTP ()
                + ", "  + this.getFN ()
                + " | " + this.getFP ()
                + ", "  + this.getTN ()
            + ")"
        );
    }
}
