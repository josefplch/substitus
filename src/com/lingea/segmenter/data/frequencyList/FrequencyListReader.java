package com.lingea.segmenter.data.frequencyList;

import com.github.josefplch.utils.data.tuple.Pair;

/**
 * @author  Josef Plch
 * @since   2018-10-08
 * @version 2020-12-11
 */
@Deprecated
public class FrequencyListReader {
    @Deprecated
    public static Pair <String, Long> readLine (String line) throws IllegalArgumentException {
        // Line format: frequency [white space] word
        String [] columns = line.split ("[ \t]+");
        if (columns.length != 2) {
            throw new IllegalArgumentException ("Illegal line: " + line);
        }
        else {
            return (
                Pair.of (
                    columns [1],
                    Long.parseLong (columns [0])
                )
            );
        }
    }
}
