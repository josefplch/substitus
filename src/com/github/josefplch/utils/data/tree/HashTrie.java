package com.github.josefplch.utils.data.tree;

import com.github.josefplch.utils.data.Bifunctor;
import com.github.josefplch.utils.data.Either;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.map.FunctionalMap;
import com.github.josefplch.utils.data.set.FunctionalSet;
import com.github.josefplch.utils.data.set.PairSet;
import com.github.josefplch.utils.data.string.StringUtils;
import com.github.josefplch.utils.data.tuple.Pair;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Trie, also known as prefix tree.
 * 
 * TODO: Implement Foldable?
 * 
 * @param <A>  Type of the atomic elements of the key.
 * @param <IV> Type of the internal (inner) vertex.
 * @param <LV> Type of the external (leaf) vertex.
 * 
 * @author  Josef Plch
 * @since   2018-05-12
 * @version 2021-01-12
 */
public class HashTrie <A, IV, LV> implements Trie <A, IV, LV>, Serializable {
    // It might be too computationally demanding to compute hash of the whole
    // trie. Therefore, in equals & hashCode, we can ignore the subtries.
    private static final boolean SHALLOW_CHECK = true;
    // HashMap uses initial capacity 16, but that is too much for us.
    private final int initialCapacity;
    // This value is used for empty nodes. (They may be created by "put".)
    private final IV initialNodeValue;
    private Either <IV, LV> nodeValue;
    // Just an auxiliary attribute.
    private int storedKeys = 0;
    private final Map <A, HashTrie <A, IV, LV>> subtries;
    
    public HashTrie (IV initialNodeValue) {
        this (2, initialNodeValue);
    }
    
    public HashTrie (int initialCapacity, IV initialNodeValue) {
        this.initialCapacity = initialCapacity;
        this.initialNodeValue = initialNodeValue;
        this.nodeValue = Either.left (initialNodeValue);
        this.subtries = new HashMap <> (initialCapacity);
    }
    
    @Override
    public <IV2, LV2> HashTrie <A, IV2, LV2> bimap (Function <IV, IV2> f, Function <LV, LV2> g) {
        HashTrie <A, IV2, LV2> result = new HashTrie <> (initialCapacity, f.apply (initialNodeValue));
        result.nodeValue = nodeValue.bimap (f, g);
        result.storedKeys = storedKeys;
        for (Map.Entry <A, HashTrie <A, IV, LV>> e : subtries.entrySet ()) {
            result.subtries.put (e.getKey (), e.getValue ().bimap (f, g));
        }
        return result;
    }

    @Override
    public boolean containsKey (List <A> key) {
        return this.getSubtrie (key).isPresent ();
    }
    
    protected void copyTo (HashTrie <A, IV, LV> other) {
        other.nodeValue = this.nodeValue;
        other.storedKeys = this.storedKeys;
        other.subtries.putAll (this.subtries);
    }
    
    @Override
    public boolean equals (Object object) {
        boolean result;
        if (object == this) {
            result = true;
        }
        else if (object == null || ! (object instanceof HashTrie)) {
            result = false;
        }
        else {
            final HashTrie <?, ?, ?> other = (HashTrie <?, ?, ?>) object;
            result =
                Objects.equals    (this.nodeValue,  other.nodeValue)
                && Objects.equals (this.storedKeys, other.storedKeys)
                && Objects.equals (
                    SHALLOW_CHECK ? this.subtries.keySet ()  : this.subtries,
                    SHALLOW_CHECK ? other.subtries.keySet () : other.subtries
                );
        }
        return result;        
    }
    
    // A type of merge function used by the put method.
    private static <V> V forgetOldValue (V oldValue, V newValue) {
        return newValue;
    }
    
    @Override
    public Either <IV, LV> getNodeValue () {
        return nodeValue;
    }
    
    public int getInitialCapacity () {
        return initialCapacity;
    }
    
    @Override
    public HashTrie <A, IV, LV> getSubtrie (A atom) {
        return subtries.get (atom);
    }
    
    @Override
    public Optional <HashTrie <A, IV, LV>> getSubtrie (List <A> key) {
        return getSubtrie (key, 0);
    }
    
