package com.lingea.segmenter.substitus;

import com.github.josefplch.utils.data.OptionalUtils;
import com.github.josefplch.utils.data.arff.ArffWriter;
import com.github.josefplch.utils.data.arff.ArffAttribute;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.list.number.DoubleList;
import com.github.josefplch.utils.data.list.number.IntegerList;
import com.github.josefplch.utils.data.list.number.LongList;
import com.github.josefplch.utils.data.math.Mean;
import com.github.josefplch.utils.data.math.VectorUtils;
import com.github.josefplch.utils.data.matrix.UniformKeyMatrix;
import com.github.josefplch.utils.data.matrix.UniformKeySparseMatrix;
import com.github.josefplch.utils.data.set.PairSet;
import com.github.josefplch.utils.data.tree.FrequencyTrie;
import com.github.josefplch.utils.data.tree.HashTrie;
import com.github.josefplch.utils.data.string.AlignmentUtils;
import com.github.josefplch.utils.data.string.DoubleFormatter;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.tuple.UniformPair;
import com.github.josefplch.utils.system.MemoryUtils;
import com.lingea.segmenter.Application;
import com.lingea.segmenter.GlobalSettings;
import com.lingea.segmenter.ProbabilisticSegmenter;
import com.lingea.segmenter.data.ProbabilisticSegmentation;
import com.lingea.segmenter.data.SimpleStringSegmentation;
import com.lingea.segmenter.substitus.data.AffixInfo;
import com.lingea.segmenter.substitus.data.AffixScores;
import com.lingea.segmenter.substitus.data.CompoundInfo;
import com.lingea.segmenter.substitus.data.FrequencyTriePair;
import com.lingea.segmenter.utils.ShowUtils;
import static com.lingea.segmenter.utils.ShowUtils.makeColumn;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @param <A> Type of the basic, atomic element (typically: byte or character).
 * 
 * The segmenter is based on the principle of repeatability.
 * 
 * Uncovered problems in word segmentation:
 * - circumfixes (ge-spiel-t)
 * - bounded alternations (big?ger, sup?port, immortal)
 * - cranberry morphemes / fossilized terms (twi-light)
 * 
 * See: Některé problémy morfémové analýzy češtiny.
 * 
 * @author  Josef Plch
 * @since   2018-05-12
 * @version 2021-01-19
 */
public class Substitus <A> implements ProbabilisticSegmenter <A>, Closeable, Flushable {
    // The number of attributes must correspond to the table size (max: 2 * TS).
    private static final IntegerList ATTRIBUTE_NUMBERS = IntegerList.ofNumbers (1, 2, 4, 8, 16);
    private static final IntegerList ATTRIBUTE_NUMBERS_2 = ATTRIBUTE_NUMBERS.filter (n -> n > 1);
    
    // Czech: 0.690, English: 0.709.
    public static final double DEFAULT_NORMALIZATION_MEAN = 0.70;
    public static final int DEFAULT_K_MOST_FREQUENT = 64;
    public static final int DEFAULT_SQUARE_SIZE = 8;
    
    // Do not remember compounds longer than the given constant.
    private static final int MAX_COMPOUND_LENGTH = 256;
    
    // Last atom of the alternative prefix must be different from the tested prefix.
    // First atom of the alternative suffix must be different from the tested suffix.
    private static final boolean ONLY_DIFFERENT_BOUNDARY_CHARS = true;
    
    private final Optional <ArffWriter> arffWriter;
    private final UnaryOperator <A> atomPreprocessor;
    private final Writer outputWriter;
    private final FrequencyTriePair <A> triePair;
    private final int verbosity;
    
    private int minCompoundFrequency = 1;
    private int kMostFrequent = DEFAULT_K_MOST_FREQUENT;
    private int squareSize = DEFAULT_SQUARE_SIZE;
    
    // Time counters.
    private static final boolean PRINT_EXECUTION_TIME = true;
    private long arffTime = 0;
    private long bigCompoundTime = 0;
    private long prefixTime = 0;
    private long suffixTime = 0;
    private long similarTime = 0;
    private long totalTime = 0;
    
    public Substitus (UnaryOperator <A> atomPreprocessor, Writer outputWriter, Optional <Pair <Writer, Boolean>> arffSetting, int verbosity) throws IOException {
        this (new FrequencyTriePair <> (4), atomPreprocessor, outputWriter, arffSetting, verbosity);
    }
    
    protected Substitus (FrequencyTriePair <A> triePair, UnaryOperator <A> atomPreprocessor, Writer outputWriter, Optional <Pair <Writer, Boolean>> arffSetting, int verbosity) throws IOException {
        if (! arffSetting.isPresent ()) {
            this.arffWriter = Optional.empty ();
        }
        else {
            Pair <Writer, Boolean> setting = arffSetting.get ();
            List <ArffAttribute> attributes = arffAttributes ();
            ArffWriter writer = new ArffWriter (setting.get1 (), attributes);
            int numberOfFunctions = attributes.size () - 2;
            if (setting.get2 ()) {
                writer.writeHead (
                    "substitus_" + numberOfFunctions + "_scores",
                    StringList.ofStrings (
                        "Train data for Substitus segmenter",
                        "",
                        "Attributes: " + attributes.size () + " (segmentation, " + numberOfFunctions + " score functions, and the class)"
                    )
                );
            }
            this.arffWriter = Optional.of (writer);
        }
        this.atomPreprocessor = atomPreprocessor;
        this.outputWriter = outputWriter;
        this.triePair = triePair;
        this.verbosity = verbosity;
    }
    
