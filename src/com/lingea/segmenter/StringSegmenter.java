package com.lingea.segmenter;

import com.github.josefplch.utils.data.list.CharList;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.string.StringUtils;

/**
 * @author  Josef Plch
 * @since   2019-02-27
 * @version 2019-12-04
 */
public interface StringSegmenter extends Segmenter <Character> {
    public default StringList segmentize (String string) {
        return (
            FunctionalList.from (this.segmentize (CharList.fromString (string)))
            .mapToString (StringUtils :: charListToString)
        );
    }
}
