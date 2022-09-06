package com.github.josefplch.utils.data.matrix;

import com.github.josefplch.utils.data.list.FunctionalList;
import java.util.function.Function;

/**
 * @param <K> The type of row & column keys.
 * @param <V> The type of stored values.
 * 
 * @author  Josef Plch
 * @since   2019-02-14
 * @version 2019-02-14
 */
public interface UniformKeyMatrix <K, V> extends Matrix <K, K, V> {
    @Override
    public <W> UniformKeyMatrix <K, W> map (Function <V, W> f);
    
    @Override
    public UniformKeyMatrix <K, V> select (FunctionalList <K> rowKeys, FunctionalList <K> columnKeys);
}
