package com.lingea.segmenter.eval;

import com.github.josefplch.utils.data.list.ComparableList;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.ListUtils;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.list.number.DoubleList;
import com.github.josefplch.utils.data.map.MapUtils;
import com.github.josefplch.utils.data.matrix.confusion.ConfusionMatrix;
import com.github.josefplch.utils.data.matrix.confusion.IntegerConfusionMatrix;
import com.github.josefplch.utils.data.set.FunctionalSet;
import com.github.josefplch.utils.data.string.DoubleFormatter;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.tuple.Tuple3;
import com.lingea.segmenter.GlobalSettings;
import com.lingea.segmenter.data.ProbabilisticStringSegmentation;
import com.lingea.segmenter.data.SimpleStringSegmentation;
import com.lingea.segmenter.data.TestSet;
import com.lingea.segmenter.substitus.StringSubstitusTokenizer;
import com.lingea.segmenter.substitus.Substitus;
import com.lingea.segmenter.substitus.data.SubstitusSetting3;
import com.lingea.segmenter.substitus.data.SubstitusSetting4;
import com.lingea.segmenter.substitus.data.TokenizerSettings;
import com.lingea.segmenter.utils.ShowUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Evaluate test of multiple settings and compare the results.
 * 
 * @author  Josef Plch
 * @since   2018-09-19
 * @version 2021-01-18
 */
public class SettingTestEvaluator {
    private static final boolean PRINT_BEST_SETTINGS = true;
    private static final boolean PRINT_SCORE_TABLES = true;
    private final int nBestSettings;
    private final int verbosity;
    private final Writer writer;
    
    public SettingTestEvaluator (Writer writer, int verbosity, int nBestSettings) {
        this.nBestSettings = nBestSettings;
        this.verbosity = verbosity;
        this.writer = writer;
    }
    
    private Optional <Pair <SubstitusSetting4, ConfusionMatrix <Integer>>> defaultSegmenterSetting (
        Map <SubstitusSetting4, ConfusionMatrix <Integer>> cfBySetting
    ) {
        PairList <SubstitusSetting4, ConfusionMatrix <Integer>> defaultSettings =
            MapUtils.toList (cfBySetting)
            .filter (p ->
                p.get1 ().getMinWordFrequency () == 1
                && p.get1 ().getKMostFrequent () == 64
                && p.get1 ().getSquareSize () == 8
                // Oh, float deserialization ...
                && p.get1 ().getThreshold () > Substitus.DEFAULT_NORMALIZATION_MEAN - 0.000_001
                && p.get1 ().getThreshold () < Substitus.DEFAULT_NORMALIZATION_MEAN + 0.000_001
            );
        
        return (
            defaultSettings.isEmpty ()
            ? Optional.empty ()
            : Optional.of (defaultSettings.head ())
        );
    }
    
    private <ST, SG> void evaluateGeneric (
        Stream <Tuple3 <String, ST, SG>> segmentedWords,
        BiFunction <ST, SG, StringList> binarizer,
        TestSet testSet,
        Map <ST, ConfusionMatrix <Integer>> results
    ) throws IOException {
        segmentedWords.forEach (segmentedWord -> {
            String word = segmentedWord.get1 ();
            ST setting = segmentedWord.get2 ();
            SG segmentation = segmentedWord.get3 ();
            
            SimpleStringSegmentation correctSegmentation =
                testSet.lookUp (word)
                .orElseThrow (() -> new NoSuchElementException (
                    "Unknown correct segmentation for word " + word
                ));
            
            if (verbosity >= 2) {
                System.err.println (
                    "* Evaluating word " + word
                    + " (correct: " + correctSegmentation + ") ..."
                );
            }
            
            results.merge (
                setting,
                SegmentationEvaluator.evaluate (
                    SimpleStringSegmentation.read (
                        binarizer.apply (setting, segmentation)
                        .join (GlobalSettings.HARD_DELIMITER_STRING)
                    ),
                    correctSegmentation
                ),
                (matrixA, matrixB) -> {
                    IntegerConfusionMatrix mergedMatrix = new IntegerConfusionMatrix ();
                    mergedMatrix.merge (matrixA);
                    mergedMatrix.merge (matrixB);
                    return mergedMatrix;
                }
            );
        });
    }
    