    // TODO: Simplify.
    private Pair <Double, Optional <List <Object>>> addArffEntry (
        FunctionalList <A> testedPrefix,
        FunctionalList <A> testedSuffix,
        UniformKeyMatrix <FunctionalList <A>, CompoundInfo> compoundSquare,
        DoubleList orderedFrequencyScores,
        DoubleList orderedLengthScores,
        DoubleList orderedPredictabilityScores,
        DoubleList orderedSimilarityScores,
        DoubleList orderedMixedScores
    ) throws IOException {
        int maxTotal = squareSize * squareSize;
        
        // FS.
        DoubleList fsList = addZeros (orderedFrequencyScores);
        DoubleList fsAvgList = arithmeticMeans (fsList);
        Double fsTotal =
            // Warning: It is not equal to the arithmetic mean!
            FunctionalList.from (compoundSquare.getValues ())
            .mapToDouble (this :: frequencyScore)
            .sum ()
            / maxTotal;
        
        // LS.
        DoubleList lsList = addZeros (orderedLengthScores);
        DoubleList lsAvgList = arithmeticMeans (lsList);
        Double lsTotal =
            // Warning: It is not equal to the arithmetic mean!
            FunctionalList.from (compoundSquare.getExistingKeys ())
            .mapToInteger (affixPair -> affixPair.get1 ().size () + affixPair.get2 ().size ())
            .mapToDouble (Substitus :: lengthScore)
            .sum ()
            / maxTotal;
        
        // PS.
        DoubleList psList = addZeros (orderedPredictabilityScores);
        DoubleList psAvgList = arithmeticMeans (psList);
        Double psTotal =
            // Warning: It is not equal to the arithmetic mean!
            FunctionalList.from (compoundSquare.getValues ())
            .mapToDouble (Substitus :: predictabilityScore)
            .sum ()
            / maxTotal;
        
        // SS: since 2019-12-20.
        DoubleList ssList = addZeros (orderedSimilarityScores);
        DoubleList ssAvgList = arithmeticMeans (ssList);
        Double ssTotal =
            // Warning: It is not equal to the arithmetic mean!
            FunctionalList.from (compoundSquare.getValues ())
            .mapToDouble (CompoundInfo :: getHSimilarity)
            .sum ()
            / maxTotal;
        
        // MS.
        DoubleList mixList = addZeros (orderedMixedScores);
        DoubleList mixAvgList = arithmeticMeans (mixList);
        Double mixTotal = Mean.arithmeticMean (fsTotal, psTotal, lsTotal);
        
        int prefixLength = testedPrefix.size ();
        int suffixLength = testedSuffix.size ();
        Double affixMin = lengthScore (Math.min (prefixLength, suffixLength));
        Double affixMax = lengthScore (Math.max (prefixLength, suffixLength));
        Double affixSum = lengthScore (prefixLength + suffixLength);
        
        // ---------------------------------------------------------------------
        
        Optional <List <Object>> arffInstance;
        if (! arffWriter.isPresent ()) {
            arffInstance = Optional.empty ();
        }
        else {
            List <Object> attributes = new FunctionalList <> ();
            attributes.add (showSegmentation (testedPrefix, testedSuffix));
            attributes.addAll (
                combineAttributeValues (
                    FunctionalList.concat (
                        selectScores (fsList),
                        selectScores2 (fsAvgList),
                        Arrays.asList (fsTotal),

                        selectScores (lsList),
                        selectScores2 (lsAvgList),
                        Arrays.asList (lsTotal),

                        selectScores (psList),
                        selectScores2 (psAvgList),
                        Arrays.asList (psTotal),

                        selectScores (ssList),
                        selectScores2 (ssAvgList),
                        Arrays.asList (ssTotal),
                        
                        selectScores (mixList),
                        selectScores2 (mixAvgList),
                        Arrays.asList (mixTotal),

                        Arrays.asList (affixMin),
                        Arrays.asList (affixMax),
                        Arrays.asList (affixSum)
                    )
                )
            );
            arffInstance = Optional.of (attributes);
        }
        
        // ---------------------------------------------------------------------
        
        String segmentation = showSegmentation (testedPrefix, testedSuffix);
        String detailsAboutDivision = "Segmentation " + segmentation + ": ";
        
        Double segmentability;
        if (compoundSquare.isEmpty ()) {
            segmentability = 0.0;
            detailsAboutDivision += "  0.00% ... no evidence";
        }
        else {
            // Best score functions for Czech, among 45,000 variants:
            // AriM 3 LSAvg(2-4) 1 MixAvg(5-16).
            
            // Auxiliary values.
            // double ls3 = lsList.get (3 - 1);
            double lsAvgT = lsAvgList.get (Math.min (squareSize - 1, lsAvgList.size () - 1));
            // double mixAvg10 = mixAvgList.get (10 - 1);
            double mixAvgT = mixAvgList.get (Math.min (squareSize - 1, mixAvgList.size () - 1));
            
            // Note: CS*EN*CS failed. For each individual language, it was
            // better to use the setting found by merged CS+EN+HU. (2018-10-12)
            // Best attributes according to CS+EN+HU / OneR 100:
            // 1. AriM_3LSAvg7_1Mix8
            // 2. AriM_3LSAvg9_1MixAvg9
            // 3. AriM_3LSAvg9_1MixAvg8
            // 4. AriM_3LS3_1Mix7
            // 5. AriM_3LSAvg10_1Mix7
            // See details below. Based on these results, 3LSAvgT_1MixAvgT was
            // chosen.
            
            // (m1, f64, t8) / AriM_3LS3_1MixAvg10 / CS + EN + HU / 3291 instances / 2018-10-25
            // CS+EN+HU, OneR 50:     AriM_3LS3_1MixAvg10 >= 0.715; Weka: (600, 234 |  80, 2377), acc. 0.905, F-m. 0.793, MCC 0.738
            // CS, OneR 50:           AriM_3LS3_1MixAvg10 >= 0.689; Weka: (336,  56 |  53,  744), acc. 0.908, F-m. 0.860, MCC 0.792
            // EN, OneR 50:           AriM_3LS3_1MixAvg10 >= 0.707; Weka: (129,  48 |  51,  801), acc. 0.904, F-m. 0.723, MCC 0.665
            // HU, OneR 50:           AriM_3LS3_1MixAvg10 >= 0.721; Weka: (191,  74 |  37,  771), acc. 0.897, F-m. 0.775, MCC 0.712
            // segmentability = Mean.arithmeticMean (ls3, ls3, ls3, mixAvg10);
            
            // (m1, f64, t8) / AriM_3LSAvg8_1MixAvg8 / CS + EN + HU / 3291 instances / 2018-10-25
            // CS+EN+HU, OneR 50:     AriM_3LSAvg8_1MixAvg8 >= 0.707; Weka: (597, 237 |  86, 2371), acc. 0.902, F-m. 0.787, MCC 0.730
            // CS, OneR 50:           AriM_3LSAvg8_1MixAvg8 >= 0.690; Weka: (313,  79 |  27,  770), acc. 0.911, F-m. 0.855, MCC 0.795
            // EN, OneR 50:           AriM_3LSAvg8_1MixAvg8 >= 0.709; Weka: (121,  56 |  22,  830), acc. 0.904, F-m. 0.756, MCC 0.718
            // HU, OneR 55:           AriM_3LSAvg8_1MixAvg8 >= 0.712; Weka: (189,  76 |  41,  767), acc. 0.891, F-m. 0.764, MCC 0.696
            // segmentability = Mean.arithmeticMean (lsAvg8, lsAvg8, lsAvg8, mixAvg8);
            
            // Best as of 2019-01-01.
            segmentability = Mean.arithmeticMean (lsAvgT, lsAvgT, lsAvgT, mixAvgT);
            
            StringList prefixes = compoundSquare.getRowKeys ().mapToString (p -> showList (p) + "-").take (3);
            StringList suffixes = compoundSquare.getColumnKeys ().mapToString (s -> "-" + showList (s)).take (3);
            detailsAboutDivision +=
                // Segmentability.
                AlignmentUtils.toRight (
                    ShowUtils.showAsPercent (normalize (segmentability, DEFAULT_NORMALIZATION_MEAN)),
                    7
                )
                // Example prefixes and suffixes.
                + " ... "
                + (Application.HTML_MODE ? ("<span class=\"alternativePrefixes\">" + prefixes + "</span>") : prefixes)
                + " × "
                + (Application.HTML_MODE ? ("<span class=\"alternativeSuffixes\">" + suffixes + "</span>") : suffixes);
        }
        
        if (Application.HTML_MODE) {
            detailsAboutDivision =
                "<div class=\"details\">"
                    + detailsAboutDivision
                + "</div>";
        }
        
        if (verbosity >= 1) {
            outputWriter.append (detailsAboutDivision + "\n");
        }
        
        return Pair.of (segmentability, arffInstance);
    }
    
    // Append some extra zeros to reach the required length.
    private static DoubleList addZeros (DoubleList scores) {
        DoubleList result = new DoubleList (scores);
        for (int i = result.size (); i < ATTRIBUTE_NUMBERS.last (); i++) {
            result.add (0.0);
        }
        return result;
    }
    