    private Optional <HashTrie <A, IV, LV>> getSubtrie (List <A> key, int keyAtomIndex) {
        Optional <HashTrie <A, IV, LV>> result;
        if (keyAtomIndex == key.size ()) {
            result = Optional.of (this);
        }
        else {
            A atom = key.get (keyAtomIndex);
            if (! subtries.containsKey (atom)) {
                result = Optional.empty ();
            }
            else {
                result = subtries.get (atom).getSubtrie (key, keyAtomIndex + 1);
            }
        }
        return result;
    }
    
    @Override
    public FunctionalMap <A, Trie <A, IV, LV>> getSubtries () {
        FunctionalMap <A, Trie <A, IV, LV>> result = new FunctionalMap <> ();
        for (Map.Entry <A, HashTrie <A, IV, LV>> subtrie : subtries.entrySet ()) {
            result.put (subtrie.getKey (), subtrie.getValue ());
        }
        return result;
    }
    
    @Override
    public int hashCode () {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode (nodeValue);
        hash = 43 * hash + Objects.hashCode (storedKeys);
        hash = 43 * hash + Objects.hashCode (SHALLOW_CHECK ? subtries.keySet () : subtries);
        return hash;
    }
    
    @Override
    public boolean isEmpty () {
        return (storedKeys == 0);
    }
    
    @Override
    public FunctionalSet <? extends FunctionalList <A>> keySet () {
        return this.nodeSet ().firsts ();
    }
    
    @Override
    public void merge (
        List <A> key,
        Either <IV, LV> value,
        BinaryOperator <Either <IV, LV>> remappingFunction
    ) {
        this.merge (key, 0, value, remappingFunction, false);
    }
    
    private boolean merge (
        List <A> key,
        int keyAtomIndex,
        Either <IV, LV> value,
        BinaryOperator <Either <IV, LV>> remappingFunction,
        boolean mergeInternalNodes
    ) {
        boolean isNewLeaf;
        if (keyAtomIndex > key.size ()) {
            throw new IllegalArgumentException (
                "Atom index is too large (" + keyAtomIndex + ")"
            );
        }
        else if (keyAtomIndex == key.size ()) {
            isNewLeaf = nodeValue.isLeft () && value.isRight ();
            nodeValue = remappingFunction.apply (nodeValue, value);
        }
        else {
            if (mergeInternalNodes) {
                nodeValue = remappingFunction.apply (nodeValue, value);
            }
            
            A keyHead = key.get (keyAtomIndex);
            // If no corresponding subtrie exists, create a new one.
            if (! subtries.containsKey (keyHead)) {
                HashTrie <A, IV, LV> subtrie = new HashTrie <> (initialCapacity, initialNodeValue);
                subtries.put (keyHead, subtrie);
                isNewLeaf = subtrie.merge (key, keyAtomIndex + 1, value, HashTrie :: forgetOldValue, mergeInternalNodes);
            }
            // Else, update the corresponding subtrie.
            else {
                HashTrie <A, IV, LV> subtrie = subtries.get (keyHead);
                isNewLeaf = subtrie.merge (key, keyAtomIndex + 1, value, remappingFunction, mergeInternalNodes);
            }
        }
        if (isNewLeaf) {
            storedKeys++;
        }
        return isNewLeaf;
    }
    
    @Override
    public void mergePrefixes (
        List <A> key,
        Either <IV, LV> value,
        BinaryOperator <Either <IV, LV>> remappingFunction
    ) {
        this.merge (key, 0, value, remappingFunction, true);
    }
    
    @Override
    public PairSet <? extends FunctionalList <A>, LV> entrySet () {
        return this.entrySet (FunctionalList :: new);
    }
    
    protected <LA extends FunctionalList <A>> PairSet <LA, LV> entrySet (Supplier <LA> constructor) {
        return this.nodeSet (constructor).filter (entry -> entry.get2 ().isRight ()).map2 (Either :: getRight);
    }
    
    @Override
    public <IV2> Bifunctor <IV2, LV> map1 (Function <IV, IV2> f) {
        return this.bimap (f, Function.identity ());
    }

    @Override
    public <LV2> Bifunctor <IV, LV2> map2 (Function <LV, LV2> f) {
        return this.bimap (Function.identity (), f);
    }
    