    private void evaluateSubstitusFileInternal (
        String inputFilePath,
        TestSet testSet,
        Map <SubstitusSetting4, ConfusionMatrix <Integer>> results
    ) throws IOException {
        BufferedReader reader = new BufferedReader (new FileReader (inputFilePath));
        
        Optional <String> headerLine = Optional.empty ();
        String line;
        while (! headerLine.isPresent () && (line = reader.readLine ()) != null) {
            // Skip empty lines and comments.
            if (! line.isEmpty () && ! line.startsWith (GlobalSettings.COMMENT_MARK)) {
                headerLine = Optional.of (line);
            }
        }
        
        // Read the table header, i.e. list of settings.
        FunctionalList <SubstitusSetting3> settings =
            StringList.split (
                "(\t|  +)",
                headerLine.orElseThrow (() -> new RuntimeException ("Header not found."))
            )
            .tail ()
            .map (SubstitusSetting3 :: read);
        
        Stream <Tuple3 <String, SubstitusSetting3, ProbabilisticStringSegmentation>> segmentationsWithoutThreshold =
            reader.lines ()
            .flatMap (line2 -> {
                StringList columns = StringList.split ("(\t|  +)", line2);
                String word = columns.head ();
                FunctionalList <ProbabilisticStringSegmentation> wordSegmentations =
                    columns.tail ().map (ProbabilisticStringSegmentation :: readDouble);
                
                if (wordSegmentations.size () != settings.size ()) {
                    throw new RuntimeException ("Corrupted line: " + line2);
                }
                else {
                    return (
                        ListUtils.zipWith (
                            (setting, segmentation) -> Tuple3.of (word, setting, segmentation),
                            settings,
                            wordSegmentations
                        )
                        .stream ()
                    );
                }
            });
        
        DoubleList thresholds = DoubleList.interval (0.6, 0.8, 0.001);
        Stream <Tuple3 <String, SubstitusSetting4, ProbabilisticStringSegmentation>> segmentationsWithThreshold =
            segmentationsWithoutThreshold.flatMap (tuple ->
                thresholds
                .map (threshold -> tuple.map2 (setting3 -> new SubstitusSetting4 (setting3, threshold)))
                .stream ()
            );
        
        evaluateGeneric (
            segmentationsWithThreshold,
            (setting, segmentation) -> segmentation.binarizeS (setting.getThreshold ()),
            testSet,
            results
        );
        
        reader.close ();
    }
    
    public void evaluateSubstitusFiles (PairList <String, String> pathsAndTestSets) throws IOException {
        Map <SubstitusSetting4, ConfusionMatrix <Integer>> results = new HashMap <> ();
        for (Pair <String, String> fileAndTestSet : pathsAndTestSets) {
            TestSet testSet = TestSet.readFile (fileAndTestSet.get2 ());
            if (verbosity >= 1) {
                System.err.println (
                    "Evaluating file " + fileAndTestSet.get1 ()
                    + ", test set: " + testSet
                );
            }
            evaluateSubstitusFileInternal (
                fileAndTestSet.get1 (),
                testSet,
                results
            );
        }
        if (verbosity >= 1) {
            System.err.println ("Printing the results ...");
        }
        printSegmenterResults (results);
    }
    
    public void evaluateKnownTokenFiles (StringList tokenFilePaths, TestSet testSet) throws IOException {
        if (verbosity >= 1) {
            System.err.println ("Test set: " + testSet);
        }
        
        Map <TokenizerSettings, ConfusionMatrix <Integer>> results = new HashMap <> ();
        System.err.println ("Evaluating token files:");
        for (int i = 0; i < tokenFilePaths.size (); i++) {
            String tokenFilePath = tokenFilePaths.get (i);
            System.err.println ("* " + i + "/" + tokenFilePaths.size () + ": " + tokenFilePath);
            
            StringSubstitusTokenizer tokenizer =
                StringSubstitusTokenizer.loadTokenFile (
                    Character :: toLowerCase,
                    tokenFilePath
                );
            TokenizerSettings setting = new TokenizerSettings (tokenFilePath);
            evaluateGeneric (
                testSet
                    .firsts (StringList :: new)
                    .map (word -> Tuple3.of (word, setting, tokenizer.segmentize (word)))
                    .stream (),
                (setting2, segmentation) -> segmentation,
                testSet,
                results
            );
        }
        
        System.err.println ("Printing the results ...");
        printTokenizerResults (results);
    }
    
