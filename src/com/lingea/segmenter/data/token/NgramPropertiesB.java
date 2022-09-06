package com.lingea.segmenter.data.token;

import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.math.fraction.LongFraction;
import com.github.josefplch.utils.data.tuple.UniformPair;

/**
 * @author  Josef Plch
 * @since   2019-11-21
 * @version 2021-01-12
 */
public class NgramPropertiesB extends GenericNgramProperties <StringList> {
    public NgramPropertiesB (LongFraction tf, LongFraction iwf, UniformPair <StringList> examples) {
        super (tf, iwf, examples);
    }
    
    public static NgramPropertiesB combine (NgramPropertiesB a, NgramPropertiesB b) {
        return new NgramPropertiesB (
            a.e1.sumElementWise (b.e1),
            a.e2.sumElementWise (b.e2),
            new UniformPair <> (
                combineExamples (a.getExamples ().get1 (), b.getExamples ().get1 ()),
                combineExamples (a.getExamples ().get2 (), b.getExamples ().get2 ())
            )
        );
    }
    
    private static StringList combineExamples (StringList listA, StringList listB) {
        StringList result = new StringList ();
        result.addAll (listA);
        for (String example : listB) {
            if (! result.contains (example)) {
                result.add (example);
            }
        }
        return result;
    }
}
