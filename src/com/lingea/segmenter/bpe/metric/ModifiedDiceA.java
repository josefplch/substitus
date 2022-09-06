package com.lingea.segmenter.bpe.metric;

import java.util.Objects;

/**
 * @author  Josef Plch
 * @since   2018-05-08
 * @version 2018-06-11
 */
public class ModifiedDiceA implements AssociationMetric {
    private final AssociationMetric diceMetric = new Dice ();
    
    @Override
    public double apply (int lx, int ly, long fx, long fy, long fxy) {
        double dice = diceMetric.apply (lx, ly, fx, fy, fxy);
        return (dice * fxy);
    }
    
    @Override
    public int hashCode () {
        return 260205911;
    }

    @Override
    public boolean equals (Object object) {
        return (Objects.nonNull (object) && (object instanceof ModifiedDiceA));
    }
    
    @Override
    public String toString () {
        return "ModifiedDiceA";
    }
}