    private static FunctionalList <ArffAttribute> arffAttributes () {
        StringList numericAttributeNames =
            combineAttributeNames (
                FunctionalList.concat (
                    // Frequency scores.
                    enumeratedNames ("FS",     ATTRIBUTE_NUMBERS),
                    enumeratedNames ("FSAvg",  ATTRIBUTE_NUMBERS_2),
                    Arrays.asList   ("FSTotal"),
                    
                    // Length scores.
                    enumeratedNames ("LS",     ATTRIBUTE_NUMBERS),
                    enumeratedNames ("LSAvg",  ATTRIBUTE_NUMBERS_2),
                    Arrays.asList   ("LSTotal"),
                    
                    // Predictability scores.
                    enumeratedNames ("PS",     ATTRIBUTE_NUMBERS),
                    enumeratedNames ("PSAvg",  ATTRIBUTE_NUMBERS_2),
                    Arrays.asList   ("PSTotal"),
                    
                    // Similarity scores.
                    enumeratedNames ("SS",     ATTRIBUTE_NUMBERS),
                    enumeratedNames ("SSAvg",  ATTRIBUTE_NUMBERS_2),
                    Arrays.asList   ("SSTotal"),
                    
                    // Mixed scores.
                    enumeratedNames ("MS",    ATTRIBUTE_NUMBERS),
                    enumeratedNames ("MSAvg", ATTRIBUTE_NUMBERS_2),
                    Arrays.asList   ("MSTotal"),
                    
                    Arrays.asList   ("AffMin"),
                    Arrays.asList   ("AffMax"),
                    Arrays.asList   ("AffSum")
                )
            );
        
        FunctionalList <ArffAttribute> result = new FunctionalList <> ();
        result.add (ArffAttribute.string ("segmentation"));
        result.addAll (numericAttributeNames.map (ArffAttribute :: numeric));
        result.add (ArffAttribute.enumeration ("class", Arrays.asList ("1", "0")));
        // result.add (ArffAttribute.numeric ("class"));
        return result;
    }
    
    private static DoubleList arithmeticMeans (DoubleList values) {
        return (
            IntegerList.interval (1, ATTRIBUTE_NUMBERS.last ())
            .mapToDouble (n -> values.take (n).arithmeticMean ())
        );
    }
    
    @Override
    public void close () throws IOException {
        if (arffWriter.isPresent ()) {
            arffWriter.get ().close ();
        }
        this.outputWriter.close ();
    }
    
    /**
     * Functions for combining attribute names & values.
     * 
     * @return List of combinators.
     */
    private static PairList <BinaryOperator <String>, BinaryOperator <Double>> combinationFunctions () {
        return PairList.ofPairs (
            // 1 : 1
            Pair.of ((n1, n2) -> "AriM_" + n1 + "_" + n2,   (x, y) -> Mean.arithmeticMean (x, y)),
            Pair.of ((n1, n2) -> "GeoM_" + n1 + "_" + n2,   (x, y) -> Mean.geometricMean  (x, y)),
            Pair.of ((n1, n2) -> "HarM_" + n1 + "_" + n2,   (x, y) -> Mean.harmonicMean   (x, y)),
            Pair.of ((n1, n2) -> "QuaM_" + n1 + "_" + n2,   (x, y) -> Mean.quadraticMean  (x, y)),
            
            // 1 : 2
            Pair.of ((n1, n2) -> "AriM_1" + n1 + "_2" + n2, (x, y) -> Mean.arithmeticMean (x, y, y)),
            Pair.of ((n1, n2) -> "AriM_2" + n1 + "_1" + n2, (x, y) -> Mean.arithmeticMean (x, x, y)),
            
            // 1 : 3
            Pair.of ((n1, n2) -> "AriM_1" + n1 + "_3" + n2, (x, y) -> Mean.arithmeticMean (x, y, y, y)),
            Pair.of ((n1, n2) -> "AriM_3" + n1 + "_1" + n2, (x, y) -> Mean.arithmeticMean (x, x, x, y))
        );
    }
    
    private static StringList combineAttributeNames (FunctionalList <String> names) {
        // System.out.println ("Names (" + names.size () + "): " + names);
        return combineInto (combinationFunctions ().firsts (), names, StringList :: new);
    }
    
    private static DoubleList combineAttributeValues (FunctionalList <Double> values) {
        // System.out.println ("Values (" + values.size () + "): " + values);
        return combineInto (combinationFunctions ().seconds (), values, DoubleList :: new);
    }
    
    private static <A, LA extends FunctionalList <A>> LA combineInto (
        FunctionalList <BinaryOperator <A>> combinators,
        FunctionalList <A> values,
        Supplier <LA> constructor
    ) {
        LA result = constructor.get ();
        result.addAll (values);
        PairList.cartesianProduct (combinators, values.uniquePairs ())
        .forEach (pair ->
            result.add (
                pair.get1 ().apply (
                    pair.get2 ().get1 (),
                    pair.get2 ().get2 ()
                )
            )
        );
        return result;
    }
    
    private Long compoundFrequency (FunctionalList <A> compound) {
        return triePair.getFrequency (compound);
    }
    
    private Pair <Double, Optional <List <Object>>> computeScores (
        FunctionalList <A> testedPrefix,
        FunctionalList <A> testedSuffix,
        PairList <FunctionalList <A>, AffixScores> orderedRows,
        PairList <FunctionalList <A>, AffixScores> orderedColumns,
        UniformKeyMatrix <FunctionalList <A>, CompoundInfo> compoundSquare
    ) throws IOException {
        // Combine row and column scores.
        // TODO: Different type of combination, e.g. zipWith max?
        FunctionalList <AffixScores> rowsAndColumnScores =
            FunctionalList.concat (orderedRows, orderedColumns)
            .map (Pair :: get2);
        
        DoubleList orderedFrequencyScores =
            getOrderedScores (rowsAndColumnScores, AffixScores :: getFrequencyScore);
        DoubleList orderedLengthScores =
            getOrderedScores (rowsAndColumnScores, AffixScores :: getLengthScore);
        DoubleList orderedPredictabilityScores =
            getOrderedScores (rowsAndColumnScores, AffixScores :: getPredictabilityScore);
        DoubleList orderedSimilarityScores =
            getOrderedScores (rowsAndColumnScores, AffixScores :: getSimilarity);
        DoubleList orderedMixedScores =
            getOrderedScores (rowsAndColumnScores, AffixScores :: getMixedScore);
        
        // Print the compound square & best scores.
        if (verbosity >= 2) {
            outputWriter.append ('\n');
            if (compoundSquare.size () > 0) {
                outputWriter.append ("Frequencies of affix combinations:\n");
                outputWriter.append (showCompoundSquare (orderedRows, orderedColumns, compoundSquare));;
                if (verbosity >= 3) {
                    int tableWidth = 1 + orderedColumns.size () + 2;
                    outputWriter.append (ShowUtils.horizontalLine (tableWidth) + "\n");
                    printBestScores ("Max frequency scores: ", orderedFrequencyScores);
                    printBestScores ("Max length scores:    ", orderedLengthScores);
                    printBestScores ("Max predict. scores:  ", orderedPredictabilityScores);
                    printBestScores ("Max similar. scores:  ", orderedSimilarityScores);
                    printBestScores ("Max mixed scores:     ", orderedMixedScores);
                }
            }
        }
        
        return addArffEntry (
            testedPrefix,
            testedSuffix,
            compoundSquare,
            orderedFrequencyScores,
            orderedLengthScores,
            orderedPredictabilityScores,
            orderedSimilarityScores,
            orderedMixedScores
        );
    }
    
