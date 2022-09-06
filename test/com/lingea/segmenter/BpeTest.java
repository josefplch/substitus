package com.lingea.segmenter;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.string.StringUtils;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.tuple.Tuple3;
import com.lingea.segmenter.data.BoundaryStrings;
import com.lingea.segmenter.bpe.StringBpe;
import com.lingea.segmenter.bpe.metric.CommonFrequency;
import com.lingea.segmenter.bpe.metric.LogDice;
import com.lingea.segmenter.bpe.metric.MiScore;
import com.lingea.segmenter.bpe.metric.ModifiedDiceA;
import com.lingea.segmenter.bpe.metric.ModifiedDiceB;
import com.lingea.segmenter.bpe.metric.ModifiedMiScore;
import com.lingea.segmenter.bpe.metric.AssociationMetric;
import com.lingea.segmenter.data.token.Token;
import com.lingea.segmenter.utils.ShowUtils;
import com.lingea.segmenter.data.frequencyList.FrequencyListReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author  Josef Plch
 * @since   2018-05-05
 * @version 2019-11-26
 */
public abstract class BpeTest {
    private static final BoundaryStrings BOUNDARY_STRINGS = BoundaryStrings.NONE;
    private static final int FREQUENCY_LIST_LIMIT = 1000 * 1000;
    private static final int MIN_FREQUENCY = 2;
    private static final int STEP_LIMIT = 100; // 8192;
    
    public static void main (String [] args) {
        try {
            testBpe ();
        }
        catch (IOException exception) {
            System.err.println (exception);
        }
    }
    
    private static String showTokenizedString (List <String> tokens) {
        return (
            String.join (
                GlobalSettings.HARD_DELIMITER_STRING,
                tokens
            )
        );
    }
    
    private static String showTokenizedWords (List <List <String>> words, boolean breakLines) {
        StringBuilder result = new StringBuilder ();
        int entryIndex = 1;
        for (List <String> word : words) {
            String wordString = showTokenizedString (word);
            result.append ("[").append (wordString).append ("]");
            if (breakLines && entryIndex % 10 == 0) {
                result.append ("\n");
            }
            else {
                result.append (" ");
            }
            entryIndex++;
        }
        return result.toString ();
    }
    
    private static void testBpe () throws IOException {
        testSettings (
            Arrays.asList (
                Tuple3.of (DataPaths.FREQUENCY_DE_ALL, 0, new CommonFrequency ())
            ),
            FunctionalList.of (
                TestData.EN_HEAD_22.toLowerCase ().split (" ")
            ).stream ()
        );
    }
    
    private static void testSettings (
        List <Tuple3 <String, Integer, AssociationMetric>> settings,
        Stream <String> testWords
    ) throws FileNotFoundException, IOException {
        Map <Tuple3 <String, Integer, AssociationMetric>, StringBpe> bpeBySetting = new HashMap <> ();
        for (Tuple3 <String, Integer, AssociationMetric> setting : settings) {
            String frequencyListPath = setting.get1 ();
            Integer columnIndex      = setting.get2 ();
            AssociationMetric metric = setting.get3 ();
            
            System.out.println (
                "frequencyListPath = " + frequencyListPath
                + ",\ncolumnIndex = " + columnIndex
                + ",\nmetric = " + metric
                + ",\nminFrquency = " + MIN_FREQUENCY
                + ",\nboundaryStrings = " + BOUNDARY_STRINGS
                + ",\nsteps = " + STEP_LIMIT
            );
            
            BufferedReader frequencyListReader = new BufferedReader (new FileReader (frequencyListPath));
            StringBpe bpe =
                StringBpe.loadTrainStrings (
                    metric,
                    MIN_FREQUENCY,
                    BOUNDARY_STRINGS,
                    frequencyListReader.lines ()
                        .limit (FREQUENCY_LIST_LIMIT)
                        .map (FrequencyListReader :: readLine)
                        /*
                        .flatMap (pair ->
                            Stream.of (pair.get1 ().split (" "))
                            .map (x -> Pair.of (x, pair.get2 ()))
                        )
                        */
                        .map (pair -> pair.map1 (String :: toLowerCase))
                );
            frequencyListReader.close ();
            int finalStep = bpe.makeSteps (STEP_LIMIT);
            System.out.println ("finalStep = " + finalStep);
            
            // Print token frequencies.
            Map <Token <Character>, Long> tokenFrequencies = bpe.getTokenFrequencies ();
            PairList <Long, String> tokenFrequencyList = new PairList <> ();
            for (Map.Entry <Token <Character>, Long> entry : tokenFrequencies.entrySet ()) {
                tokenFrequencyList.add (
                    Pair.of (
                        entry.getValue (),
                        StringUtils.charListToString (entry.getKey ().getAtoms ())
                    )
                );
            }
            Comparator <Pair <Long, String>> comparator = Pair.LexicographicalComparator.natural ();
            Collections.sort (tokenFrequencyList, comparator.reversed ());
            System.out.println ("tokenFrequencies = " + FunctionalList.from (tokenFrequencyList).take (10) + " ...");

            // Print merged pairs.
            System.out.println ("mergedPairs = " + FunctionalList.from (bpe.getMergedPairs ()).take (10) + " ...");
            System.out.println ();
            
            bpe.saveModel (
                DataPaths.BPE_MODEL_DIR
                + "/xx_" + BOUNDARY_STRINGS.getName ()
                + "_t" + (FREQUENCY_LIST_LIMIT / 1000) + "k"
                + "_s" + STEP_LIMIT
                + "_NEW.txt"
            );
            bpeBySetting.put (setting, bpe);
        }
        
        // Separator line.
        System.out.println (ShowUtils.horizontalLine (1 + settings.size ()));
        
        // Table body.
        testWords.forEach (word -> {
            System.out.print (word);
            for (Tuple3 <String, Integer, AssociationMetric> setting : settings) {
                StringBpe bpe = bpeBySetting.get (setting);
                List <String> tokenized = bpe.segmentize (word);
                System.out.print ("\t" + showTokenizedString (tokenized));
            }
            System.out.println ();
        });
        System.out.println ();
    }
}
