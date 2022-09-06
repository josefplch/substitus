package com.github.josefplch.utils.data.map;

import com.github.josefplch.utils.data.tuple.Pair;
import java.util.Map;
import java.util.function.Function;

/**
 * Pair-based implementation of Map.Entry.
 * 
 * @param <K> Key type.
 * @param <V> Value type.
 * 
 * @author  Josef Plch
 * @since   2018-05-14
 * @version 2019-03-29
 */
public class MapEntry <K, V> extends Pair <K, V> implements Map.Entry <K, V> {
    public MapEntry (K key, V value) {
        super (key, value);
    }
    
    public static <K, V> MapEntry <K, V> from (Map.Entry <K, V> entry) {
        return new MapEntry <> (entry.getKey (), entry.getValue ());
    }
    
    public static <K, V> MapEntry <K, V> from (Pair <K, V> entry) {
        return new MapEntry <> (entry.get1 (), entry.get2 ());
    }
    
    @Override
    public <A, B> MapEntry <A, B> bimap (Function <K, A> f1, Function <V, B> f2) {
        return new MapEntry <> (f1.apply (super.get1 ()), f2.apply (super.get2 ()));
    }
    
    @Override
    public K getKey () {
        return super.get1 ();
    }
    
    @Override
    public V getValue () {
        return super.get2 ();
    }
    
    @Override
    public <A> MapEntry <A, V> map1 (Function <K, A> f) {
        return this.bimap (f, Function.identity ());
    }
    
    @Override
    public <A> MapEntry <K, A> map2 (Function <V, A> f) {
        return this.bimap (Function.identity (), f);
    }
    
    @Override
    public V setValue (V value) {
        V oldValue = this.get2 ();
        super.set2 (value);
        return oldValue;
    }
}