    private static <ST, A extends Comparable <A>> ComparableList <A> getUniqueAttributeValues (
        FunctionalSet <ST> settings,
        Function <ST, A> attributeExtractor
    ) {
        return (
            (new ComparableList <> (settings.map (attributeExtractor)))
            .sortAsc ()
        );
    }
    
    private static <K, V1, V2> Map <K, V2> mapOnValue (
        Function <V1, V2> f,
        Map <K, V1> cfBySetting
    ) throws IOException {
        Map <K, V2> result = new HashMap <> ();
        for (K setting : cfBySetting.keySet ()) {
            result.put (setting, f.apply (cfBySetting.get (setting)));
        }
        return result;
    }
    
    private <ST extends Comparable <ST>, N extends Number> void printBestSettings (
        Map <ST, ConfusionMatrix <N>> cfBySetting,
        Function <ConfusionMatrix <N>, Double> f
    ) throws IOException {
        PairList <ST, ConfusionMatrix <N>> bestSettingsByScore =
            MapUtils.toList (cfBySetting)
            .map (pair -> Tuple3.of (pair.get1 (), pair.get2 (), f.apply (pair.get2 ())))
            .filter (triple -> ! triple.get3 ().isNaN ())
            // Order by score and setting (if the scores are equal).
            .sortBy (
                Comparator.comparing (
                    triple -> Pair.of (-1 * triple.get3 (), triple.get1 ()),
                    Pair.LexicographicalComparator.natural ()
                )
            )
            .mapToPair (triple -> Pair.of (triple.get1 (), triple.get2 ()))
            .take (nBestSettings);
        
        writer.append ("Best settings\n");
        for (Pair <ST, ConfusionMatrix <N>> pair : bestSettingsByScore) {
            writer.append (pair.get1 () + ": " + showSettingDetails (pair.get2 (), f) + "\n");
        }
    }
    
    private void printSegmenterResults (Map <SubstitusSetting4, ConfusionMatrix <Integer>> cfBySetting) throws IOException {
        Optional <Pair <SubstitusSetting4, ConfusionMatrix <Integer>>> defaultSetting = defaultSegmenterSetting (cfBySetting);
        printResults (cfBySetting, defaultSetting, SettingTestEvaluator :: showSegmenterTables, "Overall accuracy", ConfusionMatrix :: getAccuracy);
        printResults (cfBySetting, defaultSetting, SettingTestEvaluator :: showSegmenterTables, "Positive F-score", ConfusionMatrix :: getPositiveFMeasure);
        printResults (cfBySetting, defaultSetting, SettingTestEvaluator :: showSegmenterTables, "Matthews correlation coefficient (MCC)", ConfusionMatrix :: getMcc);
    }
    
    private void printTokenizerResults (Map <TokenizerSettings, ConfusionMatrix <Integer>> cfBySetting) throws IOException {
        printResults (cfBySetting, Optional.empty (), SettingTestEvaluator :: showTokenizerTables, "Overall accuracy", ConfusionMatrix :: getAccuracy);
        printResults (cfBySetting, Optional.empty (), SettingTestEvaluator :: showTokenizerTables, "Positive F-score", ConfusionMatrix :: getPositiveFMeasure);
        printResults (cfBySetting, Optional.empty (), SettingTestEvaluator :: showTokenizerTables, "Matthews correlation coefficient (MCC)", ConfusionMatrix :: getMcc);
    }
    
