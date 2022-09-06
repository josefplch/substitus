package com.lingea.segmenter.eval;

import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.matrix.confusion.ConfusionMatrix;
import com.github.josefplch.utils.data.matrix.confusion.IntegerConfusionMatrix;
import com.lingea.segmenter.data.SimpleStringSegmentation;
import java.util.Objects;

/**
 * @author  Josef Plch
 * @since   2018-07-02
 * @version 2021-01-06
 */
public class SegmentationEvaluator {
    public static ConfusionMatrix <Integer> evaluate (
        SimpleStringSegmentation testedSegmentation,
        SimpleStringSegmentation correctSegmentation
    ) {
        if (! Objects.equals (testedSegmentation.elements (), correctSegmentation.elements ())) {
            throw new IllegalArgumentException (
                "Cannot compare segmentations of different words: "
                + testedSegmentation
                + ", " + correctSegmentation
            );
        }
        else {
            ConfusionMatrix <Integer> result = new IntegerConfusionMatrix ();
            PairList.zip (testedSegmentation.glues (), correctSegmentation.glues ()).forEach (pair -> {
                pair.get2 ().ifPresent (correct -> {
                    Boolean tested =
                        pair.get1 ().orElseThrow (
                            () -> new IllegalArgumentException (
                                "Tested segmentation must be unambiguous."
                            )
                        );
                    if (correct) {
                        if (tested) {
                            result.incrementTP (1);
                        }
                        else {
                            result.incrementFN (1);
                        }
                    }
                    else {
                        if (tested) {
                            result.incrementFP (1);
                        }
                        else {
                            result.incrementTN (1);
                        }
                    }
                });
            });
            return result;
        }
    }
}
