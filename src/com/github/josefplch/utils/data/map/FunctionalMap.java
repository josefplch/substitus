package com.github.josefplch.utils.data.map;

import com.github.josefplch.utils.data.Functor;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.set.FunctionalSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Map with some additional functionality.
 * 
 * In Haskell, Map k implements Foldable v. However, it forces the toList method
 * to return List v, whereas we want to return List (k, v). Thus, we do not
 * implement the Foldable interface.
 * 
 * In contrary to Haskell, the key does not have to be comparable.
 * 
 * @param <K> Type of keys.
 * @param <V> Type of values.
 * 
 * @author  Josef Plch
 * @since   2020-12-04
 * @version 2021-01-08
 */
public class FunctionalMap <K, V> implements Functor <V>, Map <K, V> {
    protected final Map <K, V> delegate = new HashMap <> ();

    public FunctionalMap () {
    }
    
    @Override
    public void clear () {
        delegate.clear ();
    }

    @Override
    public boolean containsKey (Object key) {
        return delegate.containsKey (key);
    }

    @Override
    public boolean containsValue (Object value) {
        return delegate.containsValue (value);
    }

    @Override
    public FunctionalSet <Entry <K, V>> entrySet () {
        return FunctionalSet.from (delegate.entrySet ());
    }
    
    @Override
    public boolean equals (Object object) {
        boolean result;
        if (object == this) {
            result = true;
        }
        else if (object == null || ! (object instanceof Map)) {
            result = false;
        }
        else {
            result = Objects.equals (delegate, object);
        }
        return result;
    }
    
    @Override
    public V get (Object key) {
        return delegate.get (key);
    }
    
    public Optional <V> getOptional (K key) {
        return Optional.ofNullable (delegate.get (key));
    }

    @Override
    public int hashCode () {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode (delegate);
        return hash;
    }
    
    @Override
    public boolean isEmpty () {
        return delegate.isEmpty ();
    }

    @Override
    public FunctionalSet <K> keySet () {
        return FunctionalSet.from (delegate.keySet ());
    }

    @Override
    public <W> FunctionalMap <K, W> map (Function <V, W> f) {
        FunctionalMap <K, W> result = new FunctionalMap <> ();
        for (Entry <K, V> entry : delegate.entrySet ()) {
            result.put (entry.getKey (), f.apply (entry.getValue ()));
        }
        return result;
    }
    
    @Override
    public V put (K key, V value) {
        return delegate.put (key, value);
    }

    @Override
    public void putAll (Map <? extends K, ? extends V> map) {
        delegate.putAll (map);
    }

    @Override
    public V remove (Object key) {
        return delegate.remove (key);
    }

    @Override
    public int size () {
        return delegate.size ();
    }
    
    public PairList <K, V> toList () {
        PairList <K, V> result = new PairList <> ();
        for (Entry <K, V> entry : delegate.entrySet ()) {
            result.addPair (entry.getKey (), entry.getValue ());
        }
        return result;
    }

    @Override
    public String toString () {
        return (
            "{"
            + this.entrySet ()
                .toList ()
                .mapToString (entry -> entry.getKey () + ": " + entry.getValue ())
                // The keys do not have to be comparable, so instead, we sort the strings.
                .sortAsc ()
                .join (", ")
            + "}"
        );
    }
    
    @Override
    public FunctionalList <V> values () {
        return FunctionalList.from (delegate.values ());
    }
}
