package com.github.josefplch.utils.data.matrix;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.tuple.Pair;
import java.util.List;
import java.util.function.Function;

/**
 * @param <K> The type of row & column keys.
 * @param <V> The type of stored values.
 * 
 * @author  Josef Plch
 * @since   2019-02-14
 * @version 2019-02-14
 */
public class UniformKeySparseMatrix <K, V> extends SparseMatrix <K, K, V> implements UniformKeyMatrix <K, V> {
    public UniformKeySparseMatrix (List <K> rowKeys, List <K> columnKeys) {
        super (rowKeys, columnKeys);
    }

    @Override
    public <W> UniformKeySparseMatrix <K, W> map (Function <V, W> f) {
        UniformKeySparseMatrix <K, W> result = new UniformKeySparseMatrix <> (rowKeys, columnKeys);
        for (K rowKey : rowKeys) {
            for (K columnKey : columnKeys) {
                Pair <K, K> key = Pair.of (rowKey, columnKey);
                if (delegate.containsKey (key)) {
                    result.delegate.put (key, f.apply (delegate.get (key)));
                }
            }
        }
        return result;
    }
    
    @Override
    public UniformKeySparseMatrix <K, V> select (FunctionalList <K> rowKeys, FunctionalList <K> columnKeys) {
        UniformKeySparseMatrix <K, V> result = new UniformKeySparseMatrix <> (rowKeys, columnKeys);
        for (K rowKey : rowKeys) {
            for (K columnKey : columnKeys) {
                Pair <K, K> key = Pair.of (rowKey, columnKey);
                if (delegate.containsKey (key)) {
                    result.delegate.put (key, delegate.get (key));
                }
            }
        }
        return result;
    }
}