    private UniformKeyMatrix <FunctionalList <A>, CompoundInfo> createCompoundSquare (
        FunctionalList <A> testedPrefix,
        FunctionalList <A> testedSuffix,
        FunctionalList <AffixInfo <A>> alternativePrefixes,
        FunctionalList <AffixInfo <A>> alternativeSuffixes
    ) {
        UniformKeyMatrix <FunctionalList <A>, CompoundInfo> result =
            new UniformKeySparseMatrix <> (
                alternativePrefixes.map (AffixInfo :: getForm),
                alternativeSuffixes.map (AffixInfo :: getForm)
            );
        
        // Warning: rankedPrefix.getFrequency () differs from prefixFrequency (prefix)!
        // The first is context-dependent, the second is not.
        Long testedPrefixFrequency = triePair.getPrefixFrequency (testedPrefix);
        Long testedSuffixFrequency = triePair.getSuffixFrequency (testedSuffix);
        Long originalCompoundFrequency = compoundFrequency (FunctionalList.concat (testedPrefix, testedSuffix));
        
        for (AffixInfo <A> rankedAlternativePrefix : alternativePrefixes) {
            FunctionalList <A> alternativePrefix = rankedAlternativePrefix.getForm ();
            Long alternativePrefixFrequency = triePair.getPrefixFrequency (alternativePrefix);
            
            for (AffixInfo <A> rankedAlternativeSuffix : alternativeSuffixes) {
                FunctionalList <A> alternativeSuffix = rankedAlternativeSuffix.getForm ();
                Long alternativeSuffixFrequency = triePair.getSuffixFrequency (alternativeSuffix);
                FunctionalList <A> alternativeCompound = FunctionalList.concat (alternativePrefix, alternativeSuffix);
                Long actualFrequency = compoundFrequency (alternativeCompound);
                
                if (actualFrequency > 0) {
                    Double hypotheticalSimilarity =
                        rankedAlternativePrefix.getSimilarity ()
                        * rankedAlternativeSuffix.getSimilarity ();
                    
                    // Affix version: sleeping =~ walks / walk- * sleep- / -s * -ing.
                    Double hypotheticalFrequencyA =
                        // If the original compound is not known, we must increment
                        // its frequency to 1 (after all, we just saw it).
                        // Otherwise, the hypothetical frequency would be also
                        // zero and the predictability score would not work.
                        1.0 * Math.max (originalCompoundFrequency, 1L)
                        / testedPrefixFrequency * alternativePrefixFrequency
                        / testedSuffixFrequency * alternativeSuffixFrequency;
                    
                    // Whole word version: sleeping =~ sleeps * walking / walks.
                    Double hypotheticalFrequencyB =
                        1.0
                        * compoundFrequency (PairList.concat (alternativePrefix, testedSuffix))
                        * compoundFrequency (PairList.concat (testedPrefix, alternativeSuffix))
                        / Math.max (originalCompoundFrequency, 1L);
                    
                    /*
                    System.err.println (
                        ShowUtils.makeColumn ("Orig: " + originalCompoundFrequency)
                        + ShowUtils.makeColumn ("Act: " + actualFrequency)
                        + ShowUtils.makeColumn ("A: " + DoubleFormatter.POINT_1.format (hypotheticalFrequencyA))
                        + ShowUtils.makeColumn ("B: " + DoubleFormatter.POINT_1.format (hypotheticalFrequencyB))
                    );
                    */
                    
                    if (hypotheticalFrequencyA < 0 || hypotheticalFrequencyB < 0) {
                        throw (new ArithmeticException ("hypotheticalFrequency < 0"));
                    }
                    
                    result.set (
                        alternativePrefix,
                        alternativeSuffix,
                        new CompoundInfo (alternativeCompound.size (), actualFrequency, hypotheticalFrequencyA, hypotheticalFrequencyB, hypotheticalSimilarity)
                    );
                }
            }
        }
        
        return result;
    }
    
    private static StringList enumeratedNames (String name, IntegerList numbers) {
        return numbers.mapToString (n -> name + n);
    }
    
    /**
     * The number of affixes may be reduced, as we remove affixes with zero
     * similarity.
     */
    private FunctionalList <AffixInfo <A>> findSimilarAffixes (
        boolean testedAffixIsPrefix,
        FrequencyTrie <A> trie,
        UnaryOperator <FunctionalList <A>> identityOrReverse,
        FunctionalList <A> testedAffix,
        FunctionalList <A> testedAffixComplement,
        PairList <FunctionalList <A>, Long> alternativeAffixes,
        FunctionalList <FunctionalList <A>> complementaryAffixes
    ) throws IOException {
        // Get model vector of the original affix.
        LongList testedAffixVector =
            getFrequencyVector (
                trie,
                identityOrReverse,
                testedAffix,
                complementaryAffixes
            );
        
        FunctionalList <AffixInfo <A>> result = new FunctionalList <> ();
        for (Pair <FunctionalList <A>, Long> alternativeAffix : alternativeAffixes) {
            LongList alternativeAffixVector =
                getFrequencyVector (
                    trie,
                    identityOrReverse,
                    alternativeAffix.get1 (),
                    complementaryAffixes
                );
            
            result.add (
                new AffixInfo <> (
                    alternativeAffix.get1 (),
                    alternativeAffix.get2 (),
                    vectorSimilarity (alternativeAffixVector, testedAffixVector)
                )
            );
        }
        
        // Order the results by similarity (in descending order).
        result =
            result
            // Keep only affixes with positive similarity.
            .filter (rankedAffix -> rankedAffix.getSimilarity () > 0)
            .sortBy (Comparator.comparing (AffixInfo :: getSimilarity))
            .reverse ();
        
        if (verbosity >= 1_000) {
            printMostSimilarAffixesArff (
                testedAffixIsPrefix,
                testedAffix,
                testedAffixComplement,
                result
            );
        }
        
        return result;
    }
    
    // See also: lastAtom.
    private static <A> Optional <A> firstAtom (List <A> list) {
        Optional <A> result;
        if (list.isEmpty ()) {
            result = Optional.empty ();
        }
        else {
            result = Optional.of (list.get (0));
        }
        return result;
    }
    
    @Override
    public void flush () throws IOException {
        if (arffWriter.isPresent ()) {
            arffWriter.get ().flush ();
        }
        this.outputWriter.flush ();
    }
    
    // For graph, see the Excel file.
    private double frequencyScore (CompoundInfo info) {
        // Using relative frequency, PPM (2018-10-10).
        double ppm = 1_000_000.0 * info.getActualFrequency () / triePair.totalSequencesCount ();
        return (ppm / (ppm + 10.0));
        // 2019-04: Almost the na same results.
        // return (ppm / (ppm + 4.0));
    }
    
    public Optional <ArffWriter> getArffWriter () {
        return arffWriter;
    }
    
    private static <A> PairSet <FunctionalList <A>, Long> getEntriesByMinFrequency (
        HashTrie <A, Long, UniformPair <Long>> maybeTrie,
        UnaryOperator <FunctionalList <A>> identityOrReverse,
        long minFrequency
    ) {
        return (
            // Filter the branches.
            maybeTrie.prune (subtrie ->
                FrequencyTrie.prefixFrequency (subtrie.getNodeValue ())
                >= minFrequency
            )
            .entrySet ()
            // Filter the nodes & get compound frequencies.
            .mapOptional (entry -> {
                Long compoundFrequency = entry.get2 ().get2 ();
                return (
                    (compoundFrequency < minFrequency)
                    ? Optional.empty ()
                    : Optional.of (entry.bimap (FunctionalList :: from, Pair :: get2))
                );
            })
            // Reverse the keys, if needed.
            .mapToPair (entry -> entry.map1 (identityOrReverse))
        );
    }
    
    // Combine the affix with all complementary affixes and return vector of resulting compounds.
    private static <A> LongList getFrequencyVector (
        FrequencyTrie <A> trie,
        UnaryOperator <FunctionalList <A>> identityOrReverse,
        FunctionalList <A> affix,
        FunctionalList <FunctionalList <A>> complementaryAffixes
    ) {
        // Get subtrie for the tested affix.
        Optional <HashTrie <A, Long, UniformPair <Long>>> subtrie =
            trie.getSubtrie (
                identityOrReverse.apply (affix)
            );
        return (
            complementaryAffixes.mapToLong (complementaryAffix ->
                subtrie.flatMap (st -> st.get (identityOrReverse.apply (complementaryAffix)))
                .map (FrequencyTrie :: sequenceFrequency)
                .orElse (0L)
            )
        );
    }
    