    private <ST extends Comparable <ST>> void printResults (
        Map <ST, ConfusionMatrix <Integer>> cfBySetting,
        Optional <Pair <ST, ConfusionMatrix <Integer>>> defaultSetting,
        BiFunction <FunctionalSet <ST>, Map <ST, Double>, String> tablePrinter,
        String title,
        Function <ConfusionMatrix <Integer>, Double> f
    ) throws IOException {
        writer.append (title + "\n");
        writer.append (
            defaultSetting
            .map (pair -> pair.get1 () + ": " + showSettingDetails (pair.get2 (), f))
            .orElse ("Default setting not found")
            + "\n"
        );
        writer.append ('\n');
        
        if (PRINT_BEST_SETTINGS) {
            printBestSettings (cfBySetting, f);
            writer.append ('\n');
        }
        
        if (PRINT_SCORE_TABLES) {
            writer.append (
                tablePrinter.apply (
                    FunctionalSet.from (cfBySetting.keySet ()),
                    mapOnValue (f, cfBySetting)
                )
            );
        }
        
        writer.flush ();
    }
    
    private static String showSegmenterTables (
        FunctionalSet <SubstitusSetting4> settings,
        Map <SubstitusSetting4, Double> settingScores
    ) {
        StringBuilder resultBuilder = new StringBuilder ();
        
        resultBuilder.append (
            showTablePair (
                "minWordFrequency : kMostFrequent",
                settingScores,
                settings,
                SubstitusSetting4 :: getMinWordFrequency,
                SubstitusSetting4 :: getKMostFrequent
            )
        );
        
        resultBuilder.append (
            showTablePair (
                "minWordFrequency : squareSize",
                settingScores,
                settings,
                SubstitusSetting4 :: getMinWordFrequency,
                SubstitusSetting4 :: getSquareSize
            )
        );
        
        resultBuilder.append (
            showTablePair (
                "kMostFrequent : squareSize",
                settingScores,
                settings,
                SubstitusSetting4 :: getKMostFrequent,
                SubstitusSetting4 :: getSquareSize
            )
        );
        
        return resultBuilder.toString ();
    }
    
    private static <N extends Number> String showSettingDetails (ConfusionMatrix <N> confusionMatrix, Function <ConfusionMatrix <N>, Double> f) {
        return (
            ShowUtils.showAsPercent (f.apply (confusionMatrix))
            + ", Acc " + DoubleFormatter.POINT_3.format (confusionMatrix.getAccuracy ())
            + ", Pre " + DoubleFormatter.POINT_3.format (confusionMatrix.getPositivePrecision ())
            + ", Rec " + DoubleFormatter.POINT_3.format (confusionMatrix.getPositiveRecall ())
            + ", FSc " + DoubleFormatter.POINT_3.format (confusionMatrix.getPositiveFMeasure ())
            + ", MCC " + DoubleFormatter.POINT_3.format (confusionMatrix.getMcc ())
            + ", CM = " + confusionMatrix
        );
    }
    
    private static <ST, A extends Comparable <A>, B extends Comparable <B>> String showTable (
        FunctionalSet <ST> settings,
        Map <ST, Double> scoresBySetting,
        Function <ST, A> rowAttributeExtractor,
        Function <ST, B> columnAttributeExtractor,
        BiFunction <ST, Double, String> cellPrinter
    ) {
        StringBuilder resultBuilder = new StringBuilder ();
        
        ComparableList <B> columnAttributeValues =
            getUniqueAttributeValues (
                settings,
                columnAttributeExtractor
            );
        
        // Table header.
        resultBuilder.append ("Score");
        for (B columnAttributeValue : columnAttributeValues) {
            resultBuilder.append ("\t" + columnAttributeValue);
        }
        resultBuilder.append ('\n');
        
        // Table body.
        PairList <ST, Double> settingScores =
            MapUtils.toList (scoresBySetting);
        for (A rowAttributeValue : getUniqueAttributeValues (settings, rowAttributeExtractor)) {
            resultBuilder.append (rowAttributeValue.toString ());
            for (B columnAttributeValue : columnAttributeValues) {
                Optional <Pair <ST, Double>> bestPair =
                    settingScores
                    .filter (entry ->
                        Objects.equals (rowAttributeExtractor.apply (entry.get1 ()), rowAttributeValue)
                        && Objects.equals (columnAttributeExtractor.apply (entry.get1 ()), columnAttributeValue)
                    )
                    .filter (pair -> ! pair.get2 ().isNaN ())
                    .maximumBy (Comparator.comparing (Pair :: get2));
                
                resultBuilder.append ('\t');
                resultBuilder.append (
                    bestPair
                    .map (pair -> cellPrinter.apply (pair.get1 (), pair.get2 ()))
                    .orElse ("N/A")
                );
            }
            resultBuilder.append ('\n');
        }
        
        return resultBuilder.toString ();
    }
    
