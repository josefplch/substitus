package com.lingea.segmenter;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.system.TextFileUtils;
import com.lingea.segmenter.substitus.StringSubstitusTokenizer;
import com.lingea.segmenter.utils.ShowUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author  Josef Plch
 * @since   2018-10-10
 * @version 2021-01-14
 */
public abstract class SubstitusTokenizerTest {
    public static void main (String [] args) {
        try {
            testSegmenter ();
        }
        catch (IOException exception) {
            System.err.println (exception);
        }
    }
    
    private static void printSegmentation (
        String string,
        String tokenFilePath
    ) throws IOException {
        StringList tokens =
            StringSubstitusTokenizer.loadTokenFile (Character :: toLowerCase, tokenFilePath)
            .segmentize (string);
        
        System.out.println (
            "[" + tokens.size () + " tokens] "
            + String.join (
                GlobalSettings.HARD_DELIMITER_STRING,
                tokens
            )
            .replace (
                GlobalSettings.HARD_DELIMITER + " " + GlobalSettings.HARD_DELIMITER,
                "  "
            )
        );
    }
    
    public static void segmentizeFile (
        StringSegmenter segmenter,
        String inputFilePath,
        String outputFilePath
    ) throws IOException {
        BufferedWriter writer =
            new BufferedWriter (
                new FileWriter (outputFilePath)
            );
        
        writer.append (ShowUtils.makeColumn ("Word"));
        writer.append (ShowUtils.makeColumn ("m0,f0,s0,t0,e0")); // TODO
        writer.newLine ();
        writer.append (ShowUtils.horizontalLine (2));
        writer.newLine ();
        
        StringList words = TextFileUtils.readColumn (inputFilePath, 0);
        for (String word : words) {
            List <String> segments = FunctionalList.from (segmenter.segmentize (word));
            writer.append (
                ShowUtils.makeColumn (word)
                + String.join (GlobalSettings.HARD_DELIMITER_STRING, segments)
            );
            writer.newLine ();
        }
        
        writer.close ();
    }
    
    private static void testSegmenter () throws IOException {
        String testString = TestData.CS_A.toLowerCase ().replaceAll ("[ \t\n]+", " ");
        System.out.println ("Test string: " + testString.length () + " characters");
        
        System.out.println ("CS");
        printSegmentation (testString, DataPaths.tunedTokenFile ("cs_all_1000k_bin50_n3_frq100_nob_ordered_n3_t10_len10_frq100_iwf5_unq_fn2.txt"));
        printSegmentation (testString, DataPaths.tunedTokenFile ("cs_all_1000k_bin50_n3_frq100_nob_ordered_n3_t10_len10_frq100_iwf5_unq_fn3.txt"));
        
        System.out.println ();
        System.out.println ("DE");
        printSegmentation (testString, DataPaths.tunedTokenFile ("de_all_1000k_bin50_n3_frq100_nob_ordered_n3_t10_len10_frq100_iwf5_unq_fn3.txt"));
        
        System.out.println ();
        System.out.println ("EN");
        printSegmentation (testString, DataPaths.tunedTokenFile ("en_all_1000k_bin50_n3_frq100_nob_ordered_n3_t10_len10_frq100_iwf5_unq_fn3.txt"));
    }
}
