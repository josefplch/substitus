package com.lingea.segmenter.utils;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.number.DoubleList;
import com.github.josefplch.utils.data.list.number.IntegerList;

/**
 * @author  Josef Plch
 * @since   2019-06-21
 * @version 2019-12-01
 */
public abstract class RandomReliability {
    private final static double THRESHOLD = 0.5;
    private final static int BATCH_SIZE = 40;
    private final static int TESTS = 1_000_000;
    
    public static void main () {
        FunctionalList <IntegerList> binaryTable =
            FunctionalList.replicate (TESTS, "useless value").map (test ->
                FunctionalList.replicate (BATCH_SIZE, "useless value")
                .mapToInteger (x -> Math.random () >= THRESHOLD ? 1 : 0)
            );
        
        DoubleList batchErrors =
            IntegerList.interval (1, BATCH_SIZE)
            .mapToDouble (bi ->
                binaryTable.mapToDouble (batch ->
                    // Get batch error.
                    Math.abs (batch.take (bi).arithmeticMean () - THRESHOLD)
                )
                // Get average error.
                .arithmeticMean ()
            );
                
        // System.out.println (table.mapToString ().unlines ());
        System.out.println (batchErrors);
    }
}
