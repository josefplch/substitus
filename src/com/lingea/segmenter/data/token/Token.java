package com.lingea.segmenter.data.token;

import java.util.List;

/**
 * @param <A> Type of the basic, atomic element (typically: byte or character).
 * 
 * @author  Josef Plch
 * @since   2018-10-10
 * @version 2018-10-10
 */
public interface Token <A> {
    public List <A> getAtoms ();
    
    public int size ();
}
