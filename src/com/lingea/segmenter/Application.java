package com.lingea.segmenter;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.list.number.IntegerList;
import com.github.josefplch.utils.data.nlp.Tokenizer;
import com.github.josefplch.utils.data.string.AlignmentUtils;
import com.github.josefplch.utils.data.string.DoubleFormatter;
import com.github.josefplch.utils.data.string.StringUtils;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.tuple.UniformPair;
import com.github.josefplch.utils.system.Args;
import com.github.josefplch.utils.system.FakeWriter;
import com.github.josefplch.utils.system.MemoryUtils;
import com.github.josefplch.utils.system.TextFileUtils;
import com.lingea.segmenter.data.ProbabilisticStringSegmentation;
import com.lingea.segmenter.data.SimpleStringSegmentation;
import com.lingea.segmenter.data.TestSet;
import com.lingea.segmenter.data.frequencyList.FrequencyListConverter;
import com.lingea.segmenter.data.frequencyList.FrequencyListEntry;
import com.lingea.segmenter.eval.SettingTestEvaluator;
import com.lingea.segmenter.eval.SubstitusSettingTester;
import com.lingea.segmenter.substitus.StringSubstitus;
import com.lingea.segmenter.substitus.StringSubstitusTokenizer;
import com.lingea.segmenter.substitus.Substitus;
import com.lingea.segmenter.substitus.TokenPreprocessor;
import com.lingea.segmenter.substitus.TokenTuner;
import com.lingea.segmenter.substitus.TokenUsageAnalyser;
import com.lingea.segmenter.substitus.data.SegmentationFileReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * The main class.
 * 
 * @author  Josef Plch
 * @since   2018-05-14
 * @version 2021-01-19
 */
public abstract class Application {
    public static final boolean DEBUG = false;
    public static final boolean HTML_MODE = true;
    private static final int VERSION_YEAR = 2021;
    private static final String VERSION = VERSION_YEAR + "-01-19 (Java 8)";
    
    public static final String ACTION_BEAUTIFY_SFWL = "beautify-segmented-list";
    public static final String ACTION_CONVERT_FWL = "convert-frequency-list";
    public static final String ACTION_CREATE_FWL = "create-frequency-list";
    public static final String ACTION_CREATE_TRAIN_DATA = "create-train-data";
    public static final String ACTION_EVALUATE_SETTINGS = "evaluate-settings";
    public static final String ACTION_EVALUATE_TOKEN_FILES = "evaluate-tokens";
    public static final String ACTION_FIND_TOKENS = "find-tokens";
    public static final String ACTION_SEGMENTIZE_FWL = "segmentize-frequency-list";
    public static final String ACTION_SEGMENTIZE_MC2010 = "segmentize-mc2010";
    public static final String ACTION_SEGMENTIZE_WORDS = "segmentize-words";
    public static final String ACTION_SHOW_TOKEN_USAGE = "show-token-usage";
    public static final String ACTION_TEST_SETTINGS = "test-segmenter-settings";
    public static final String ACTION_TOKENIZE = "tokenize";
    public static final String ACTION_TUNE_TOKENS = "tune-tokens";
    
    public static final String ARG_CASE_SENSITIVE = "case-sensitive";
    public static final String ARG_FREQUENCY_LIST_PATH = "frequency-list";
    public static final String ARG_FREQUENCY_LIST_LIMIT = "frequency-list-limit";
    public static final String ARG_HELP = "help";
    public static final String ARG_INPUT_FILES = "input-files";
    public static final String ARG_K_MOST_FREQUENT = "k-most-frequent";
    public static final String ARG_LEMMATIZE = "lemmatize";
    public static final String ARG_LETTER_CASE = "letter-case";
    public static final String ARG_LETTER_CASE_LOWER = "lower";
    public static final String ARG_LETTER_CASE_ORIGINAL = "original";
    public static final String ARG_LETTER_CASE_UPPER = "upper";
    public static final String ARG_MAX_NGRAM_LENGTH = "max-ngram-length";
    public static final String ARG_MAX_NGRAM_LEVEL = "max-ngram-level";
    public static final String ARG_MAX_RESULT_SIZE = "max-result-size";
    public static final String ARG_MAX_TOKEN_LENGTH = "max-token-length";
    public static final String ARG_MAX_WORD_LENGTH = "max-word-length";
    public static final String ARG_MIN_NGRAM_IWF = "min-ngram-productivity";
    public static final String ARG_MIN_NGRAM_TF = "min-ngram-frequency";
    public static final String ARG_MIN_TOKEN_IWF = "min-token-productivity";
    public static final String ARG_MIN_TOKEN_TF = "min-token-frequency";
    public static final String ARG_MIN_VARIANT_SHARE = "min-variant-share";
    public static final String ARG_NORMALIZATION_MEAN = "normalization-mean";
    public static final String ARG_ONLY_WORDS = "only-words";
    public static final String ARG_OUTPUT_DIRECTORY = "output-dir";
    public static final String ARG_OUTPUT_FORMAT = "output-format";
    public static final String ARG_OUTPUT_FORMAT_BINARY = "binary";
    public static final String ARG_OUTPUT_FORMAT_BINARY_ALT = "binary-alt";
    public static final String ARG_OUTPUT_FORMAT_DECIMAL = "decimal";
    public static final String ARG_OUTPUT_FORMAT_HTML = "html";
    public static final String ARG_OUTPUT_FORMAT_PERCENT = "percent";
    public static final String ARG_OUTPUT_FORMAT_BPE = "bpe";
    public static final String ARG_OUTPUT_FORMAT_DASH = "dash";
    public static final String ARG_OUTPUT_FORMAT_SPACE = "space";
    public static final String ARG_OUTPUT_PREFIX = "output-prefix";
    public static final String ARG_SQUARE_SIZE = "square-size";
    public static final String ARG_TEST_SET = "test-set";
    public static final String ARG_TOKEN_FILE = "token-file";
    public static final String ARG_TOKEN_NGRAMS = "token-ngrams";
    public static final String ARG_VERBOSITY = "verbosity";
    public static final String ARG_WORD_FILTER = "word-filter";
    
    private static final String HELP_ARG_CASE_SENSITIVE       = "(optional): use case-sensitive mode";
    private static final String HELP_ARG_FREQUENCY_LIST_LIMIT = "(optional): use only n first entries";
    private static final String HELP_ARG_MAX_WORD_LENGTH      = "(optional): ignore words longer than n";
    private static final String HELP_ARG_NORMALIZATION_MEAN   = "(optional): [0..1], lower mean = more word splits";

