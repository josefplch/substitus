package com.lingea.segmenter.substitus.data;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.set.StringSet;
import com.github.josefplch.utils.data.tuple.Tuple4;
import com.github.josefplch.utils.system.TextFileUtils;
import com.lingea.segmenter.GlobalSettings;
import com.lingea.segmenter.data.ProbabilisticStringSegmentation;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author  Josef Plch
 * @since   2018-09-24
 * @version 2021-01-14
 */
public class SegmentationFileReader {
    // Original thesis format:   frequency \t word \t segmentation
    // Updated format (2020-12): frequency \t word \t [lemmata \t] segmentation
    public static Tuple4 <Long, String, StringSet, ProbabilisticStringSegmentation> readLine (String line) {
        StringList columns = StringList.split ("\t", line);
        String word = columns.get (1);
        String lemmaColumn = columns.size () == 3 ? "" : columns.get (2);
        Predicate <String> hasPunctuation = Pattern.compile ("[-.]").asPredicate ();
        return (
            Tuple4.of (
                Long.valueOf (columns.get (0)),
                word,
                lemmaColumn.isEmpty ()
                    ? new StringSet ()
                    : StringList.split (String.valueOf (GlobalSettings.LEMMA_DELIMITER), lemmaColumn)
                        // Heuristics: Ignore lemmata with too different length as they are probably wrong.
                        // .filter (lemma -> Math.abs (lemma.length () - word.length ()) <= 5)
                        // Heuristics: The punctuation in lemma shall occur iff it occurs in the word.
                        .filter (lemma -> hasPunctuation.test (lemma) == hasPunctuation.test (word))
                        .toSet (),
                ProbabilisticStringSegmentation.readDouble (columns.last ())
            )
        );
    }
    
    public static FunctionalList <Tuple4 <Long, String, StringSet, ProbabilisticStringSegmentation>> readList (
        BufferedReader reader,
        Optional <Integer> limit
    ) throws IOException {
        return TextFileUtils.readLineList (reader, 0, limit).map (SegmentationFileReader :: readLine);
    }
    
    public static Stream <Tuple4 <Long, String, StringSet, ProbabilisticStringSegmentation>> readStream (
        BufferedReader reader,
        Optional <Integer> limit
    ) throws IOException {
        return TextFileUtils.readLineStream (reader, limit).map (SegmentationFileReader :: readLine);
    }
}
