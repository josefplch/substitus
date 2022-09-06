package com.lingea.segmenter.eval;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.number.IntegerList;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.system.MemoryUtils;
import com.lingea.segmenter.GlobalSettings;
import com.lingea.segmenter.substitus.data.SubstitusSetting3;
import com.lingea.segmenter.data.ProbabilisticStringSegmentation;
import com.lingea.segmenter.data.SimpleStringSegmentation;
import com.lingea.segmenter.substitus.StringSubstitus;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This is a utility for testing multiple settings using a single Substitus.
 * It saves a lot of resources.
 * 
 * @author  Josef Plch
 * @since   2018-05-21
 * @version 2021-01-14
 */
public class SubstitusSettingTester {
    private static final boolean PRINT_HEADER = true;
    private final IntegerList minWordFrequencyValues;
    private final IntegerList kMostFrequentValues;
    private final IntegerList squareSizeValues;
    private final Function <ProbabilisticStringSegmentation, String> showFunction;
    private final FunctionalList <SubstitusSetting3> settings;
    
    public SubstitusSettingTester (
        IntegerList minWordFrequencyValues,
        IntegerList kMostFrequentValues,
        IntegerList squareSizeValues,
        Function <ProbabilisticStringSegmentation, String> showFunction
    ) {
        this.minWordFrequencyValues = minWordFrequencyValues;
        this.kMostFrequentValues = kMostFrequentValues;
        this.squareSizeValues = squareSizeValues;
        this.showFunction = showFunction;
        this.settings = new FunctionalList <> ();
        for (Integer minWordFrequency : minWordFrequencyValues) {
            for (Integer kMostFrequent : kMostFrequentValues) {
                for (Integer squareSize : squareSizeValues) {
                    // Greater values do not make sense.
                    if (squareSize <= kMostFrequent) {
                        SubstitusSetting3 setting =
                            new SubstitusSetting3 (
                                minWordFrequency,
                                kMostFrequent,
                                squareSize
                            );
                        settings.add (setting);
                    }
                }
            }
        }
    }
    
    private void printHeader (
        Writer writer,
        String frequencyListPath,
        int uniqueWords
    ) throws IOException {
        // Comments.
        writer
            .append (GlobalSettings.COMMENT_MARK + " Frequency list path:  " + frequencyListPath + "\n")
            .append (GlobalSettings.COMMENT_MARK + " Frequency list size:  " + Integer.toString (uniqueWords) + " words\n")
            .append (GlobalSettings.COMMENT_MARK + " Min word frequencies: " + minWordFrequencyValues + "\n")
            .append (GlobalSettings.COMMENT_MARK + " Most frequent words:  " + kMostFrequentValues + "\n")
            .append (GlobalSettings.COMMENT_MARK + " Table sizes:          " + squareSizeValues + "\n")
            .append (GlobalSettings.COMMENT_MARK + " Number of settings:   " + Integer.toString (settings.size ()) + "\n");
        
        writer.append ('\n');
        
        // Table header.
        writer.append ("Word");
        for (SubstitusSetting3 setting : settings) {
            String thisSettingHeader =
                "f" + setting.getMinWordFrequency ()
                + ",k" + setting.getKMostFrequent ()
                + ",s" + setting.getSquareSize ();
            writer.append ("\t" + thisSettingHeader);
        }
        writer.append ('\n');
        
        writer.flush ();
    }
    
    public void runTest (
        StringSubstitus substitus,
        String frequencyListPath,
        Stream <Pair <String, SimpleStringSegmentation>> inputWords,
        Writer outputWriter
    ) throws IOException {
        if (PRINT_HEADER) {
            printHeader (outputWriter, frequencyListPath, substitus.uniqueCompoundsCount ());
        }
        
        // Table body.
        inputWords.forEach (pair -> {
            try {
                String word = pair.get1 ();
                SimpleStringSegmentation correctSegmentation = pair.get2 ();
                System.err.println ("* Segmenting word " + word + ", memory usage: " + MemoryUtils.memoryUsageMessageMB ());
                
                outputWriter.append (word);
                for (SubstitusSetting3 setting : settings) {
                    substitus.setMinCompoundFrequency (setting.getMinWordFrequency ());
                    substitus.setKMostFrequent (setting.getKMostFrequent ());
                    substitus.setSquareSize (setting.getSquareSize ());
                    ProbabilisticStringSegmentation segmentation =
                        substitus.segmentizeAndPrintArff (
                            word,
                            Optional.of (correctSegmentation)
                        );
                    outputWriter.append ("\t" + showFunction.apply (segmentation));
                }
                outputWriter.append ('\n');
                outputWriter.flush ();
            }
            catch (IOException exception) {
                throw new RuntimeException ("Processing entry failed.", exception);
            }
        });
        
        substitus.flush ();
    }
}