    public int getMinCompoundFrequency () {
        return minCompoundFrequency;
    }

    /**
     * The trie can be searched much faster using a minimal frequency limit.
     * Thus, we provide a high limit and check whether there are enough entries.
     * If yes, return them; else, lower the limit and repeat.
     */
    private PairList <FunctionalList <A>, Long> getMostFrequentEntries (
        Optional <HashTrie <A, Long, UniformPair <Long>>> maybeKeySubtrie,
        UnaryOperator <FunctionalList <A>> identityOrReverse,
        FunctionalList <A> originalComplementaryAffix,
        Function <List <A>, Optional <A>> boundaryAtomExtractor
    ) {
        PairList <FunctionalList <A>, Long> result;
        // Useful for very long sequences.
        if (! maybeKeySubtrie.isPresent ()) {
            result = PairList.ofPairs ();
        }
        else {
            HashTrie <A, Long, UniformPair <Long>> keySubtrie = maybeKeySubtrie.get ();
            
            long minFrequency;
            // Optimization: reduce the recursion depth.
            if (keySubtrie.size () <= kMostFrequent) {
                minFrequency = minCompoundFrequency;
            }
            else {
                // TODO: Use max compound frequency instead?
                minFrequency = 1_000_000_000;
            }
            result =
                getMostFrequentEntriesR (
                    keySubtrie,
                    identityOrReverse,
                    originalComplementaryAffix,
                    boundaryAtomExtractor,
                    minFrequency
                );
        }
        return result;
    }
    
    // R stands for Recursive.
    private PairList <FunctionalList <A>, Long> getMostFrequentEntriesR (
        HashTrie <A, Long, UniformPair <Long>> keySubtrie,
        UnaryOperator <FunctionalList <A>> identityOrReverse,
        FunctionalList <A> originalComplementaryAffix,
        Function <List <A>, Optional <A>> boundaryAtomExtractor,
        long minFrequency
    ) {
        PairSet <FunctionalList <A>, Long> mostFrequentEntries =
            getEntriesByMinFrequency (keySubtrie, identityOrReverse, minFrequency)
            // Remove the original complementary affix.
            .filter (entry -> ! Objects.equals (entry.get1 (), originalComplementaryAffix))
            // Filter the result according to the boundary (initial / final) character.
            .filter (entry ->
                ONLY_DIFFERENT_BOUNDARY_CHARS == false
                || ! Objects.equals (
                    boundaryAtomExtractor.apply (entry.get1 ()),
                    boundaryAtomExtractor.apply (originalComplementaryAffix)
                )
            );
        
        PairList <FunctionalList <A>, Long> result;
        // Do not lower the limit below the minimal compound frequency.
        if (mostFrequentEntries.size () >= kMostFrequent || minFrequency == minCompoundFrequency) {
            // Order the affixes by frequency and take the k most frequent.
            result =
                mostFrequentEntries
                .toList ()
                .sortBy (Comparator.comparing (Pair :: get2))
                .reverse ()
                .take (kMostFrequent);
        }
        else {
            result =
                getMostFrequentEntriesR (
                    keySubtrie,
                    identityOrReverse,
                    originalComplementaryAffix,
                    boundaryAtomExtractor,
                    // Lower the limit. Different factors were not better.
                    Math.max (minFrequency / 2, minCompoundFrequency)
                );
        }
        return result;
    }
    
    private PairList <FunctionalList <A>, Long> getMostFrequentEntriesByPrefix (
        FunctionalList <A> prefix,
        FunctionalList <A> originalSuffix
    ) {
        return (
            getMostFrequentEntries (
                triePair.getPrefixTrie ().getSubtrie (prefix),
                x -> x,
                originalSuffix,
                Substitus :: firstAtom
            )
        );
    }
    
    private PairList <FunctionalList <A>, Long> getMostFrequentEntriesBySuffix (
        FunctionalList <A> suffix,
        FunctionalList <A> originalPrefix
    ) {
        return (
            getMostFrequentEntries (
                triePair.getSuffixTrie ().getSubtrie (suffix.reverse ()),
                FunctionalList :: reverse,
                originalPrefix,
                Substitus :: lastAtom
            )
        );
    }
    
    // RoC = row or column.
    private PairList <AffixInfo <A>, AffixScores> getOrderedRoC (
        FunctionalList <AffixInfo <A>> rankedAffixes,
        UniformKeyMatrix <FunctionalList <A>, CompoundInfo> compoundSquare,
        BiFunction <
            UniformKeyMatrix <FunctionalList <A>, CompoundInfo>,
            FunctionalList <A>,
            FunctionalList <Optional <CompoundInfo>>
        > rowOrColumnExtractor,
        int fullRoCSize
    ) {
        return (
            rankedAffixes.mapToPair (rankedAffix -> {
                FunctionalList <A> form = rankedAffix.getForm ();
                FunctionalList <CompoundInfo> rowOrColumn = OptionalUtils.catOptionals (rowOrColumnExtractor.apply (compoundSquare, form));
                Double avgFrequencyScore = rowOrColumn.mapToDouble (this :: frequencyScore).sum () / fullRoCSize;
                Double avgPredictabilityScore = rowOrColumn.mapToDouble (Substitus :: predictabilityScore).sum () / fullRoCSize;
                // Since 2019-12-20, not very useful.
                Double avgSimilarityScore = rowOrColumn.mapToDouble (c -> Math.sqrt (c.getHSimilarity ())).sum () / fullRoCSize;
                // Since 2019-12-20, not very useful.
                Double avgLengthScore = rowOrColumn.mapToDouble (c -> lengthScore (c.getLength ())).sum () / fullRoCSize;
                
                /*
                System.err.println (
                    ShowUtils.makeColumn (showList (form))
                    + "\tSS: " + DoubleFormatter.POINT_3.format (rankedAffix.getSimilarity ())
                    + "\tSSA: " + DoubleFormatter.POINT_3.format (avgSimilarityScore)
                    + "\tLS: " + DoubleFormatter.POINT_3.format (lengthScore (form))
                    + "\tLSA: " + DoubleFormatter.POINT_3.format (avgLengthScore)
                    + "\tFS: " + DoubleFormatter.POINT_3.format (avgFrequencyScore)
                    + "\tPS: " + DoubleFormatter.POINT_3.format (avgPredictabilityScore)
                );
                */
                
                return Pair.of (
                    rankedAffix,
                    new AffixScores (
                        avgFrequencyScore,
                        lengthScore (form),
                        avgPredictabilityScore,
                        rankedAffix.getSimilarity ()
                    )
                );
            })
            // For sorting, mixed score is better than similarity. It is even
            // better than sophisticated measures combining mixed score with
            // length score.
            .sortBy (Comparator.comparing (entry -> (-1) * entry.get2 ().getMixedScore ()))
        );
    }
    
    // See also: firstAtom.
    private static <A> Optional <A> lastAtom (List <A> list) {
        Optional <A> result;
        if (list.isEmpty ()) {
            result = Optional.empty ();
        }
        else {
            result = Optional.of (list.get (list.size () - 1));
        }
        return result;
    }
    
    // For graph, see the Excel file.
    private static Double lengthScore (int sequenceSize) {
        return (1.0 - 1.0 / (2.0 + sequenceSize));
    }
    
    private static Double lengthScore (List <?> sequence) {
        return lengthScore (sequence.size ());
    }
    