    private static final String MESSAGE_YOU_MUST_SPECIFY = "You must specify ";
    private static final String MESSAGE_NUMBER_SHORTHANDS = "You can use 'k' for thousands and 'M' for millions, e.g. 2k = 2,000.";
    private static final String MESSAGE_TO_CHANGE_IT_USE = " If you want to change it, add --";
    private static final String MESSAGE_UNKNOWN_USING_DEFAULT = " not specified, using the default: ";
    private static final String MESSAGE_UNKNOWN_YOU_CAN_USE = " not specified. If you want to set it, add --";
    
    public static void main (String [] args) {
        try {
            // System.setProperty ("line.separator", "\n");
            Args parsedArgs = new Args (args);
            
            if (args.length == 0 || (args [0]).isEmpty ()) {
                System.err.println ("Use --" + ARG_HELP + " to see the manual.");
            }
            // We want to enable sub-helps, i.e. substitus action --help.
            else if ((args [0]).equals ("--" + ARG_HELP)) {
                help ();
            }
            else if (parsedArgs.contains ("version")) {
                System.out.println ("Version: " + version ());
            }
            else {
                String action = args [0];
                doAction (action, parsedArgs);
            }
            System.exit (0);
        }
        catch (Throwable exception) {
            if (DEBUG) {
                throw new RuntimeException (exception);
            }
            else {
                StringList errors = new StringList ();
                Throwable cause = exception;
                while (cause != null) {
                    String message = cause.getMessage ();
                    errors.add (
                        Objects.isNull (message)
                        ? cause.getClass ().getSimpleName ()
                        : message
                    );
                    cause = cause.getCause ();
                }
                System.err.println ("[Error] " + errors.join (" | "));
                System.exit (1);
            }
        }
    }
    
    private static void doAction (String action, Args args) throws IOException {
        if (action.equals (ACTION_SEGMENTIZE_WORDS)) {
            segmentizeWords (args);
        }
        else if (action.equals (ACTION_TOKENIZE)) {
            tokenize (args);
        }
        else if (action.equals (ACTION_CONVERT_FWL)) {
            convertFrequencyList (args);
        }
        else if (action.equals (ACTION_CREATE_FWL)) {
            createFrequencyList (args);
        }
        else if (action.equals (ACTION_SEGMENTIZE_FWL)) {
            segmentizeFrequencyList (args);
        }
        else if (action.equals (ACTION_SEGMENTIZE_MC2010)) {
            segmentizeMc2010 (args);
        }
        else if (action.equals (ACTION_BEAUTIFY_SFWL)) {
            beautifySegmentedList (args);
        }
        else if (action.equals (ACTION_SHOW_TOKEN_USAGE)) {
            showTokenUsage (args);
        }
        else if (action.equals (ACTION_FIND_TOKENS)) {
            findTokens (args);
        }
        else if (action.equals (ACTION_TUNE_TOKENS)) {
            tuneTokens (args);
        }
        else if (action.equals (ACTION_CREATE_TRAIN_DATA)) {
            createMultilingualArff (args);
        }
        else if (action.equals (ACTION_TEST_SETTINGS)) {
            testSettings (args);
        }
        else if (action.equals (ACTION_EVALUATE_SETTINGS)) {
            evaluateSettings (args);
        }
        else if (action.equals (ACTION_EVALUATE_TOKEN_FILES)) {
            evaluateTokenFiles (args);
        }
        else {
            System.err.println ("Unknown action: " + action + ". Use --" + ARG_HELP + " to see the manual.");
        }
    }
    
    // Since 2020-10-24
    private static void beautifySegmentedList (Args args) throws IOException {
        if (args.contains (ARG_HELP)) {
            printLines (
                "Accept a segmented list (" + GlobalSettings.EXTENSION_SFWL + "), filter only words consisting of letters, order them alphabetically, normalize the segmentability values and print the list in a human-readable format.",
                "",
                "Parameters:",
                "--" + ARG_FREQUENCY_LIST_LIMIT + " " + HELP_ARG_FREQUENCY_LIST_LIMIT,
                "--" + ARG_NORMALIZATION_MEAN + " " + HELP_ARG_NORMALIZATION_MEAN,
                "--" + ARG_MAX_WORD_LENGTH + " " + HELP_ARG_MAX_WORD_LENGTH
            );
        }
        else {
            Collator collator = Collator.getInstance (new Locale (GlobalSettings.LOCALE));
            double normalizationMean = getNormalizationMean (args);
            int maxWordLength = getMaxWordLength (args);
            
            TextFileUtils.writeLines (
                freshStdoutWriter (),
                SegmentationFileReader.readStream (
                    freshStdinReader (),
                    getFrequencyListLimit (args)
                )
                .map (
                    tuple -> Pair.of (
                        tuple.get2 (),
                        tuple.get4 ().normalize (normalizationMean)
                    )
                )
                // Keep only "clean" words without punctuation.
                .filter (pair -> pair.get1 ().length () <= maxWordLength && Tokenizer.isWord (pair.get1 (), true, true, false))
                .sorted (Comparator.comparing (Pair :: get1, collator))
                .map (pair ->
                    AlignmentUtils.toLeft (pair.get1 (), maxWordLength)
                    + "  "
                    + AlignmentUtils.toLeft (
                        pair.get2 ()
                            .binarizeIfS ((len, p) -> p >= 0.5 || (len >= 4 && p >= 0.1))
                            .join (GlobalSettings.HARD_DELIMITER_STRING),
                        maxWordLength + 5
                    )
                    + "  "
                    + pair.get2 ().toString (p -> " " + DoubleFormatter.POINT_2.format (p) + " ")
                )
            );
        }
    }
    
    private static void convertFrequencyList (Args args) throws IOException {
        FrequencyListConverter.convert (
            freshStdinReader (),
            Optional.of (getMaxWordLength (args)),
            getLetterCase (args),
            getOnlyWords (args),
            getMinVariantShare (args),
            getMaxResultSize (args),
            freshStdoutWriter ()
        );
    }
    
    private static void createFrequencyList (Args args) throws IOException {
        FrequencyListConverter.tokenizedToCaseSensitive (
            freshStdinReader (),
            Optional.of (getMaxWordLength (args)),
            getMaxResultSize (args),
            freshStdoutWriter ()
        );
    }
    
