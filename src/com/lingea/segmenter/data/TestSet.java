package com.lingea.segmenter.data;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.system.TextFileUtils;
import com.lingea.segmenter.GlobalSettings;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

/**
 * @author  Josef Plch
 * @since   2018-10-29
 * @version 2021-01-14
 */
public class TestSet extends PairList <String, SimpleStringSegmentation> {
    public TestSet () {
    }
    
    public TestSet (Collection <? extends Pair <String, SimpleStringSegmentation>> pairs) {
        super (pairs);
    }
    
    public static TestSet readFile (String filePath) throws FileNotFoundException, IOException {
        TestSet result;
        if (filePath.endsWith ("." + GlobalSettings.EXTENSION_MCDS)) {
            result = readMC2010File (filePath);
        }
        else {
            result = readSubstitusFile (filePath);
        }
        return result;
    }
    
    // Format: bit \t bit:bite_V ~:+PAST, bit:bit_N
    public static TestSet readMC2010File (String filePath) throws FileNotFoundException, IOException {
        return (
            new TestSet (
                TextFileUtils.readLineList (filePath).mapToPair (line -> {
                    String [] columns = line.split ("\t");
                    if (columns.length != 2) {
                        throw new RuntimeException ("Invalid test set line: " + line);
                    }
                    else {
                        String word = columns [0];
                        FunctionalList <SimpleStringSegmentation> segmentations =
                            StringList.split (", ", columns [1])
                            .map (segmentation ->
                                SimpleStringSegmentation.read (
                                    StringList.split (" ", segmentation)
                                    // Erase the morphological info (token:info).
                                    // Beware of true colon, encoded with backslash.
                                    .mapToString (morph -> morph.replace ("\\:", "<colon>").replaceAll (":.+", "").replace ("<colon>", ":"))
                                    // Filter out null morphs.
                                    .filter (x -> ! x.equals ("~"))
                                    .join (GlobalSettings.HARD_DELIMITER_STRING)
                                )
                            );
                        
                        SimpleStringSegmentation combinedSegmentation =
                            segmentations.foldl (
                                SimpleStringSegmentation :: combine,
                                segmentations.head ()
                            );
                        
                        return Pair.of (word, combinedSegmentation);
                    }
                })
            )
        );
    }
    
    public static TestSet readSubstitusFile (String filePath) throws FileNotFoundException, IOException {
        return (
            new TestSet (
                TextFileUtils.readLineList (filePath)
                // Remove the comments.
                .mapToString (line -> line.replaceFirst (GlobalSettings.COMMENT_MARK + ".*", "").trim ())
                // Filter out empty lines.
                .filter (line -> ! line.isEmpty ())
                .mapToPair (line -> {
                    String [] columns = line.split ("(\t|  +)");
                    if (columns.length != 2) {
                        throw new RuntimeException ("Corrupted line: " + line);
                    }
                    else {
                        return Pair.of (
                            columns [0],
                            SimpleStringSegmentation.read (columns [1])
                        );
                    }
                })
            )
        );
    }
    
    @Override
    public String toString () {
        FunctionalList <Optional <Boolean>> glues = this.flatMap (entry -> entry.get2 ().glues ());
        return (
            "TestSet ("
            + "words: "       + this.size ()
            + ", correct: "   + glues.count (Optional.of (Boolean.TRUE))
            + ", incorrect: " + glues.count (Optional.of (Boolean.FALSE))
            + ", uncertain: " + glues.count (Optional.empty ())
            + ")"
        );
    }
}