    public static double normalize (double probability, double normalizationMean) {
        return (Math.tanh (25.0 * (probability - normalizationMean)) + 1) / 2;
    }
    
    // For graph, see the Excel file.
    // Old names: factored length (fLength), frequency share.
    // Update 2019-12-20: Using PFrequencyM instead of PFrequencyA.
    private static double predictabilityScore (CompoundInfo info) {
        // Beware of dividing large number by a very small one.
        // ([a], (1.707050830953006E-4, -1.1534773613364046E10, 847222334))
        double share =
            Math.max (
                info.getPFrequencyM () / info.getActualFrequency (),
                info.getActualFrequency () / info.getPFrequencyM ()
            );
        return (1.0 / (1.0 + 0.1 * Math.sqrt (share - 1.0)));
    }
    
    private void printAffixOrdering (
        String title,
        PairList <FunctionalList <A>, AffixScores> orderedAffixes,
        Function <Pair <FunctionalList <A>, AffixScores>, Double> keyExtractor,
        int numberOfAffixes
    ) throws IOException {
        outputWriter.append (
            title + ": "
            + (
                orderedAffixes
                .sortBy (Comparator.comparing (keyExtractor))
                .reverse ()
                .take (numberOfAffixes)
                .map (entry -> showList (entry.get1 ()) + " (" + ShowUtils.showAsPercent (keyExtractor.apply (entry)) + ")")
            )
            + "\n"
        );
    }
    
    private void printAffixOrdering1 (
        String title,
        PairList <FunctionalList <A>, AffixScores> orderedAffixes,
        Function <AffixScores, Double> keyExtractor,
        int numberOfAffixes
    ) throws IOException {
        printAffixOrdering (
            title,
            orderedAffixes,
            entry -> keyExtractor.apply (entry.get2 ()),
            numberOfAffixes
        );
    }
    
    private void printBestScores (String name, DoubleList orderedScores) throws IOException {
        outputWriter.append (
            name
            + String.join (", ", orderedScores.take (squareSize).map (ShowUtils :: showDouble))
            + "\n"
        );
    }
    
    private void printDifferentAffixOrderings (String affixesName, PairList <FunctionalList <A>, AffixScores> orderedAffixes) throws IOException {
        int n = 15;
        
        outputWriter.append ('\n');
        outputWriter.append (affixesName + " info (" + (orderedAffixes.size ()) + "):\n");
        if (verbosity >= 5) {
            outputWriter.append (
                String.join (",\n",
                    orderedAffixes.take (n)
                    .map (entry -> entry.map1 (Substitus :: showList))
                    .map (Object :: toString)
                )
                + "\n"
            );
        }
        printAffixOrdering1 (affixesName + " by SS", orderedAffixes, AffixScores :: getSimilarity,          n);
        printAffixOrdering1 (affixesName + " by FS", orderedAffixes, AffixScores :: getFrequencyScore,      n);
        printAffixOrdering1 (affixesName + " by PS", orderedAffixes, AffixScores :: getPredictabilityScore, n);
        printAffixOrdering1 (affixesName + " by LS", orderedAffixes, AffixScores :: getLengthScore,         n);
        printAffixOrdering (
            affixesName + " by SS * FS",
            orderedAffixes,
            entry -> Math.pow (entry.get2 ().getSimilarity (), 2) * entry.get2 ().getFrequencyScore (),
            n
        );
        printAffixOrdering (
            affixesName + " by SS * PS",
            orderedAffixes,
            entry -> Math.pow (entry.get2 ().getSimilarity (), 2) * entry.get2 ().getPredictabilityScore (),
            n
        );
        printAffixOrdering (
            affixesName + " by SS * LS",
            orderedAffixes,
            entry -> Math.pow (entry.get2 ().getSimilarity (), 2) * entry.get2 ().getLengthScore (),
            n
        );
        printAffixOrdering (
            affixesName + " by FS * PS",
            orderedAffixes,
            entry -> entry.get2 ().getFrequencyScore () * entry.get2 ().getPredictabilityScore (),
            n
        );
        printAffixOrdering (
            affixesName + " by FS * LS",
            orderedAffixes,
            entry -> entry.get2 ().getFrequencyScore () * entry.get2 ().getLengthScore (),
            n
        );
        printAffixOrdering (
            affixesName + " by PS * LS",
            orderedAffixes,
            entry -> entry.get2 ().getPredictabilityScore () * entry.get2 ().getLengthScore (),
            n
        );
        printAffixOrdering (
            affixesName + " by MS",
            orderedAffixes,
            entry -> entry.get2 ().getMixedScore (),
            n
        );
        printAffixOrdering (
            affixesName + " by LS^3 * MS",
            orderedAffixes,
            entry -> Math.pow (Math.pow (entry.get2 ().getLengthScore (), 3) * entry.get2 ().getMixedScore (), 0.25),
            n
        );
    }
    
    public void printExecutionTime () {
        final int billion = 1_000_000_000;
        final int width1 = 24;
        final int width2 = 10;
        final int width3 = 9;
        final long otherTime = totalTime - (prefixTime + suffixTime + similarTime + bigCompoundTime + arffTime);
        
        System.err.println ();
        System.err.println (
            AlignmentUtils.toLeft ("Frequent prefix search:", width1)
            + AlignmentUtils.toRight ((prefixTime / billion) + " s", width2)
            + AlignmentUtils.toRight (ShowUtils.showAsPercent (1.0 * prefixTime / totalTime), width3)
        );
        System.err.println (
            AlignmentUtils.toLeft ("Frequent suffix search:", width1)
            + AlignmentUtils.toRight ((suffixTime / billion) + " s", width2)
            + AlignmentUtils.toRight (ShowUtils.showAsPercent (1.0 * suffixTime / totalTime), width3)
        );
        System.err.println (
            AlignmentUtils.toLeft ("Similar affix search:", width1)
            + AlignmentUtils.toRight ((similarTime / billion) + " s", width2)
            + AlignmentUtils.toRight (ShowUtils.showAsPercent (1.0 * similarTime / totalTime), width3)
        );
        System.err.println (
            AlignmentUtils.toLeft ("Big square construction:", width1)
            + AlignmentUtils.toRight ((bigCompoundTime / billion) + " s", width2)
            + AlignmentUtils.toRight (ShowUtils.showAsPercent (1.0 * bigCompoundTime / totalTime), width3)
        );
        System.err.println (
            AlignmentUtils.toLeft ("ARFF writing:", width1)
            + AlignmentUtils.toRight ((arffTime / billion) + " s", width2)
            + AlignmentUtils.toRight (ShowUtils.showAsPercent (1.0 * arffTime / totalTime), width3)
        );
        System.err.println (
            AlignmentUtils.toLeft ("Other:", width1)
            + AlignmentUtils.toRight ((otherTime / billion) + " s", width2)
            + AlignmentUtils.toRight (ShowUtils.showAsPercent (1.0 * otherTime  / totalTime), width3)
        );
        System.err.println (
            AlignmentUtils.toLeft ("Total time:", width1)
            + AlignmentUtils.toRight ((totalTime / billion) + " s", width2)
            + " (" + (totalTime / billion / 60) + " min)"
        );
        System.err.flush ();
    }
    
    private static DoubleList getOrderedScores (
        FunctionalList <AffixScores> rowsAndColumnScores,
        Function <AffixScores, Double> valueExtractor
    ) {
        return (
            rowsAndColumnScores
            .mapToDouble (valueExtractor)
            .sortDesc ()
        );
    }
    