    private static void createMultilingualArff (Args args) throws IOException {
        PairList <String, String> inputFiles =
            args.read (
                ARG_INPUT_FILES,
                Application :: readFilePairs,
                MESSAGE_YOU_MUST_SPECIFY + "the input files. Use: --" + ARG_INPUT_FILES + " [frequency-list-path-1]::[test-set-path-1],..."
            );
        Writer writer = freshStdoutWriter ();
        boolean writeHead = true;
        for (Pair <String, String> filePair : inputFiles) {
            StringSubstitus substitus =
                trainSubstitus (
                    getCaseSensitive (args),
                    new BufferedReader (new FileReader (filePair.get1 ())),
                    getFrequencyListLimit (args),
                    new FakeWriter (),
                    Optional.of (Pair.of (writer, writeHead)),
                    0
                );
            substitus.setKMostFrequent (getKMostFrequent (args));
            substitus.setSquareSize (getSquareSize (args));

            for (Pair <String, SimpleStringSegmentation> entry : TestSet.readFile (filePair.get2 ())) {
                System.err.println ("* Processing " + entry.get1 () + " ...");
                substitus.segmentizeAndPrintArff (
                    entry.get1 (),
                    Optional.of (entry.get2 ())
                );
            }
            substitus.flush ();
            
            writeHead = false;
        }
    }
    
    private static void evaluateSettings (Args args) throws IOException {
        SettingTestEvaluator evaluator =
            new SettingTestEvaluator (
                freshStdoutWriter (),
                getVerbosity (args, 1, 2),
                10
            );
        evaluator.evaluateSubstitusFiles (
            args.read (
                ARG_INPUT_FILES,
                Application :: readFilePairs,
                MESSAGE_YOU_MUST_SPECIFY + "the input files. Use: --" + ARG_INPUT_FILES + " [test-file-path-1]::[test-set-path-1],..."
            )
        );
    }
    
    private static void evaluateTokenFiles (Args args) throws IOException {
        SettingTestEvaluator evaluator =
            new SettingTestEvaluator (freshStdoutWriter (), 1, 10);
        evaluator.evaluateKnownTokenFiles (
            args.read (
                ARG_INPUT_FILES,
                arg -> StringList.split (",", arg),
                MESSAGE_YOU_MUST_SPECIFY + "the tested files. Use: --" + ARG_INPUT_FILES + " [file-path-1],..."
            ),
            TestSet.readFile (getTestSet (args))
        );
    }
    
    private static void findTokens (Args args) throws IOException {
        if (args.contains (ARG_HELP)) {
            printLines (
                "Find tokens in a segmented frequency list (." + GlobalSettings.EXTENSION_SFWL + ").",
                "",
                "Parameters:",
                "--" + ARG_FREQUENCY_LIST_LIMIT + " " + HELP_ARG_FREQUENCY_LIST_LIMIT,
                "--" + ARG_NORMALIZATION_MEAN + " " + HELP_ARG_NORMALIZATION_MEAN,
                "--" + ARG_MAX_WORD_LENGTH + " " + HELP_ARG_MAX_WORD_LENGTH,
                "--" + ARG_MAX_NGRAM_LEVEL + " (optional): limit the number of tokens in n-grams",
                "--" + ARG_MIN_NGRAM_TF + " (optional): ignore n-grams with frequency < n"
            );
        }
        else {
            // TODO: Load other arguments from args.
            UniformPair <Boolean> addBoundaries = new UniformPair <> (false, false);

            TokenPreprocessor preprocessor =
                new TokenPreprocessor (
                    getMaxWordLength (args),
                    getMaxNgramLevel (args),
                    getMinNgramTf (args),
                    addBoundaries,
                    getNormalizationMean (args),
                    5
                );

            preprocessor.findTokenNgrams (
                freshStdinReader (),
                getFrequencyListLimit (args),
                freshStdoutWriter ()
            );
        }
    }
    
    private static BufferedReader freshStdinReader () {
        return new BufferedReader (new InputStreamReader (System.in));
    }
    
    private static OutputStreamWriter freshStdoutWriter () {
        return new OutputStreamWriter (System.out);
    }
    
    private static UnaryOperator <Character> getCaseSensitive (Args args) {
        UnaryOperator <Character> result;
        if (args.contains (ARG_CASE_SENSITIVE)) {
            result = c -> c;
        }
        else {
            System.err.println (
                "Substitus runs in case-insensitive mode."
                + MESSAGE_TO_CHANGE_IT_USE + ARG_CASE_SENSITIVE + "."
            );
            // result = CharPreprocessor :: convertToLower;
            result = Character :: toLowerCase;
        }
        return result;
    }
    
    private static String getFrequencyListPath (Args args) {
        return (
            args.getString (
                ARG_FREQUENCY_LIST_PATH,
                MESSAGE_YOU_MUST_SPECIFY + "the frequency list. Use --" + ARG_FREQUENCY_LIST_PATH + " [file-path]."
            )
        );
    }
    
    // Frequency list length limit (optional): with more than 1 million, one risks an OutOfMemoryError.
    private static Optional <Integer> getFrequencyListLimit (Args args) {
        Optional <Integer> result = args.getInteger (ARG_FREQUENCY_LIST_LIMIT);
        if (! result.isPresent ()) {
            System.err.println (
                "Frequency list length limit"
                + MESSAGE_UNKNOWN_YOU_CAN_USE + ARG_FREQUENCY_LIST_LIMIT + " [number]."
                + " " + MESSAGE_NUMBER_SHORTHANDS
            );
        }
        return result;
    }
    
    private static int getKMostFrequent (Args args) {
        int defaultValue = Substitus.DEFAULT_K_MOST_FREQUENT;
        int value =
            args.getInteger (ARG_K_MOST_FREQUENT).orElseGet (() -> {
                System.err.println (
                    "K most frequent" + MESSAGE_UNKNOWN_USING_DEFAULT + defaultValue + "."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_K_MOST_FREQUENT + " [number]."
                    + " Recommended values are 25-1000."
                );
                return defaultValue;
            });
        if (value < 1) {
            throw new IllegalArgumentException ("The k most frequent must be positive.");
        }
        else {
            return value;
        }
    }
    
    private static boolean getLemmatize (Args args) {
        boolean result;
        if (args.contains (ARG_LEMMATIZE)) {
            result = true;
        }
        else {
            System.err.println (
                "Word forms are used. To evaluate the lemmata, add --"
                + ARG_LEMMATIZE + "."
            );
            result = false;
        }
        return result;
    }
    
