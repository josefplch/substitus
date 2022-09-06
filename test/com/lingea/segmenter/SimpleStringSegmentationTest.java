package com.lingea.segmenter;

import com.lingea.segmenter.data.SimpleStringSegmentation;
import com.lingea.segmenter.eval.SegmentationEvaluator;
import static com.lingea.segmenter.data.SimpleStringSegmentation.read;

/**
 * @author  Josef Plch
 * @since   2019-11-26
 * @version 2019-11-26
 */
public abstract class SimpleStringSegmentationTest {
    public static void main (String [] args) {
        System.out.println (read ("xxx"));
        System.out.println (read ("x x?x"));
        System.out.println (read ("x?xx"));
        
        System.out.println (SegmentationEvaluator.evaluate (read ("xxx"), read ("xxx")));
        System.out.println (SegmentationEvaluator.evaluate (read ("x xx"), read ("x xx")));
        System.out.println (SegmentationEvaluator.evaluate (read ("x x x"), read ("x x?x")));
        
        System.out.println (SimpleStringSegmentation.combine (read ("abcd"), read ("abcd")));
        System.out.println (SimpleStringSegmentation.combine (read ("a bc d"), read ("a bcd")));
    }
}
