package com.github.josefplch.utils.data.map;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.tuple.Pair;
import java.util.Map;

/**
 * @author  Josef Plch
 * @since   2018-05-21
 * @version 2019-03-25
 */
public abstract class MapUtils {
    public static <A, B> PairList <A, B> toList (Map <A, B> map) {
        return (
            FunctionalList.from (map.entrySet ())
            .mapToPair (entry -> Pair.of (entry.getKey (), entry.getValue ()))
        );
    }
}