    private static UnaryOperator <Character> getLetterCase (Args args) {
        return (
            args.getString (ARG_LETTER_CASE)
            .map (letterCase -> {
                UnaryOperator <Character> result;
                if (letterCase.equals (ARG_LETTER_CASE_LOWER)) {
                    result = Character :: toLowerCase;
                }
                else if (letterCase.equals (ARG_LETTER_CASE_ORIGINAL)) {
                    result = (c -> c);
                }
                else if (letterCase.equals (ARG_LETTER_CASE_UPPER)) {
                    result = Character :: toUpperCase;
                }
                else {
                    throw new IllegalArgumentException ("");
                }
                return result;
            })
            .orElseGet (() -> {
                System.err.println (
                    "The letter case is not changed."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_LETTER_CASE + " ("
                        + ARG_LETTER_CASE_ORIGINAL
                        + "|" + ARG_LETTER_CASE_LOWER
                        + "|" + ARG_LETTER_CASE_UPPER
                    + ")."
                );
                return (c -> c);
            })
        );
    }
    
    private static int getMaxNgramLength (Args args) {
        int defaultValue = 10;
        return (
            args.getInteger (ARG_MAX_NGRAM_LENGTH).orElseGet (() -> {
                System.err.println (
                    "Maximum n-gram length (sum of token lengths)" + MESSAGE_UNKNOWN_USING_DEFAULT + defaultValue + "."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_MAX_NGRAM_LENGTH + " [number]."
                    + " Recommended values are 6-12."
                );
                return defaultValue;
            })
        );
    }
    
    private static int getMaxNgramLevel (Args args) {
        int defaultValue = 1;
        return (
            args.getInteger (ARG_MAX_NGRAM_LEVEL).orElseGet (() -> {
                System.err.println (
                    "Maximum n-gram level" + MESSAGE_UNKNOWN_USING_DEFAULT + defaultValue + "."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_MAX_NGRAM_LEVEL + " [number]."
                    + " Recommended values are 1-3."
                );
                return defaultValue;
            })
        );
    }
    
    private static Optional <Integer> getMaxResultSize (Args args) {
        Optional <Integer> result = args.getInteger (ARG_MAX_RESULT_SIZE);
        if (! result.isPresent ()) {
            System.err.println (
                "Maximum number of results"
                + MESSAGE_UNKNOWN_YOU_CAN_USE + ARG_MAX_RESULT_SIZE + " [number]."
                + " " + MESSAGE_NUMBER_SHORTHANDS
            );
        }
        return result;
    }
    
    private static int getMaxTokenLength (Args args) {
        int defaultValue = 10;
        return (
            args.getInteger (ARG_MAX_TOKEN_LENGTH).orElseGet (() -> {
                System.err.println (
                    "Maximum token length" + MESSAGE_UNKNOWN_USING_DEFAULT + defaultValue + "."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_MAX_TOKEN_LENGTH + " [number]."
                    + " Recommended values are 6-10."
                );
                return defaultValue;
            })
        );
    }
    
    private static int getMaxWordLength (Args args) {
        int defaultValue = 30;
        return (
            args.getInteger (ARG_MAX_WORD_LENGTH).orElseGet (() -> {
                System.err.println (
                    "Maximum word length" + MESSAGE_UNKNOWN_USING_DEFAULT + defaultValue + "."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_MAX_WORD_LENGTH + " [number]."
                    + " Recommended values are 20-50."
                );
                return defaultValue;
            })
        );
    }
    
    private static int getMinNgramIwf (Args args) {
        int defaultValue = 1;
        return (
            args.getInteger (ARG_MIN_NGRAM_IWF).orElseGet (() -> {
                System.err.println (
                    "Minimum n-gram productivity (also known as inverse word frequency, IWF)"
                    + MESSAGE_UNKNOWN_USING_DEFAULT + defaultValue + "."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_MIN_NGRAM_IWF + " [number]."
                    + " Recommended values are 1-10."
                );
                return defaultValue;
            })
        );
    }
    
    private static int getMinNgramTf (Args args) {
        int defaultValue = 1;
        return (
            args.getInteger (ARG_MIN_NGRAM_TF).orElseGet (() -> {
                System.err.println (
                    "Minimum n-gram frequency" + MESSAGE_UNKNOWN_USING_DEFAULT + defaultValue + "."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_MIN_NGRAM_TF + " [number]."
                    + " " + MESSAGE_NUMBER_SHORTHANDS
                );
                return defaultValue;
            })
        );
    }
    
    private static int getMinTokenIwf (Args args) {
        int defaultValue = 1;
        return (
            args.getInteger (ARG_MIN_TOKEN_IWF).orElseGet (() -> {
                System.err.println (
                    "Minimum token productivity (also known as inverse word frequency, IWF)"
                    + MESSAGE_UNKNOWN_USING_DEFAULT + defaultValue + "."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_MIN_NGRAM_IWF + " [number]."
                    + " Recommended values are 1-10."
                );
                return defaultValue;
            })
        );
    }
    
    private static int getMinTokenTf (Args args) {
        int defaultValue = 1;
        return (
            args.getInteger (ARG_MIN_TOKEN_TF).orElseGet (() -> {
                System.err.println (
                    "Minimum token frequency" + MESSAGE_UNKNOWN_USING_DEFAULT + defaultValue + "."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_MIN_NGRAM_TF + " [number]."
                    + " " + MESSAGE_NUMBER_SHORTHANDS
                );
                return defaultValue;
            })
        );
    }
    
    private static Optional <Double> getMinVariantShare (Args args) {
        Optional <Double> result = args.getDouble (ARG_MIN_VARIANT_SHARE);
        if (! result.isPresent ()) {
            System.err.println (
                "Minimum frequency share for accent variants"
                + MESSAGE_UNKNOWN_YOU_CAN_USE + ARG_MIN_VARIANT_SHARE + " [number]."
                + " Recommended values are 0.05-0.20."
            );
        }
        else {
            Double value = result.get ();
            if (value < 0.0 || value > 1.0) {
                throw new IllegalArgumentException (
                    "The minimum frequency share for accent variants must be in range [0, 1]."
                );
            }
        }
        return result;
    }
    