    private static <ST, A extends Comparable <A>, B extends Comparable <B>> String showTablePair (
        String title,
        Map <ST, Double> scoreBySetting,
        FunctionalSet <ST> settings,
        Function <ST, A> rowAttributeExtractor,
        Function <ST, B> columnAttributeExtractor
    ) {
        StringBuilder resultBuilder = new StringBuilder ();
        
        resultBuilder.append (title + "\n");
        resultBuilder.append (
            showTable (
                settings,
                scoreBySetting,
                rowAttributeExtractor,
                columnAttributeExtractor,
                (setting, score) -> DoubleFormatter.POINT_3.format (score)
            )
        );
        
        resultBuilder.append ('\n');
        
        resultBuilder.append ("Optimal settings\n");
        resultBuilder.append (
            showTable (
                settings,
                scoreBySetting,
                rowAttributeExtractor,
                columnAttributeExtractor,
                (setting, score) -> setting.toString ()
            )
        );
        
        resultBuilder.append ('\n');
        return resultBuilder.toString ();
    }
    
    private static String showTokenizerTables (
        FunctionalSet <TokenizerSettings> settings,
        Map <TokenizerSettings, Double> settingScores
    ) {
        StringBuilder resultBuilder = new StringBuilder ();
        
        resultBuilder.append (
            showTablePair (
                "maxNgramLevel : maxNgramLength",
                settingScores,
                settings,
                TokenizerSettings :: getMaxNgramLevel,
                TokenizerSettings :: getMaxNgramLength
            )
        );
        
        resultBuilder.append (
            showTablePair (
                "maxNgramLevel : minNgramFrequency",
                settingScores,
                settings,
                TokenizerSettings :: getMaxNgramLevel,
                TokenizerSettings :: getMinNgramFrequency
            )
        );
        
        resultBuilder.append (
            showTablePair (
                "maxNgramLevel : minNgramProductivity",
                settingScores,
                settings,
                TokenizerSettings :: getMaxNgramLevel,
                TokenizerSettings :: getMinNgramProductivity
            )
        );
        
        resultBuilder.append (
            showTablePair (
                "maxNgramLevel : maxTokenLength",
                settingScores,
                settings,
                TokenizerSettings :: getMaxNgramLevel,
                TokenizerSettings :: getMaxTokenLength
            )
        );
        
        resultBuilder.append (
            showTablePair (
                "maxNgramLevel : minTokenFrequency",
                settingScores,
                settings,
                TokenizerSettings :: getMaxNgramLevel,
                TokenizerSettings :: getMinTokenFrequency
            )
        );
        
        resultBuilder.append (
            showTablePair (
                "maxNgramLevel : minTokenProductivity",
                settingScores,
                settings,
                TokenizerSettings :: getMaxNgramLevel,
                TokenizerSettings :: getMinTokenProductivity
            )
        );
        
        resultBuilder.append (
            showTablePair (
                "maxNgramLength : maxTokenLength",
                settingScores,
                settings,
                TokenizerSettings :: getMaxNgramLength,
                TokenizerSettings :: getMaxTokenLength
            )
        );
        
        resultBuilder.append (
            showTablePair (
                "minNgramFrequency : minTokenFrequency",
                settingScores,
                settings,
                TokenizerSettings :: getMinNgramFrequency,
                TokenizerSettings :: getMinTokenFrequency
            )
        );
        
        resultBuilder.append (
            showTablePair (
                "minNgramProductivity : minTokenProductivity",
                settingScores,
                settings,
                TokenizerSettings :: getMinNgramProductivity,
                TokenizerSettings :: getMinTokenProductivity
            )
        );
        
        return resultBuilder.toString ();
    }
}