    @Override
    public PairSet <? extends FunctionalList <A>, Either <IV, LV>> nodeSet () {
        return this.nodeSet (FunctionalList :: new);
    }
    
    protected <LA extends FunctionalList <A>> PairSet <LA, Either <IV, LV>> nodeSet (Supplier <LA> constructor) {
        return this.nodeSetInternal ().map1 (internalList -> {
            LA convertedList = constructor.get ();
            convertedList.addAll (internalList);
            return convertedList;
        });
    }
    
    // Internally, use LinkedList to enable the addFirst operation.
    private PairSet <LinkedList <A>, Either <IV, LV>> nodeSetInternal () {
        PairSet <LinkedList <A>, Either <IV, LV>> result = new PairSet <> ();
        result.addPair (new LinkedList <> (), nodeValue);
        for (Map.Entry <A, HashTrie <A, IV, LV>> atomAndSubtrie : subtries.entrySet ()) {
            A firstAtom = atomAndSubtrie.getKey ();
            PairSet <LinkedList <A>, Either <IV, LV>> entrySubset = atomAndSubtrie.getValue ().nodeSetInternal ();
            for (Pair <LinkedList <A>, Either <IV, LV>> entry : entrySubset) {
                entry.get1 ().addFirst (firstAtom);
                result.add (entry);
            }
        }
        return result;
    }
    
    @Override
    public HashTrie <A, IV, LV> prune (Predicate <Trie <A, IV, LV>> nodePredicate) {
        HashTrie <A, IV, LV> result = new HashTrie <> (initialCapacity, initialNodeValue);
        result.nodeValue = nodeValue;
        if (nodeValue.isRight ()) {
            result.storedKeys++;
        }
        
        // Copy only those branches which satisfy the predicate.
        for (Map.Entry <A, HashTrie <A, IV, LV>> atomAndSubtrie : subtries.entrySet ()) {
            HashTrie <A, IV, LV> subTrie = atomAndSubtrie.getValue ();
            if (nodePredicate.test (subTrie)) {
                HashTrie <A, IV, LV> prunedSubtrie = subTrie.prune (nodePredicate);
                result.subtries.put (atomAndSubtrie.getKey (), prunedSubtrie);
                result.storedKeys += prunedSubtrie.storedKeys;
            }
        }
        
        return result;
    }
    
    @Override
    public void put (List <A> key, Either <IV, LV> value) {
        this.merge (key, value, HashTrie :: forgetOldValue);
    }
    
    @Override
    public void putAll (Map <? extends List <A>, ? extends Either <IV, LV>> entries) {
        for (Map.Entry <? extends List <A>, ? extends Either <IV, LV>> entry : entries.entrySet ()) {
            this.put (entry.getKey (), entry.getValue ());
        }
    }
    
    @Override
    public int size () {
        return storedKeys;
    }
    
    @Override
    public String toString () {
        return toString (false);
    }
    
    public String toString (boolean indent) {
        return this.toString (indent, 0);
    }
    
    private String toString (boolean indent, int level) {
        StringBuilder resultBuilder = new StringBuilder ();
        resultBuilder.append ("Trie (");
        resultBuilder.append ("nodeValue = ").append (nodeValue);
        resultBuilder.append (", storedKeys = ").append (storedKeys);
        resultBuilder.append (", subtries = {");
        boolean first = true;
        for (Map.Entry <A, HashTrie <A, IV, LV>> atomAndSubtrie : subtries.entrySet ()) {
            if (first) {
                first = false;
            }
            else {
                resultBuilder.append (",");
            }
            A atom = atomAndSubtrie.getKey ();
            HashTrie <A, IV, LV> subtrie = atomAndSubtrie.getValue ();
            if (indent) {
                resultBuilder.append ("\n").append (StringUtils.space4 (level + 1));
            }
            resultBuilder.append (atom).append (" = ");
            resultBuilder.append (subtrie.toString (indent, level + 1));
        }
        if (indent) {
            resultBuilder.append ("\n").append (StringUtils.space4 (level + 0));
        }
        resultBuilder.append ("})");
        return resultBuilder.toString ();
    }
    
    @Override
    public FunctionalList <LV> values () {
        return this.entrySet ().toList ().seconds ();
    }
}
