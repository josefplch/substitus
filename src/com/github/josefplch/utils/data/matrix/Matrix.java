package com.github.josefplch.utils.data.matrix;

import com.github.josefplch.utils.data.Functor;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.map.MapEntry;
import com.github.josefplch.utils.data.tuple.Pair;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

/**
 * @param <RK> The type of row keys.
 * @param <CK> The type of column keys.
 * @param <V>  The type of stored values.
 * 
 * @author  Josef Plch
 * @since   2019-02-14
 * @version 2019-02-14
 */
public interface Matrix <RK, CK, V> extends Functor <V> {
    public default Optional <V> get (Pair <RK, CK> keys) {
        return this.get (keys.get1 (), keys.get2 ());
    }
    
    public default V get (Pair <RK, CK> keys, V defaultValue) {
        return this.get (keys).orElse (defaultValue);
    }
    
    public Optional <V> get (RK rowKey, CK columnKey);
    
    public default V get (RK rowKey, CK columnKey, V defaultValue) {
        return this.get (rowKey, columnKey).orElse (defaultValue);
    }
    
    public FunctionalList <Optional <V>> getColumn (CK columnKey);
    
    public default FunctionalList <V> getColumn (CK columnKey, V defaultValue) {
        return (
            this.getColumn (columnKey)
            .map (value -> value.orElse (defaultValue))
        );
    }
    
    public FunctionalList <CK> getColumnKeys ();
    
    public Collection <MapEntry <Pair <RK, CK>, V>> getEntries ();
    
    public Collection <Pair <RK, CK>> getExistingKeys ();
    
    public FunctionalList <Optional <V>> getRow (RK rowKey);
    
    public default FunctionalList <V> getRow (RK rowKey, V defaultValue) {
        return (
            this.getRow (rowKey)
            .map (value -> value.orElse (defaultValue))
        );
    }
    
    public FunctionalList <RK> getRowKeys ();
    
    public Collection <V> getValues ();
    
    public default boolean isEmpty () {
        return (this.size () == 0);
    }
    
    @Override
    public <W> Matrix <RK, CK, W> map (Function <V, W> f);
    
    public Matrix <RK, CK, V> select (FunctionalList <RK> rowKeys, FunctionalList <CK> columnKeys);
    
    public default void set (Pair <RK, CK> keys, V value) {
        this.set (keys.get1 (), keys.get2 (), value);
    }
    
    public void set (RK rowKey, CK columnKey, V value);
    
    public int size ();
}