    @Deprecated
    private void printMostSimilarAffixesArff (
        boolean testedAffixIsPrefix,
        FunctionalList <A> testedAffix,
        FunctionalList <A> testedAffixComplement,
        FunctionalList <AffixInfo <A>> result
    ) throws IOException {
        int n = 10;
        String headerString;
        if (testedAffixIsPrefix) {
            headerString = showList (testedAffix) + "(" + showList (testedAffixComplement) + ")";
        }
        else {
            headerString = "(" + showList (testedAffixComplement) + ")" + showList (testedAffix);
        }
        outputWriter.append ('\n');
        outputWriter.append ("% Most similar affixes for \"" + headerString + "\":\n");
        Long totalCompounds = triePair.totalSequencesCount ();

        Long testedAffixComplementFrequency;
        if (testedAffixIsPrefix) {
            testedAffixComplementFrequency = triePair.getSuffixFrequency (testedAffixComplement);
        }
        else {
            testedAffixComplementFrequency = triePair.getPrefixFrequency (testedAffixComplement);
        }
        for (AffixInfo <A> alternative : result.take (n)) {
            FunctionalList <A> affix = alternative.getForm ();
            String bodyString;
            Long testedAffixFrequency = alternative.getFrequency ();
            FunctionalList <A> common;
            if (testedAffixIsPrefix) {
                bodyString = showList (affix) + "(" + showList (testedAffixComplement) + ")";
                common = FunctionalList.concat (affix, testedAffixComplement);
            }
            else {
                bodyString = "(" + showList (testedAffixComplement) + ")" + showList (affix);
                common = FunctionalList.concat (testedAffixComplement, affix);
            }
            Double similarity = alternative.getSimilarity ();
            outputWriter.append (
                "\"" + bodyString + "\""
                
                + ",\t" + affix.size ()
                + ",\t" + DoubleFormatter.POINT_6.format (1_000_000.0 * testedAffixFrequency / totalCompounds)
                + ",\t" + DoubleFormatter.POINT_6.format (1_000_000.0 * testedAffixComplementFrequency / totalCompounds)
                + ",\t" + DoubleFormatter.POINT_6.format (1_000_000.0 * compoundFrequency (common) / totalCompounds)
                + ",\t" + DoubleFormatter.POINT_9.format (similarity)
                
                + ",\t" + DoubleFormatter.POINT_6.format (Math.log (1.0 + affix.size ()))
                + ",\t" + DoubleFormatter.POINT_6.format (Math.log (1.0 * testedAffixFrequency / totalCompounds))
                + ",\t" + DoubleFormatter.POINT_6.format (Math.log (1.0 * testedAffixComplementFrequency / totalCompounds))
                + ",\t" + DoubleFormatter.POINT_6.format (Math.log (1.0 * compoundFrequency (common) / totalCompounds))
                + ",\t" + DoubleFormatter.POINT_6.format (Math.log (1.0 + similarity))
                
                + ",\t" + "x"
                + "\n"
            );
        }
    }
    
    private Pair <Double, Optional <List <Object>>> processSimilarAffixes (
        FunctionalList <A> testedPrefix,
        FunctionalList <A> testedSuffix,
        FunctionalList <AffixInfo <A>> prefixesBySimilarity,
        FunctionalList <AffixInfo <A>> suffixesBySimilarity
    ) throws IOException {
        long bigCompoundTimeStart = System.nanoTime ();
        UniformKeyMatrix <FunctionalList <A>, CompoundInfo> bigCompoundSquare =
            createCompoundSquare (
                testedPrefix,
                testedSuffix,
                prefixesBySimilarity,
                suffixesBySimilarity
            );
        
        PairList <AffixInfo <A>, AffixScores> orderedRows;
        PairList <AffixInfo <A>, AffixScores> orderedColumns;
        
        // Reorder the compound square rows & columns.
        orderedRows    = getOrderedRoC (prefixesBySimilarity, bigCompoundSquare, (m, p) -> m.getRow    (p), kMostFrequent);
        orderedColumns = getOrderedRoC (suffixesBySimilarity, bigCompoundSquare, (m, s) -> m.getColumn (s), kMostFrequent);
        
        if (verbosity >= 4) {
            printDifferentAffixOrderings (
                "Prefixes",
                orderedRows.map1 (AffixInfo :: getForm)
            );
            printDifferentAffixOrderings (
                "Suffixes",
                orderedColumns.map1 (AffixInfo :: getForm)
            );
        }
        bigCompoundTime += System.nanoTime () - bigCompoundTimeStart;
        
        // Get the n best ordered affixes.
        FunctionalList <AffixInfo <A>> orderedPrefixesInfo = orderedRows.take (squareSize).firsts ();
        FunctionalList <AffixInfo <A>> orderedSuffixesInfo = orderedColumns.take (squareSize).firsts ();
        
        // Create a smaller square.
        UniformKeyMatrix <FunctionalList <A>, CompoundInfo> smallCompoundSquare =
            bigCompoundSquare.select (
                orderedPrefixesInfo.map (AffixInfo :: getForm),
                orderedSuffixesInfo.map (AffixInfo :: getForm)
            );
        
        // Reorder the rows & columns into a smaller square.
        orderedRows    = getOrderedRoC (orderedPrefixesInfo, smallCompoundSquare, (m, p) -> m.getRow    (p), squareSize);
        orderedColumns = getOrderedRoC (orderedSuffixesInfo, smallCompoundSquare, (m, s) -> m.getColumn (s), squareSize);
        
        return computeScores (
            testedPrefix,
            testedSuffix,
            orderedRows.map1 (AffixInfo :: getForm),
            orderedColumns.map1 (AffixInfo :: getForm),
            smallCompoundSquare
        );
    }
    
    public void rememberCounted (FunctionalList <A> compound, Long frequency) {
        int size = compound.size ();
        // Very long compounds could cause a stack overflow error.
        if (size > MAX_COMPOUND_LENGTH) {
            System.err.println (
                "* Memorizing: The entry " + compound.take (20) + "..."
                + " is too long (" + size + " atoms), ignoring."
            );
        }
        else {
            // Original condition: Math.random () < Math.pow (10, -5)
            if (triePair.uniqueSequencesCount () % 10_000 == 0) {
                System.err.println (
                    "* Memorizing entry #" + (triePair.uniqueSequencesCount () / 1_000) + "k"
                    + ": frequency (" + showList (compound) + ") = " + frequency
                    + ", memory usage: " + MemoryUtils.memoryUsageMessageMB ()
                );
            }
            triePair.rememberCounted (compound.map (atomPreprocessor), frequency);
        }
    }
    
    private Pair <Double, Optional <List <Object>>> segmentability (
        FunctionalList <A> testedPrefix,
        FunctionalList <A> testedSuffix
    ) throws IOException {
        long prefixTimeStart = System.nanoTime ();
        PairList <FunctionalList <A>, Long> mostFrequentPrefixes =
            getMostFrequentEntriesBySuffix (testedSuffix, testedPrefix);
        prefixTime += System.nanoTime () - prefixTimeStart;
        
        long suffixTimeStart = System.nanoTime ();
        PairList <FunctionalList <A>, Long> mostFrequentSuffixes =
            getMostFrequentEntriesByPrefix (testedPrefix, testedSuffix);
        suffixTime += System.nanoTime () - suffixTimeStart;
        
        long similarTimeStart = System.nanoTime ();
        FunctionalList <AffixInfo <A>> prefixesBySimilarity =
            findSimilarAffixes (
                true,
                triePair.getPrefixTrie (),
                x -> x,
                testedPrefix,
                testedSuffix,
                mostFrequentPrefixes,
                mostFrequentSuffixes.firsts ()
            );
        FunctionalList <AffixInfo <A>> suffixesBySimilarity =
            findSimilarAffixes (
                false,
                triePair.getSuffixTrie (),
                FunctionalList :: reverse,
                testedSuffix,
                testedPrefix,
                mostFrequentSuffixes,
                mostFrequentPrefixes.firsts ()
            );
        similarTime += System.nanoTime () - similarTimeStart;
        
        return (
            processSimilarAffixes (
                testedPrefix,
                testedSuffix,
                prefixesBySimilarity,
                suffixesBySimilarity
            )
        );
    }
    
