package com.lingea.segmenter.substitus.data;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.tuple.Tuple3;

/**
 * (form, frequency, similarity)
 * 
 * @param <A> Type of the basic, atomic element (typically: byte or character).
 * 
 * @author  Josef Plch
 * @since   2018-07-10
 * @version 2019-02-15
 */
public class AffixInfo <A> extends Tuple3 <FunctionalList <A>, Long, Double> {
    public AffixInfo (FunctionalList <A> form, Long frequency, Double similarity) {
        super (form, frequency, similarity);
    }
    
    public FunctionalList <A> getForm () {
        return super.get1 ();
    }
    
    public Long getFrequency () {
        return super.get2 ();
    }
    
    public Double getSimilarity () {
        return super.get3 ();
    }

    @Override
    public String toString () {
        return ("AffixInfo3 " + super.toString ());
    }
}
