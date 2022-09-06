package com.github.josefplch.utils.data.matrix;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.map.MapEntry;
import com.github.josefplch.utils.data.tuple.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
public class SparseMatrix <RK, CK, V> implements Matrix <RK, CK, V> {
    protected final Map <Pair <RK, CK>, V> delegate;
    protected final FunctionalList <RK> rowKeys;
    protected final FunctionalList <CK> columnKeys;
    
    public SparseMatrix (List <RK> rowKeys, List <CK> columnKeys) {
        this.delegate = new HashMap <> ();
        this.rowKeys = FunctionalList.from (rowKeys);
        this.columnKeys = FunctionalList.from (columnKeys);
    }
    
    @Override
    public boolean equals (Object object) {
        boolean result;
        if (object == this) {
            result = true;
        }
        else if (object == null || ! (object instanceof Matrix)) {
            result = false;
        }
        else {
            final SparseMatrix <?, ?, ?> other = (SparseMatrix <?, ?, ?>) object;
            result =
                Objects.equals    (this.delegate,   other.delegate)
                && Objects.equals (this.rowKeys,    other.rowKeys)
                && Objects.equals (this.columnKeys, other.columnKeys);
        }
        return result;        
    }

    @Override
    public Optional <V> get (Pair <RK, CK> keys) {
        return Optional.ofNullable (delegate.get (keys));
    }

    @Override
    public Optional <V> get (RK rowKey, CK columnKey) {
        return this.get (Pair.of (rowKey, columnKey));
    }

    @Override
    public FunctionalList <Optional <V>> getColumn (CK columnKey) {
        return (
            rowKeys.map (rowKey ->
                this.get (Pair.of (rowKey, columnKey))
            )
        );
    }
    
    @Override
    public FunctionalList <CK> getColumnKeys () {
        return columnKeys;
    }
    
    @Override
    public Collection <MapEntry <Pair <RK, CK>, V>> getEntries () {
        return FunctionalList.from (delegate.entrySet ()).map (MapEntry :: from);
    }
    
    @Override
    public Collection <Pair <RK, CK>> getExistingKeys () {
        return delegate.keySet ();
    }
    
    @Override
    public FunctionalList <Optional <V>> getRow (RK rowKey) {
        return (
            columnKeys.map (columnKey ->
                this.get (rowKey, columnKey)
            )
        );
    }
    
    @Override
    public FunctionalList <RK> getRowKeys () {
        return rowKeys;
    }
    
    @Override
    public Collection <V> getValues () {
        return delegate.values ();
    }
    
    @Override
    public int hashCode () {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode (this.delegate);
        hash = 59 * hash + Objects.hashCode (this.rowKeys);
        hash = 59 * hash + Objects.hashCode (this.columnKeys);
        return hash;
    }
    
    @Override
    public <W> SparseMatrix <RK, CK, W> map (Function <V, W> f) {
        SparseMatrix <RK, CK, W> result = new SparseMatrix <> (rowKeys, columnKeys);
        for (RK rowKey : rowKeys) {
            for (CK columnKey : columnKeys) {
                Pair <RK, CK> key = Pair.of (rowKey, columnKey);
                if (delegate.containsKey (key)) {
                    result.delegate.put (key, f.apply (delegate.get (key)));
                }
            }
        }
        return result;
    }
    
    @Override
    public void set (Pair <RK, CK> keys, V value) {
        delegate.put (keys, value);
    }

    @Override
    public void set (RK rowKey, CK columnKey, V value) {
        delegate.put (Pair.of (rowKey, columnKey), value);
    }

    @Override
    public String toString () {
        return (
            "SparseMatrix {"
            + "rowKeys = " + rowKeys
            + ", columnKeys = " + columnKeys
            + ", values = " + delegate
            + "}"
        );
    }
    
    @Override
    public SparseMatrix <RK, CK, V> select (FunctionalList <RK> rowKeys, FunctionalList <CK> columnKeys) {
        SparseMatrix <RK, CK, V> result = new SparseMatrix <> (rowKeys, columnKeys);
        for (RK rowKey : rowKeys) {
            for (CK columnKey : columnKeys) {
                Pair <RK, CK> key = Pair.of (rowKey, columnKey);
                if (delegate.containsKey (key)) {
                    result.delegate.put (key, delegate.get (key));
                }
            }
        }
        return result;
    }
    
    @Override
    public int size () {
        return delegate.size ();
    }
}
