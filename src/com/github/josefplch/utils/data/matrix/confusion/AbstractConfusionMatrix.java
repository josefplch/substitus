package com.github.josefplch.utils.data.matrix.confusion;

import com.github.josefplch.utils.data.tuple.Tuple4;

/**
 * @param <N> Type of contained numbers.
 * 
 * @author  Josef Plch
 * @since   2018-07-02
 * @version 2019-03-26
 */
public abstract class AbstractConfusionMatrix <N extends Number> extends Tuple4 <N, N, N, N> implements ConfusionMatrix <N> {
    public AbstractConfusionMatrix (N tp, N fp, N tn, N fn) {
        super (tp, fp, tn, fn);
    }
    
    @Override
    public N getTP () {
        return super.get1 ();
    }
    
    @Override
    public N getFP () {
        return super.get2 ();
    }
    
    @Override
    public N getTN () {
        return super.get3 ();
    }
    
    @Override
    public N getFN () {
        return super.get4 ();
    }
    
    @Override
    public void setTP (N value) {
        super.set1 (value);
    }
    
    @Override
    public void setFP (N value) {
        super.set2 (value);
    }
    
    @Override
    public void setTN (N value) {
        super.set3 (value);
    }
    
    @Override
    public void setFN (N value) {
        super.set4 (value);
    }
}