    public ProbabilisticSegmentation <A> segmentizeAndPrintArff (
        List <A> compound,
        Optional <SimpleStringSegmentation> correctSegmentation
    ) throws IOException {
        if (compound.isEmpty ()) {
            throw new IllegalArgumentException ("Cannot segmentize compound of zero length.");
        }
        long totalTimeStart = System.nanoTime ();
        FunctionalList <A> preprocessed = FunctionalList.from (compound).map (atomPreprocessor);
        // Bug fixed on 2019-12-20: preprocessed.head replaced by compound.get (0).
        ProbabilisticSegmentation <A> result = ProbabilisticSegmentation.atom (compound.get (0));
        
        if (verbosity >= 1) {
            outputWriter.append ('\n');
        }
        for (int i = 1; i < preprocessed.size (); i++) {
            final int prefixLength = i;
            
            Pair <Double, Optional <List <Object>>> subresult =
                segmentability (
                    preprocessed.take (prefixLength),
                    preprocessed.drop (prefixLength)
                );
            
            result.addLast (subresult.get1 (), compound.get (prefixLength));
            
            // Write the ARFF instance.
            if (arffWriter.isPresent () && correctSegmentation.isPresent ()) {
                long arffTimeStart = System.nanoTime ();
                // Optional <Boolean> arffCategory = getArffCategory (prefixLength, correctSegmentation.get ());
                Optional <Boolean> arffCategory = correctSegmentation.get ().glues ().get (i - 1);
                if (arffCategory.isPresent ()) {
                    List <Object> arffInstance = subresult.get2 ().get ();
                    arffInstance.add (arffCategory.get () ? "1" : "0");
                    arffWriter.get ().writeInstance (arffInstance);
                }
                arffTime += System.nanoTime () - arffTimeStart;
            }
        }
        
        if (Application.DEBUG) {
            outputWriter.flush ();
        }
        
        totalTime += System.nanoTime () - totalTimeStart;
        if (PRINT_EXECUTION_TIME) {
            printExecutionTime ();
        }
        
        return result;
    }
    
    @Override
    public ProbabilisticSegmentation <A> segmentizeP (List <A> compound) {
        try {
            return this.segmentizeAndPrintArff (compound, Optional.empty ());
        }
        catch (IOException exception) {
            throw new RuntimeException (
                "Segmentation of compound " + compound + " failed.",
                exception
            );
        }
    }
    
    private static DoubleList selectScores (DoubleList scores) {
        return ATTRIBUTE_NUMBERS.mapToDouble (n -> scores.get (n - 1));
    }
    
    private static DoubleList selectScores2 (DoubleList scores) {
        return ATTRIBUTE_NUMBERS_2.mapToDouble (n -> scores.get (n - 1));
    }
    
    public void setMinCompoundFrequency (int minCompoundFrequency) {
        this.minCompoundFrequency = minCompoundFrequency;
    }
    
    public void setKMostFrequent (int kMostFrequent) {
        this.kMostFrequent = kMostFrequent;
    }
    
    public void setSquareSize (int squareSize) {
        this.squareSize = squareSize;
    }
    
    private String showCompoundSquare (
        PairList <FunctionalList <A>, AffixScores> orderedRows,
        PairList <FunctionalList <A>, AffixScores> orderedColumns,
        UniformKeyMatrix <FunctionalList <A>, CompoundInfo> compoundSquare
    ) {
        StringBuilder result = new StringBuilder ();
        
        // Heading (suffixes).
        result.append (makeColumn ("Affix"));
        for (FunctionalList <A> suffix : orderedColumns.firsts ()) {
            result.append (makeColumn ("-" + showList (suffix)));
        }
        if (verbosity >= 3) {
            result.append (makeColumn ("Frequency score"));
            result.append (makeColumn ("Predictability"));
        }
        result.append ('\n');
        
        // Body (prefixes and frequencies).
        for (Pair <FunctionalList <A>, AffixScores> row : orderedRows) {
            FunctionalList <A> prefix = row.get1 ();
            // First column: prefix.
            result.append (makeColumn (showList (prefix) + "-"));
            
            // Compound frequencies.
            for (FunctionalList <A> suffix : orderedColumns.firsts ()) {
                result.append (makeColumn (compoundSquare.get (prefix, suffix)
                        .map (CompoundInfo :: getActualFrequency)
                        .orElse (0L)
                        .toString ()
                    )
                );
            }
            
            // Last two columns: scores.
            if (verbosity >= 3) {
                // Row frequency score.
                result.append (
                    makeColumn (ShowUtils.showDouble (row.get2 ().getFrequencyScore ()))
                );

                // Row predictability score.
                result.append (
                    makeColumn (ShowUtils.showDouble (row.get2 ().getPredictabilityScore ()))
                );
            }
            
            result.append ('\n');
        }
        
        // Last two rows: scores.
        if (verbosity >= 3) {
            // Column frequency score.
            result.append (makeColumn ("Frequency score"));
            for (Double frequencyScore : orderedColumns.map (column -> column.get2 ().getFrequencyScore ())) {
                result.append (
                    makeColumn (ShowUtils.showDouble (frequencyScore))
                );
            }
            result.append ('\n');

            // Column predictability score.
            result.append (makeColumn ("Predictability"));
            for (Double predictability : orderedColumns.map (column -> column.get2 ().getPredictabilityScore ())) {
                result.append (
                    makeColumn (ShowUtils.showDouble (predictability))
                );
            }
            result.append ('\n');
        }
        
        return result.toString ();
    }
    
    /**
     * Empty string is shown as empty set character (∅). Non-empty list
     * containing characters is converted to string. Else, behavior is
     * equivalent to the standard toString ().
     */
    private static <A> String showList (FunctionalList <A> list) {
        return list.toString ("∅");
    }
    
    private static <A> String showSegmentation (FunctionalList <A> prefix, FunctionalList <A> suffix) {
        return (showList (prefix) + GlobalSettings.HARD_DELIMITER + showList (suffix));
    }
    
    @Override
    public String toString () {
        return (
            "Substitus {"
                + "minCompoundFrequency = " + minCompoundFrequency
                + ", kMostFrequent = " + kMostFrequent
                + ", squareSize = " + squareSize
                + ", trie size = " + triePair.totalSequencesCount ()
            + "}"
        );
    }
    
    public int uniqueCompoundsCount () {
        return triePair.uniqueSequencesCount ();
    }
    
    /**
     * Compute similarity of two vectors, defined as complement of their angle.
     * The similarity belongs to interval [0, 1]. If either of the vectors is
     * null, the similarity is 0.
     * 
     * @param u Some vector.
     * @param v Some other vector.
     * @return Similarity of the vectors.
     */
    private static double vectorSimilarity (LongList u, LongList v) {
        double result;
        // We must treat compounds with zero frequency in a special way,
        // because it is not possible to determine angle of null vector.
        if (VectorUtils.size (u) == 0 || VectorUtils.size (v) == 0) {
            result = 0.0;
        }
        else {
            boolean useSquareRoots = false;
            Function <Long, Number> f = useSquareRoots ? (Math :: sqrt) : (x -> x);
            double angle = VectorUtils.angleInDegrees (u.map (f), v.map (f));
            result = 1.0 - angle / 90.0;
        }
        return result;
    }
}
