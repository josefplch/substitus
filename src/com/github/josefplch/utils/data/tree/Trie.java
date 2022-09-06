package com.github.josefplch.utils.data.tree;

import com.github.josefplch.utils.data.Bifunctor;
import com.github.josefplch.utils.data.Either;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.map.FunctionalMap;
import com.github.josefplch.utils.data.set.FunctionalSet;
import com.github.josefplch.utils.data.set.PairSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

/**
 * Trie, also known as prefix tree.
 * 
 * TODO: Extend Foldable (Either (IV, LV))? Probably not possible.
 * 
 * @param <A>  Type of the atomic elements of the key.
 * @param <IV> Type of the internal (inner) vertex.
 * @param <LV> Type of the external (leaf) vertex.
 * 
 * @author  Josef Plch
 * @since   2018-05-13
 * @version 2021-01-12
 */
public interface Trie <A, IV, LV> extends Bifunctor <IV, LV> {
    public boolean containsKey (List <A> key);
    
    public Trie <A, IV, LV> getSubtrie (A atom);
    
    public Optional <? extends Trie <A, IV, LV>> getSubtrie (List <A> key);
    
    public FunctionalMap <A, Trie <A, IV, LV>> getSubtries ();
    
    public default Optional <Either <IV, LV>> get (List <A> key) {
        return this.getSubtrie (key).map (Trie <A, IV, LV> :: getNodeValue);
    }
    
    public Either <IV, LV> getNodeValue ();
    
    public PairSet <? extends FunctionalList <A>, LV> entrySet ();
    
    public boolean isEmpty ();
    
    public FunctionalSet <? extends FunctionalList <A>> keySet ();
    
    public void merge (
        List <A> key,
        Either <IV, LV> value,
        BinaryOperator <Either <IV, LV>> remappingFunction
    );
    
    public void mergePrefixes (
        List <A> key,
        Either <IV, LV> value,
        BinaryOperator <Either <IV, LV>> remappingFunction
    );
    
    // Original, wrong name: entrySet, changed on 2021-01-11
    public PairSet <? extends FunctionalList <A>, Either <IV, LV>> nodeSet ();
    
    /**
     * Cut off all branches which do not satisfy the given predicate.
     * 
     * @param nodePredicate
     * @return Pruned trie.
     */
    public Trie <A, IV, LV> prune (Predicate <Trie <A, IV, LV>> nodePredicate);
    
    public void put (List <A> key, Either <IV, LV> value);
    
    public void putAll (Map <? extends List <A>, ? extends Either <IV, LV>> entries);
    
    /**
     * Return size of the trie, i.e. number of leaves (not all nodes).
     * 
     * @return Number of leaves.
     */
    public int size ();
    
    // Original, wrong type: FunctionalList <Either <IV, LV>> values ();
    public FunctionalList <LV> values ();
}