    private static double getNormalizationMean (Args args) {
        double defaultValue = Substitus.DEFAULT_NORMALIZATION_MEAN;
        double value =
            args.getDouble (ARG_NORMALIZATION_MEAN).orElseGet (() -> {
                System.err.println (
                    "Normalization mean" + MESSAGE_UNKNOWN_USING_DEFAULT + defaultValue + "."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_NORMALIZATION_MEAN + " [number]."
                    + " Recommended values are 0.60-0.80."
                );
                return defaultValue;
            });
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException (
                "The normalization mean must be in range [0, 1]."
            );
        }
        else {
            return value;
        }
    }
    
    private static boolean getOnlyWords (Args args) {
        boolean result;
        if (args.contains (ARG_ONLY_WORDS)) {
            result = true;
        }
        else {
            System.err.println (
                "All entries are used. To use only entries consisting of"
                + " letters and digits, add --" + ARG_ONLY_WORDS + "."
            );
            result = false;
        }
        return result;
    }
    
    private static Function <ProbabilisticStringSegmentation, String> getOutputFormatA (Args args) {
        String arg =
            args.getString (
                ARG_OUTPUT_FORMAT,
                MESSAGE_YOU_MUST_SPECIFY + "the segmentation output format. Use --"
                + ARG_OUTPUT_FORMAT + " ("
                    + ARG_OUTPUT_FORMAT_BINARY
                    + "|" + ARG_OUTPUT_FORMAT_BINARY_ALT
                    + "|" + ARG_OUTPUT_FORMAT_DECIMAL
                    + "|" + ARG_OUTPUT_FORMAT_HTML
                    + "|" + ARG_OUTPUT_FORMAT_PERCENT
                + ")."
            );
        
        Function <ProbabilisticStringSegmentation, String> result;
        if (arg.equals (ARG_OUTPUT_FORMAT_BINARY)) {
            result = s -> s.toStringBinary ((l, p) -> p >= 0.5);
        }
        else if (arg.equals (ARG_OUTPUT_FORMAT_BINARY_ALT)) {
            result = s -> s.toStringBinary ((l, p) -> p >= 0.5 || l > 8 && p > 0.001);
        }
        // Sharp minimum version, not much better.
        else if (arg.equals ("binary-alt-2")) {
            result = s -> s.toStringBinary ((l, p) -> p >= Math.min (64.0 / Math.pow (l, 4), 0.5));
        }
        else if (arg.equals (ARG_OUTPUT_FORMAT_DECIMAL)) {
            result = s -> s.toStringDecimal6 ();
        }
        else if (arg.equals (ARG_OUTPUT_FORMAT_HTML)) {
            String ATOM_OPEN = "<span class=\"atom\">";
            String ATOM_CLOSE = "</span>";
            result = s ->
                "<div class=\"entry\">"
                    + "<div class=\"segmentation\">"
                        + ATOM_OPEN
                        + s.toString (p ->
                            ATOM_CLOSE
                            + "<span class=\"segmentability\">"
                                + DoubleFormatter.POINT_2.format (p)
                            + "</span>"
                            + ATOM_OPEN
                        )
                        + ATOM_CLOSE
                    + "</div>"
                    + "<div class=\"simple\">"
                        + s.toStringBinary ((l, p) -> p >= 0.5 || l > 8 && p > 0.001)
                    + "</div>"
                + "</div>";
        }
        else if (arg.equals ("mc2010")) {
            result = s -> s.elements () + "\t" + s.binarize50S ().mapToString (m -> m + ":" + m).unwords ();
        }
        else if (arg.equals (ARG_OUTPUT_FORMAT_PERCENT)) {
            result = s -> s.toString (p -> " " + Math.round (100 * p) + "% ");
        }
        else if (arg.equals ("ternary")) {
            result = s -> s.toString (p ->
                p >= 0.6
                ? String.valueOf (GlobalSettings.HARD_DELIMITER)
                : p >= 0.4
                ? String.valueOf (GlobalSettings.SOFT_DELIMITER)
                : ""
            );
        }
        else {
            throw new IllegalArgumentException ("Unknown output format: " + arg);
        }
        return result;
    }
    
    private static UniformPair <String> getOutputFormatB (Args args) {
        String arg =
            args.getString (
                ARG_OUTPUT_FORMAT,
                MESSAGE_YOU_MUST_SPECIFY + "the output format. Use --" + ARG_OUTPUT_FORMAT
                + " (" + ARG_OUTPUT_FORMAT_BPE
                + "|" + ARG_OUTPUT_FORMAT_DASH
                + "|" + ARG_OUTPUT_FORMAT_SPACE
                + ")."
            );
        String tokenDelimiter;
        String wordDelimiter;
        if (arg.equals (ARG_OUTPUT_FORMAT_BPE)) {
            tokenDelimiter = "@@ ";
            wordDelimiter = " ";
        }
        else if (arg.equals (ARG_OUTPUT_FORMAT_DASH)) {
            tokenDelimiter = "-";
            wordDelimiter = " ";
        }
        else if (arg.equals (ARG_OUTPUT_FORMAT_SPACE)) {
            tokenDelimiter = " ";
            wordDelimiter = " [space] ";
        }
        else {
            throw new IllegalArgumentException ("Unknown output format: " + arg);
        }
        return new UniformPair <> (tokenDelimiter, wordDelimiter);
    }
    
    private static int getSquareSize (Args args) {
        int defaultValue = Substitus.DEFAULT_SQUARE_SIZE;
        int value =
            args.getInteger (ARG_SQUARE_SIZE).orElseGet (() -> {
                System.err.println (
                    "Square size" + MESSAGE_UNKNOWN_USING_DEFAULT + defaultValue + "."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_SQUARE_SIZE + " [number]."
                    + " Recommended values are 5-50."
                );
                return defaultValue;
            });
        if (value < 1) {
            throw new IllegalArgumentException ("The square size must be positive.");
        }
        else {
            return value;
        }
    }
    
    private static String getTestSet (Args args) {
        return (
            args.getString (
                ARG_TEST_SET,
                MESSAGE_YOU_MUST_SPECIFY + "the test set. Use: --" + ARG_TEST_SET + " [file-path]."
            )
        );
    }
    
    private static String getTokenFilePath (Args args) {
        return (
            args.getString (
                ARG_TOKEN_FILE,
                MESSAGE_YOU_MUST_SPECIFY + "the token file. Use: --" + ARG_TOKEN_FILE + " [file-path]."
            )
        );
    }
    
    private static FunctionalList <SimpleStringSegmentation> getTokenNgrams (Args args) {
        return (
            StringList.split (
                ",",
                args.getString (
                    ARG_TOKEN_NGRAMS,
                    MESSAGE_YOU_MUST_SPECIFY + "the n-grams."
                    + " Use: --" + ARG_TOKEN_NGRAMS + " [morf1"
                    + ",morf2" + GlobalSettings.HARD_DELIMITER_ALT + "morf3"
                    + ",morf4" + GlobalSettings.SOFT_DELIMITER + "morf5,...]."
                )
            )
            .map (ngram -> SimpleStringSegmentation.read (ngram, GlobalSettings.HARD_DELIMITER_ALT, GlobalSettings.SOFT_DELIMITER))
        );
    }
    
    private static int getVerbosity (Args args, int defaultValue, int maxValue) {
        return (
            args.getInteger (ARG_VERBOSITY)
            .orElseGet (() -> {
                System.err.println (
                    "Verbosity" + MESSAGE_UNKNOWN_USING_DEFAULT + defaultValue + "."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_VERBOSITY + " [level]."
                    + " Supported values are 0-" + maxValue + "."
                );
                return defaultValue;
            })
        );
    }
    
    private static String getWordFilter (Args args) {
        String defaultValue = TokenUsageAnalyser.FILTER_ALL;
        return (
            args.getString (ARG_WORD_FILTER).orElseGet (() -> {
                System.err.println (
                    "Word filter" + MESSAGE_UNKNOWN_USING_DEFAULT + defaultValue + "."
                    + MESSAGE_TO_CHANGE_IT_USE + ARG_WORD_FILTER + " ("
                        + TokenUsageAnalyser.FILTER_ALL
                        + "|" + TokenUsageAnalyser.FILTER_KNOWN
                        + "|" + TokenUsageAnalyser.FILTER_UNKNOWN
                    + ")."
                );
                return defaultValue;
            })
        );
    }
    
    private static void help () {
        printLines ("Substitus, version " + version (),
            StringUtils.repeat ('-', 80),
            "For some actions, you can get detailed help using substitus [action] --" + ARG_HELP + ".",
            "",
            "Used file formats and recommended extensions:",
            "* ." + GlobalSettings.EXTENSION_ARFF + " ... data for machine learning, used by Weka",
            "* ." + GlobalSettings.EXTENSION_FWL + " .... frequency word list, lines: frequency \\t word",
            "* ." + GlobalSettings.EXTENSION_SFWL + " ... segmented word list, lines: frequency \\t word \\t segmentation",
            "* ." + GlobalSettings.EXTENSION_SDS + " .... test set, lines: word \\t segment at i?on [" + GlobalSettings.COMMENT_MARK + " comment]",
            "* ." + GlobalSettings.EXTENSION_MCDS + " ... Morpho Challenge 2010 test set, lines: word \\t segmentations",
            "* ." + GlobalSettings.EXTENSION_TNL + " .... list of token n-grams (whether tuned or not)",
            "",
            "Basic usage:",
            "* Version info ................... substitus --version",
            "* Detailed word segmentation ..... substitus " + ACTION_SEGMENTIZE_WORDS,
            "* Text tokenization .............. substitus " + ACTION_TOKENIZE,
            "",
            "How to find subword tokens using a word-level-tokenized text:",
            "1. Create a frequency list ....... substitus " + ACTION_CREATE_FWL,
            "2. Convert it to lower case ...... substitus " + ACTION_CONVERT_FWL,
            "3. Segmentize it (unnormalized)... substitus " + ACTION_SEGMENTIZE_FWL,
            "4. a) Normalize & beautify it .... substitus " + ACTION_BEAUTIFY_SFWL,
            "   b) Inspect particular token ... substitus " + ACTION_SHOW_TOKEN_USAGE,
            "   c) I.  Make list of tokens .... substitus " + ACTION_FIND_TOKENS,
            "      II. Filter & order them .... substitus " + ACTION_TUNE_TOKENS,
            "",
            "Training & testing:",
            "* Run Morpho Challenge test ...... substitus " + ACTION_SEGMENTIZE_MC2010,
            "* Test segmenter settings ........ substitus " + ACTION_TEST_SETTINGS,
            "* Evaluate test of s. settings ... substitus " + ACTION_EVALUATE_SETTINGS,
            "* Evaluate token files ........... substitus " + ACTION_EVALUATE_TOKEN_FILES,
            "* Create train ARFF for scores ... substitus " + ACTION_CREATE_TRAIN_DATA,
            "",
            "(c) " + VERSION_YEAR + " Josef Plch (josefplch@mail.muni.cz)"
        );
    }
    
    private static void printLines (String ... lines) {
        for (String line : lines) {
            System.out.println (line);
        }
    }
    
    private static PairList <String, String> readFilePairs (String string) {
        return (
            StringList.split (",", string)
            .mapToPair (filePair -> {
                String [] chunks = filePair.split ("::");
                if (chunks.length != 2) {
                    throw new IllegalArgumentException ("Corrupted file pair: " + filePair);
                }
                else {
                    return Pair.of (chunks [0], chunks [1]);
                }
            })
        );
    }
    
    private static void runSingleSetting (
        UnaryOperator <Character> charPreprocessor,
        String frequencyListPath,
        Optional <Integer> frequencyListLimit,
        int kMostFrequent,
        int squareSize,
        BufferedReader inputReader,
        Function <String, String> wordExtractor,
        int verbosity,
        BiFunction <ProbabilisticStringSegmentation, String, String> printFunction
    ) throws IOException {
        Writer outputWriter = freshStdoutWriter ();
        
        BufferedReader frequencyListReader = new BufferedReader (new FileReader (frequencyListPath));
        StringSubstitus substitus =
            trainSubstitus (
                charPreprocessor,
                frequencyListReader,
                frequencyListLimit,
                outputWriter,
                Optional.empty (),
                verbosity
            );
        frequencyListReader.close ();
        
        substitus.setKMostFrequent (kMostFrequent);
        substitus.setSquareSize (squareSize);
        
        // Skipping can be used to continue an interrupted computation.
        int skip = 0;
        int n = 0;
        String line;
        while ((line = inputReader.readLine ()) != null && n < frequencyListLimit.orElse (Integer.MAX_VALUE)) {
            if (n < skip) {
                n++;
                continue;
            }
            
            String word = wordExtractor.apply (line);
            if (Application.DEBUG) {
                System.err.println ("* Segmenting word #" + n + ": " + word + ", memory usage: " + MemoryUtils.memoryUsageMessageMB ());
                System.err.flush ();
            }
            else if (n % 10_000 == 0) {
                System.err.println ("* Segmenting word #" + (n / 1_000) + "k: " + word + ", memory usage: " + MemoryUtils.memoryUsageMessageMB ());
                System.err.flush ();
            }
            
            ProbabilisticStringSegmentation segmentation = substitus.segmentizeP (word);
            
            if (Application.DEBUG) {
                ProbabilisticStringSegmentation normalized =
                    segmentation.normalize (
                        Substitus.DEFAULT_NORMALIZATION_MEAN
                    );
                System.err.println ("  Raw result: " + segmentation.toStringDecimal6 ());
                System.err.println ("  Normalized: " + normalized.toStringDecimal6 ());
                System.err.flush ();
            }
            
            outputWriter.append (printFunction.apply (segmentation, line));
            outputWriter.append ('\n');
            outputWriter.flush ();
            
            n++;
        }
        
        outputWriter.flush ();
    }
    
    private static void segmentizeFrequencyList (Args args) throws IOException {
        if (args.contains (ARG_HELP)) {
            printLines (
                "Segmentize a frequency list using itself as dictionary.",
                "",
                "Parameters:",
                "--" + ARG_CASE_SENSITIVE + " " + HELP_ARG_CASE_SENSITIVE,
                "--" + ARG_FREQUENCY_LIST_PATH + ": path to word frequency list",
                "--" + ARG_FREQUENCY_LIST_LIMIT + " " + HELP_ARG_FREQUENCY_LIST_LIMIT,
                "--" + ARG_K_MOST_FREQUENT + " (optional): change the default k most frequent parameter",
                "--" + ARG_SQUARE_SIZE + " (optional): change the default square size parameter"
            );
        }
        else {
            String frequencyListPath = getFrequencyListPath (args);
            runSingleSetting (
                getCaseSensitive (args),
                frequencyListPath,
                getFrequencyListLimit (args),
                getKMostFrequent (args),
                getSquareSize (args),
                new BufferedReader (new FileReader (frequencyListPath)),
                line -> FrequencyListEntry.read (line).getWord (),
                0,
                (segmentation, line) -> {
                    FrequencyListEntry entry = FrequencyListEntry.read (line);
                    return (
                        // TODO: would it be sufficient to use line + "\t" + segmentation?
                        entry.getFrequency ()
                        + "\t" + entry.getWord ()
                        + "\t" + entry.getLemmataAsc ().join (String.valueOf (GlobalSettings.LEMMA_DELIMITER))
                        // Not normalized.
                        + "\t" + segmentation.toStringDecimal6 ()
                    );
                }
            );
        }
    }
    
    private static void segmentizeMc2010 (Args args) throws IOException {
        double normalizationMean = getNormalizationMean (args);
        runSingleSetting (
            getCaseSensitive (args),
            getFrequencyListPath (args),
            getFrequencyListLimit (args),
            getKMostFrequent (args),
            getSquareSize (args),
            freshStdinReader (),
            line -> line.replaceFirst ("\t.*", ""),
            0,
            (segmentation, line) -> 
                segmentation.elements ()
                + "\t"
                + segmentation
                    .normalize (normalizationMean)
                    .binarize50S ()
                    .mapToString (t -> t + ":" + t)
                    .unwords ()
        );
    }
    
    private static void segmentizeWords (Args args) throws IOException {
        if (args.contains (ARG_HELP)) {
            printLines (
                "Segmentize words from standard input, one per line.",
                "",
                "Parameters:",
                "--" + ARG_CASE_SENSITIVE + " " + HELP_ARG_CASE_SENSITIVE,
                "--" + ARG_FREQUENCY_LIST_PATH + ": path to word frequency list used for segmentation",
                "--" + ARG_FREQUENCY_LIST_LIMIT + " " + HELP_ARG_FREQUENCY_LIST_LIMIT,
                "--" + ARG_K_MOST_FREQUENT + " (optional): change the default k most frequent parameter",
                "--" + ARG_SQUARE_SIZE + " (optional): change the default square size parameter",
                "--" + ARG_NORMALIZATION_MEAN + " " + HELP_ARG_NORMALIZATION_MEAN,
                "--" + ARG_OUTPUT_FORMAT + ": serialization function for the segmentation",
                "    * " + ARG_OUTPUT_FORMAT_BINARY + " ....... split if segmentability >= 0.5",
                "    * " + ARG_OUTPUT_FORMAT_BINARY_ALT + " ... split if segmentability >= 0.5 or length > 8",
                "    * " + ARG_OUTPUT_FORMAT_DECIMAL + " ...... show the segmentability as decimal number (e.g. 0.125000)",
                "    * " + ARG_OUTPUT_FORMAT_HTML + " ......... show using HTML markup",
                "    * " + ARG_OUTPUT_FORMAT_PERCENT + " ...... show the segmentability as percentage (e.g. 12.50%)",
                "--" + ARG_VERBOSITY + " (optional): amount of output information",
                "    * 0 ... print only the resulting segmentation (default)",
                "    * 1 ... for each position, print the segmentability and alternative words",
                "    * 2 ... for each position, print the table of affix combinations",
                "    * 3 ... extend the affix combinations table by scores",
                "    * 4 ... print most similar affixes in different orderings",
                "    * 5 ... print most similar affixes with details, one per line"
            );
        }
        else {
            Function <ProbabilisticStringSegmentation, String> binarizer = getOutputFormatA (args);
            double normalizationMean = getNormalizationMean (args);
            runSingleSetting (
                getCaseSensitive (args),
                getFrequencyListPath (args),
                getFrequencyListLimit (args),
                getKMostFrequent (args),
                getSquareSize (args),
                freshStdinReader (),
                String :: trim,
                getVerbosity (args, 0, 4),
                (segmentation, line) ->
                    binarizer.apply (
                        segmentation.normalize (normalizationMean)
                    )
            );
        }
    }
    
    private static <A> FunctionalList <A> optionalTake (Optional <Integer> n, FunctionalList <A> list) {
        return n.map (value -> list.take (value)).orElse (list);
    }
    
    // Since 2020-11-08
    private static void showTokenUsage (Args args) throws IOException {
        if (args.contains (ARG_HELP)) {
            printLines ("Look up all words containing some of the given token n-grams. If there are multiple n-gram occurences in a word, only the first one is considered.",
                "",
                "Parameters:",
                "--" + ARG_TOKEN_NGRAMS + ": list of token n-grams to look for",
                "--" + ARG_NORMALIZATION_MEAN + " " + HELP_ARG_NORMALIZATION_MEAN,
                "--" + ARG_MAX_WORD_LENGTH + " " + HELP_ARG_MAX_WORD_LENGTH,
                "--" + ARG_WORD_FILTER + ": all (default), known, unknown",
                "--" + ARG_LEMMATIZE + ": instead of words, evaluate their lemmata (where known)",
                "--" + ARG_VERBOSITY + " (optional): [0..1], amount of output information",
                "    * 0 ... print score and word (default)",
                "    * 1 ... print also simple segmentations",
                "    * 2 ... print the segmentations with more details"
            );
        }
        else {
            TokenUsageAnalyser.analyse (
                freshStdinReader (),
                // TODO: Rename/remove the limit?
                getFrequencyListLimit (args),
                getMaxWordLength (args),
                getNormalizationMean (args),
                getTokenNgrams (args),
                getWordFilter (args),
                getLemmatize (args),
                getMaxResultSize (args),
                getVerbosity (args, 0, 1),
                freshStdoutWriter ()
            );
        }
    }
    
    // Test different settings of Substitus, using full grid search.
    private static void testSettings (Args args) throws IOException {
        String frequencyListPath = getFrequencyListPath (args);
        Writer writer = freshStdoutWriter ();
        
        SubstitusSettingTester settingsTester =
            new SubstitusSettingTester (
                IntegerList.ofNumbers (1), // 10, 100, 1000
                IntegerList.ofNumbers (16, 23, 32, 45, 64, 91, 128, 181, 256),
                IntegerList.ofNumbers (4, 5, 6, 7, 8, 10, 12, 15, 20),
                // The results are not normalized.
                ProbabilisticStringSegmentation :: toStringDecimal6
            );
        
        BufferedReader frequencyListReader = new BufferedReader (new FileReader (frequencyListPath));
        settingsTester.runTest (
            trainSubstitus (
                getCaseSensitive (args),
                frequencyListReader,
                getFrequencyListLimit (args),
                writer,
                Optional.empty (),
                0
            ),
            // For comment purposes only.
            frequencyListPath,
            // Use the test set words as input.
            TestSet.readFile (getTestSet (args)).stream (),
            writer
        );
        frequencyListReader.close ();
        
        writer.flush ();
    }
    
    private static void tokenize (Args args) throws IOException {
        if (args.contains (ARG_HELP)) {
            printLines (
                "Tokenize text from standard input.",
                "",
                "Parameters:",
                "--" + ARG_CASE_SENSITIVE + " " + HELP_ARG_CASE_SENSITIVE,
                "--" + ARG_TOKEN_FILE + ": path to list of token n-grams used for tokenization",
                "--" + ARG_OUTPUT_FORMAT + ": how to delimit the tokens and words",
                "    * " + ARG_OUTPUT_FORMAT_BPE + " ..... segment@@ ed word@@ s",
                "    * " + ARG_OUTPUT_FORMAT_DASH + " .... segment-ed word-s",
                "    * " + ARG_OUTPUT_FORMAT_SPACE + " ... segment ed [space] word s"
            );
        }
        else {
            StringSubstitusTokenizer tokenizer =
                StringSubstitusTokenizer.loadTokenFile (
                    getCaseSensitive (args),
                    getTokenFilePath (args)
                );
            UniformPair <String> delimiters = getOutputFormatB (args);
            freshStdinReader ().lines ().forEach (line -> {
                System.out.println (
                    StringList.split (" ", line)
                    .map (tokenizer :: segmentize)
                    .mapToString (tokens -> tokens.join (delimiters.get1 ()))
                    .join (delimiters.get2 ())
                );
            });
            System.out.flush ();
        }
    }
    
    private static StringSubstitus trainSubstitus (
        UnaryOperator <Character> charPreprocessor,
        BufferedReader frequencyListReader,
        Optional <Integer> frequencyListLimit,
        Writer outputWriter,
        Optional <Pair <Writer, Boolean>> arffWriter,
        int verbosity
    ) throws IOException {
        System.err.println ("Building tries over the frequency list ...");
        System.err.flush ();
        
        StringSubstitus result =
            StringSubstitus.train (
                charPreprocessor,
                frequencyListReader,
                frequencyListLimit,
                outputWriter,
                arffWriter,
                verbosity
            );
        System.err.println ("The tries are built, they cover " + result.uniqueCompoundsCount () + " words.");
        System.err.flush ();
        
        return result;
    }
    
    private static void tuneTokens (Args args) throws IOException {
        if (args.contains (ARG_HELP)) {
            printLines (
                "Tune list of tokens (filter and order it) in different ways.",
                "",
                "Parameters:",
                "--" + ARG_INPUT_FILES + " (optional): if not specified, use standard input",
                "--" + ARG_MAX_NGRAM_LEVEL + " (optional): limit the number of tokens in n-grams",
                "--" + ARG_MAX_NGRAM_LENGTH + " (optional): use only n-grams with length <= n",
                "--" + ARG_MIN_NGRAM_TF + " (optional): use only n-grams with frequency >= n",
                "--" + ARG_MIN_NGRAM_IWF + " (optional): use only n-grams with productivity >= n",
                "--" + ARG_MAX_TOKEN_LENGTH + " (optional): all n-gram tokens must have length <= n",
                "--" + ARG_MIN_TOKEN_TF + " (optional): all n-gram tokens must have frequency >= n",
                "--" + ARG_MIN_TOKEN_IWF + " (optional): all n-gram tokens must have product. >= n",
                "--" + ARG_OUTPUT_DIRECTORY + ": the directory to store the resulting files",
                "--" + ARG_OUTPUT_PREFIX + ": name prefix of the resulting files"
            );
        }
        else {
            Optional <StringList> inputFiles =
                args.read (
                    ARG_INPUT_FILES,
                    arg -> StringList.split (",", arg)
                );
            String outputDirectory =
                args.getString (
                    ARG_OUTPUT_DIRECTORY,
                    MESSAGE_YOU_MUST_SPECIFY + "the output directory. Use: --" + ARG_OUTPUT_DIRECTORY + " [directory-path]."
                );
            String outputFilePrefix =
                args.getString (
                    ARG_OUTPUT_PREFIX,
                    MESSAGE_YOU_MUST_SPECIFY + "the name prefix of output files. Use: --" + ARG_OUTPUT_PREFIX + " [name-prefix]."
                );

            TokenTuner tokenTuner =
                new TokenTuner (
                    getMaxNgramLevel (args),
                    getMaxNgramLength (args),
                    getMinNgramTf (args),
                    getMinNgramIwf (args),
                    getMaxTokenLength (args),
                    getMinTokenTf (args),
                    getMinTokenIwf (args)
                );

            if (inputFiles.isPresent ()) {
                tokenTuner.tuneFiles (
                    inputFiles.get (),
                    outputDirectory,
                    outputFilePrefix
                );
            }
            else {
                System.err.println (
                    "Input files not specified, using the standard input."
                    + " If you want to use multiple input files, use --"
                    + ARG_INPUT_FILES + " [file-path-1],..."
                );
                tokenTuner.tuneFile (
                    freshStdinReader (),
                    outputDirectory,
                    outputFilePrefix
                );
            }
        }
    }
    
    private static String version () {
        return (VERSION + (DEBUG ? " [debug]" : ""));
    }
}
