package com.lingea.segmenter;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.string.DoubleFormatter;
import com.lingea.segmenter.substitus.TokenTuner;
import static com.lingea.segmenter.Application.*; // Constants.
import com.lingea.segmenter.data.token.ParametrizedNgram;
import com.lingea.segmenter.utils.RandomWordGenerator;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * @author  Josef Plch
 * @since   2019-11-11
 * @version 2020-12-04
 */
public abstract class ApplicationTest {
    private static final String TEST_WORDS =
        StringList.ofStrings (
            // "žadatelka", "nedutnout", "turistka", "zálibně", "zakrátko", "bandaska", "spjatý", "unikum", "heraldický", "snášenlivě", "počínat", "rozloučení", "říčka", "sešlápnutý", "kraťas", "nahmatat", "axióm", "překrásně", "medový", "kvapit"
            // "jiskrou"
            // "originated"
            // "need", "needle", "needles", "needless"
            // "strawberry"
            // "hudebník", "jablka", "jihomoravském", "Londýně", "nějak", "Nokia", "papež", "pověřeným", "průmyslovou", "relativní", "svět", "tisíci", "určitý"
            // "město", "města", "městu", "městě", "městem", "města", "měst", "městům", "městech", "městy", "městský", "městského", "městskému", "městském", "městským"
            // "cukr", "cukru", "cukrem", "cukry", "cukrů", "cukrům", "cukrech", "cukřík", "cukrovka", "cukřenka", "cukrovat", "pocukrovat", "pocukrovaný", "cukernatý", "cukernatost"
            // MGR / Smerk
            // "bandaska", "heraldický", "lombardský", "lombarský", "nedutnout", "německý", "obsáhnout", "obsahovat", "obsahuje", "pramatčin", "prapramatka", "prapraprababička", "sešlápnutý", "snášenlivě", "spjatý", "turistka", "unikum", "vlci", "vlčí", "vlkův", "zakrátko", "zálibně", "žadatelka"
            // "companies", "theoretical", "theories", "theory"
            // "polka.mp3", "valcik.mp3", "tango.mp3"
            // Pavel / boj
            // "boj", "náboj", "výboj", "odboj", "bojiště", "bojovný", "bojovník", "bojar", "nábojnice", "odbojář", "průbojný", "průbojník", "spolubojovník", "výbojka", "NE", "podobojí", "bojácný", "obojaký", "obojek", "kovbojka"
            // Pavel / moc
            "moc", "nemoc", "nemocný", "pomoc", "pomocník", "výpomoc", "mocnářství", "bezmocný", "jednomocný", "malomocenství", "mermomocí", "mocenský", "mocichtivý", "mockrát", "mocnina", "umocnění", "odmocnina", "zmocněnec", "nemocnice"
        ).unlines ();
    
    private static StringList filePaths (String dirPath) throws IOException {
        return (
            new StringList (
                Files.walk (Paths.get (dirPath))
                .filter (Files :: isRegularFile)
                .map (Object :: toString)
                .collect (Collectors.toList ())
            )
        );
    }
    
    public static void main (String [] args) {
        try {
            final long startTime = System.nanoTime ();
            
            // Tests:
            // testHelp ();
            // testBasicFunctionality ();
            // testTokens ();
            // testEvaluation ();
            // testArff ();
            
            if (true) {
                genericTest (
                    ACTION_SHOW_TOKEN_USAGE
                        // Motor: motorista, elektromotor vs motorola, pneumotorax
                        // bá_t,bá_l,boj_í
                        // běl,bíl
                        // čoko,čoko?lád
                        // kon?v,kon?ev,kon?év,kon?ýv
                        // kop?c,kop?ec,kop?eč
                        // kro?k,kro?č
                        // mod?r,mod?ř
                        // oh?n,oh?ň,oh?eň,oh?ýn ... representative example
                        // skok,skak,skák,skoč,skač,skáč
                        // skop ... homonymous: skopové vs mikroskop
                        // sla?d,sla?ď,sla?zen
                        // ti?sk,ti?šť,ti?štěn
                        // uch,uš,uš?i,uš?í,uš?ní
                        // vr?h,vr?zích,vr?žen
                        // TODO: known words / záchod
                        + " --frequency-list-limit 100k"
                        + " --max-result-size 100k"
                        + " --max-word-length 15"
                        + " --normalization-mean 0.70"
                        + " --token-ngrams e?m_@,a_@,o_@,ý_@,t_@"
                        + " --word-filter unknown"
                        // + " --lemmatize"
                        + " --verbosity 1",
                    // fileInput (DataPaths.DATA_DIR + "/frequency_lists/cs_lingeaAll2016_lc_2m_d2m.sfwl"),
                    fileInput (DataPaths.DATA_DIR + "/frequency_lists/cs_lingeaAll2020_lc_3m_d3m.sfwl"),
                    fileOutput (DataPaths.DATA_DIR + "/frequency_lists/cs_lingeaAll2020_lc_3m_d3m_unknown_NEW.txt")
                    // System.out
                );
            }
            
            if (false) {
                genericTest (
                    ACTION_BEAUTIFY_SFWL
                        + " --frequency-list-limit 100k"
                        + " --max-word-length 20"
                        + " --normalization-mean 0.7",
                    fileInput (DataPaths.DATA_DIR + "/frequency_lists/cs_hawk2016C_lc.sfwl"),
                    fileOutput (DataPaths.TEST_DIR + "/cs_beautified_NEW.txt")
                );
            }
            
            if (false) {
                genericTest (
                    ACTION_CREATE_FWL,
                    fileInput  ("/media/jozka/Data/DATA/Korpusy/Jednojazyčné/CS (čeština)/cs_hawk2016C.txt"),
                    fileOutput ("/media/jozka/Data/DATA/Korpusy/Jednojazyčné/CS (čeština)/cs_hawk2016C_oc_NEW.fwl")
                );
            }
            
            if (false) {
                genericTest (
                    ACTION_CONVERT_FWL
                        + " --letter-case lower"
                        + " --max-word-length 50"
                        + " --only-words"
                        + " --min-variant-share 0.10"
                        + " --max-result-size 5M",
                    fileInput  (DataPaths.DATA_DIR + "/frequency_lists/cs_lingeaAll2020_03_lemmatized.fwl"),
                    fileOutput (DataPaths.DATA_DIR + "/frequency_lists/cs_lingeaAll2020_lc_5m_NEW.fwl")
                    // fileInput  ("/media/jozka/Data/DATA/Korpusy/Jednojazyčné/CS (čeština)/cs_hawk2016C_oc.fwl"),
                    // fileOutput ("/media/jozka/Data/DATA/Korpusy/Jednojazyčné/CS (čeština)/cs_hawk2016C_lc_NEW.fwl")
                );
            }
            
            if (false) {
                // Words: afterworld, football, properties, unnecessary
                // SK,  500k ... OK (1 min 23 s)
                // SK, 1000k ... OK (3 min 22 s)
                // SK, 1500k ... OK (4 min 56 s)
                // SK, 1900k ... fail, trie built (9 min 7 s, OutOfMemoryError: GC overhead limit exceeded)
                // SK, 2000k ... fail, trie not built (16 s, stack overflow)
                genericTest (
                    ACTION_SEGMENTIZE_MC2010
                        + " --case-sensitive"
                        + " --frequency-list " + DataPaths.FREQUENCY_TR_MC2010
                        + " --frequency-list-limit 2M"
                        + " --verbosity 0",
                    fileInput ("F:\\Substitus\\morpho_challenge_2010\\goldstd_combined.segmentation.tur"),
                    fileOutput ("F:\\Substitus\\morpho_challenge_2010\\substitus_NEW.tur")
                );
            }
            
            if (false) {
                // Words: afterworld, football, properties, unnecessary
                // dv-ou, hr-áč/hr-át, ne-jí/vy-jí/za-jí, ro-ce, ru-ce, sv-ém/sv-ou/sv-ým, tv-ém/tv-ou/tv-ým
                // homonyma: přejí, vyšli
                // SK,  500k ... OK (1 min 23 s)
                // SK, 1000k ... OK (3 min 22 s)
                // SK, 1500k ... OK (4 min 56 s)
                // SK, 1900k ... fail, trie built (9 min 7 s, OutOfMemoryError: GC overhead limit exceeded)
                // SK, 2000k ... fail, trie not built (16 s, stack overflow)
                genericTest (
                    ACTION_SEGMENTIZE_WORDS
                        // + " --case-sensitive"
                        + " --frequency-list " + DataPaths.FREQUENCY_CS_ALL2016
                        + " --frequency-list-limit 50k"
                        // + " --k-most-frequent 256"
                        // + " --square-size 10"
                        // + " --normalization-mean 0.75"
                        + " --output-format binary" // binary, decimal, percent, html
                        + " --verbosity 1",
                    stringInput ("černobílý\nplynoměr\npolotovar"), // hrobník
                    // stringInput (TEST_WORDS),
                    System.out
                );
            }
            
            if (false) {
                genericTest (
                    ACTION_TOKENIZE
                        + " --" + ARG_TOKEN_FILE + " " + DataPaths.TUNED_TOKENS_DIR + "/cs_lingeaAll_d2m_n3_nl12_tl10_tf1000_tp2_ordered_exp_length_mult_fixed_tf_ratio_u45919.tnl"
                        + " --" + ARG_OUTPUT_FORMAT + " space",
                    stringInput (TEST_WORDS), // nejneobhospodářovávatelnějšími
                    System.out
                );
            }
            
            /*
             * SK, d2M: GC overhead limit exceeded (after 14,700 words)
             */
            if (false) {
                String path = DataPaths.DATA_DIR + "/frequency_lists/cs_lingeaAll2020_lc_5m.fwl";
                // String path = DataPaths.DATA_DIR + "/frequency_lists/cs_hawk2016C_lc.fwl";
                genericTest (
                    ACTION_SEGMENTIZE_FWL
                        + " --frequency-list " + path
                        + " --frequency-list-limit 1M",
                    fileInput (path),
                    fileOutput (DataPaths.DATA_DIR + "/frequency_lists/cs_lingeaAll2020_lc_3m_d3m_NEW." + GlobalSettings.EXTENSION_SFWL)
                    // fileOutput (DataPaths.TEST_DIR + "/cs_hawk2016C_lc." + GlobalSettings.EXTENSION_SFWL)
                );
            }
            
            // System.out.println (TestSet.readFile (DataPaths.TEST_SET_EN_MC_2010));
            
            if (false) {
                genericTest (
                    ACTION_FIND_TOKENS
                        + " --frequency-list-limit 2M"
                        + " --max-ngram-level 3"
                        + " --min-ngram-frequency 5",
                    fileInput (DataPaths.FREQUENCY_LIST_DIR + "/cs_hawk2016C_lc." + GlobalSettings.EXTENSION_SFWL),
                    fileOutput (DataPaths.RAW_TOKENS_DIR + "/cs_hawk2016C_lc_n3_nf5_NEW." + GlobalSettings.EXTENSION_TNL)
                );
            }
            
            if (false) {
                int i = 0;
                for (int n : FunctionalList.of (1, 2, 3)) {
                    for (int nl : FunctionalList.of (6, 8, 10, 12, 14)) {
                        // for (int nf : FunctionalList.of (100)) {
                        for (int nf : FunctionalList.of (10)) {
                            // Token productivity has the same effect.
                            for (int np : FunctionalList.of (1)) {
                                for (int tl : FunctionalList.of (6, 8, 10, 12, 14)) {
                                    // for (int tf : FunctionalList.of (100, 1000, 10000)) {
                                    for (int tf : FunctionalList.of (10, 100, 1000)) {
                                        for (int tp : FunctionalList.of (1, 2, 4, 8)) {
                                            if (nl >= tl && nf <= tf && np <= tp) {
                                                System.err.println ("Setting #" + i);
                                                genericTest (
                                                    ACTION_TUNE_TOKENS
                                                        + " --max-ngram-level " + n
                                                        + " --max-ngram-length " + nl
                                                        + " --min-ngram-frequency " + nf
                                                        + " --min-ngram-productivity " + np
                                                        + " --max-token-length " + tl
                                                        + " --min-token-frequency " + tf
                                                        + " --min-token-productivity " + tp
                                                        + " --output-dir " + DataPaths.TUNED_TOKENS_DIR + "/test_en_mc2010_NEW"
                                                        + " --output-prefix en_n" + n + "_nl" + nl + "_nf" + nf + "_np" + np + "_tl" + tl + "_tf" + tf + "_tp" + tp,
                                                    // fileInput (DataPaths.RAW_TOKENS_DIR + "/cs_lingeaAll_lc_d2m_n3_nf100_u244358." + GlobalSettings.EXTENSION_TNL),
                                                    // fileInput (DataPaths.RAW_TOKENS_DIR + "/en_lingeaAll_lc_d2m_n3_nf100_u278419." + GlobalSettings.EXTENSION_TNL),
                                                    fileInput (DataPaths.RAW_TOKENS_DIR + "/en_mc2010_lc_870k_d870k_n3_nf5_u114927." + GlobalSettings.EXTENSION_TNL),
                                                    // fileInput (DataPaths.RAW_TOKENS_DIR + "/hu_lingeaAll_lc_d1m_n3_nf100_u94927." + GlobalSettings.EXTENSION_TNL),
                                                    System.out
                                                );
                                                i++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if (false) {
                genericTest (
                    ACTION_TUNE_TOKENS
                        + " --max-ngram-level " + 2 // 3
                        + " --max-ngram-length " + 12
                        + " --max-token-length " + 10
                        + " --min-token-frequency " + 50
                        + " --min-token-productivity " + 2
                        + " --output-dir " + DataPaths.TUNED_TOKENS_DIR
                        + " --output-prefix cs_hawk2016C_lc_n3_nf5",
                    // fileInput (DataPaths.RAW_TOKENS_DIR + "/cs_lingeaAll_lc_d2m_n3_nf100_u244358." + GlobalSettings.EXTENSION_TNL),
                    // fileInput (DataPaths.RAW_TOKENS_DIR + "/de_lingeaAll_lc_d1m_n3_nf100_u152970." + GlobalSettings.EXTENSION_TNL),
                    // fileInput (DataPaths.RAW_TOKENS_DIR + "/en_lingeaAll_lc_d2m_n3_nf100_u278419." + GlobalSettings.EXTENSION_TNL),
                    // fileInput (DataPaths.RAW_TOKENS_DIR + "/en_mc2010_lc_870k_d870k_n3_nf5_u114927." + GlobalSettings.EXTENSION_TNL),
                    // fileInput (DataPaths.RAW_TOKENS_DIR + "/fi_mc2010_lc_d1m_n4_nf50_u30855." + GlobalSettings.EXTENSION_TNL),
                    // fileInput (DataPaths.RAW_TOKENS_DIR + "/hu_lingeaAll_lc_d1m_n3_nf100_u94927." + GlobalSettings.EXTENSION_TNL),
                    // fileInput (DataPaths.RAW_TOKENS_DIR + "/sk_lingeaAllSum2016_lc_1m_d1m_n3_nf5." + GlobalSettings.EXTENSION_TNL),
                    fileInput (DataPaths.RAW_TOKENS_DIR + "/cs_hawk2016C_lc_n3_nf5." + GlobalSettings.EXTENSION_TNL),
                    System.out
                );
            }
            
            StringList filesPaths = filePaths (DataPaths.TUNED_TOKENS_DIR + "/test_en_mc2010_540");
            
            if (false) {
                int n = 50;
                System.err.println ("Using " + n + "/" + filesPaths.size () + " files");
                filesPaths = filesPaths.shuffle ().take (n);
            }
            
            if (false) {
                // System.out.println (filesPaths.mapToStringN ((n, path) -> n + ": " + path).unlines ());
                
                genericTest (
                    ACTION_EVALUATE_TOKEN_FILES
                        + " --input-files " + filesPaths.join (",")
                        + " --test-set " + DataPaths.TEST_SET_EN_MC2010,
                    System.in,
                    System.out
                );
            }
            
            // evaluateMC2010 ();
            
            System.err.println (
                "Total test time: "
                + DoubleFormatter.POINT_3.format ((System.nanoTime () - startTime) / 1_000_000_000.0)
                + " s"
            );
        }
        catch (IOException | InterruptedException exception) {
            throw new RuntimeException (exception);
        }
    }
    
    private static void evaluateMC2010 () throws FileNotFoundException, InterruptedException {
        if (false) {
            // FI/1M: 2 hours
            // EN/878k + FI/2M: 12 hours
            // EN/878k/s1: 80 seconds
            System.err.println ("== Testing settings ==");
            genericTest (
                ACTION_TEST_SETTINGS
                    + " --frequency-list " + DataPaths.FREQUENCY_EN_MC2010
                    + " --frequency-list-limit 2M"
                    // + " --case-sensitive"
                    + " --test-set " + DataPaths.TEST_SET_EN_MC2010,
                System.in,
                fileOutput (DataPaths.DATA_DIR + "/test_of_settings/en_mc2010_80s_d878k_mc2010_NEW.txt")
            );
        }
        
        if (false) {
            System.err.println ("== Evaluating settings ==");
            genericTest (
                ACTION_EVALUATE_SETTINGS
                    + " --input-files"
                        + " " + DataPaths.DATA_DIR + "/test_of_settings/en_mc2010_d878k_mc2010.txt"
                        + "::" + DataPaths.TEST_SET_EN_MC2010
                    + " --verbosity 2",
                System.in,
                System.out
            );
        }
        
        // fi_mc2010_d1M_n3_d100: 20 h
        // segmentizeFrequencyList (DataPaths.FREQUENCY_FI_MC_2010, "2M", DataPaths.FREQUENCY_LIST_DIR + "/fi_mc2010_lc_d2m_NEW." + GlobalSettings.EXTENSION_SFWL);

        // fi_mc2010_d1m_n3_f100: 4.5 h
        // fi_mc2010_d1m_n4_f50: 26.0 h
        if (false) {
            genericTest (
                ACTION_FIND_TOKENS
                    + " --frequency-list-limit 1M"
                    + " --max-ngram-level 4"
                    + " --min-ngram-frequency 50",
                fileInput (DataPaths.FREQUENCY_LIST_DIR + "/fi_mc2010_lc_1m_d1m." + GlobalSettings.EXTENSION_SFWL),
                fileOutput (DataPaths.RAW_TOKENS_DIR + "/fi_mc2010_d1m_n4_f50_NEW." + GlobalSettings.EXTENSION_TNL)
            );
        }

        if (false) {
            genericTest (
                ACTION_TUNE_TOKENS
                    + " --max-ngram-level 1"
                    + " --min-ngram-frequency 50"
                    + " --min-ngram-productivity 4"
                    + " --output-dir " + DataPaths.TUNED_TOKENS_DIR
                    + " --output-prefix fi_mc2010_d1m_n1_nf50_np4",
                fileInput (DataPaths.RAW_TOKENS_DIR + "/fi_mc2010_d1m_bin50_n4_f50_u30855." + GlobalSettings.EXTENSION_TNL),
                System.out
            );
        }
        
        if (false) {
        // Direct analysis: Acc 0.844, Pre 0.590, Rec 0.640, FSc 0.614, MCC 0.517
        genericTest (
                ACTION_EVALUATE_TOKEN_FILES
                    + " --input-files " + StringList.ofStrings (
                        DataPaths.TUNED_TOKENS_DIR + "/fi_mc2010_d1m_n1_nf50_np1_u28177_ordered_exp_length_mult_fixed_iwf_ratio_TIP.tnl", // Acc 0.849, Pre 0.606, Rec 0.630, FSc 0.618, MCC 0.524 // ***
                        DataPaths.TUNED_TOKENS_DIR + "/fi_mc2010_d1m_n1_nf50_np2_u25861_ordered_exp_length_mult_fixed_iwf_ratio_TIP.tnl", // Acc 0.849, Pre 0.604, Rec 0.631, FSc 0.617, MCC 0.523 // **
                        DataPaths.TUNED_TOKENS_DIR + "/fi_mc2010_d1m_n1_nf50_np4_u22275_ordered_exp_length_mult_fixed_iwf_ratio_TIP.tnl", // Acc 0.846, Pre 0.596, Rec 0.632, FSc 0.613, MCC 0.517 // *
                        DataPaths.TUNED_TOKENS_DIR + "/fi_mc2010_d1m_n2_nf50_np1_u23796_ordered_exp_length_mult_fixed_iwf_ratio_TIP.tnl", // Acc 0.833, Pre 0.556, Rec 0.685, FSc 0.614, MCC 0.513 // *
                        DataPaths.TUNED_TOKENS_DIR + "/fi_mc2010_d1m_n2_nf50_np2_u21701_ordered_exp_length_mult_fixed_iwf_ratio_TIP.tnl", // Acc 0.833, Pre 0.555, Rec 0.686, FSc 0.614, MCC 0.513 // *
                        DataPaths.TUNED_TOKENS_DIR + "/fi_mc2010_d1m_n2_nf50_np4_u18534_ordered_exp_length_mult_fixed_iwf_ratio_TIP.tnl", // Acc 0.828, Pre 0.545, Rec 0.684, FSc 0.606, MCC 0.504
                        DataPaths.TUNED_TOKENS_DIR + "/fi_mc2010_d1m_n3_nf50_np1_u23537_ordered_exp_length_mult_fixed_iwf_ratio_TIP.tnl", // Acc 0.831, Pre 0.550, Rec 0.695, FSc 0.614, MCC 0.513 // *
                        DataPaths.TUNED_TOKENS_DIR + "/fi_mc2010_d1m_n3_nf50_np2_u21476_ordered_exp_length_mult_fixed_iwf_ratio_TIP.tnl", // Acc 0.831, Pre 0.550, Rec 0.695, FSc 0.614, MCC 0.513 // *
                        DataPaths.TUNED_TOKENS_DIR + "/fi_mc2010_d1m_n3_nf50_np4_u18347_ordered_exp_length_mult_fixed_iwf_ratio_TIP.tnl", // Acc 0.826, Pre 0.540, Rec 0.694, FSc 0.608, MCC 0.505
                        DataPaths.TUNED_TOKENS_DIR + "/fi_mc2010_d1m_n4_nf50_np1_u23513_ordered_exp_length_mult_fixed_iwf_ratio_TIP.tnl", // Acc 0.830, Pre 0.548, Rec 0.697, FSc 0.613, MCC 0.512
                        DataPaths.TUNED_TOKENS_DIR + "/fi_mc2010_d1m_n4_nf50_np2_u21456_ordered_exp_length_mult_fixed_iwf_ratio_TIP.tnl", // Acc 0.830, Pre 0.548, Rec 0.698, FSc 0.614, MCC 0.513
                        DataPaths.TUNED_TOKENS_DIR + "/fi_mc2010_d1m_n4_nf50_np4_u18331_ordered_exp_length_mult_fixed_iwf_ratio_TIP.tnl"  // Acc 0.826, Pre 0.539, Rec 0.696, FSc 0.608, MCC 0.505
                    ).join (",")
                    + " --test-set " + DataPaths.TEST_SET_FI_MC2010,
                System.in,
                System.out
            );
        }
    }
    
    private static FileInputStream fileInput (String filePath) throws FileNotFoundException {
        return new FileInputStream (filePath);
    } 
    
    private static PrintStream fileOutput (String filePath) throws FileNotFoundException {
        return new PrintStream (filePath);
    }
    
    private static void genericTest (String args, InputStream in, PrintStream out) throws FileNotFoundException, InterruptedException {
        System.err.println ("Running test: " + args);
        long safetyPause = 100;
        
        // Backup.
        InputStream stdin = System.in;
        PrintStream stdout = System.out;
        
        Thread.sleep (safetyPause);
        
        // Redirect standard input & output.
        System.setIn (in);
        System.setOut (out);
        Application.main (args.split (" "));
        
        // Restore the original standard input & output.
        System.setIn (stdin);
        System.setOut (stdout);
        
        Thread.sleep (safetyPause);
    }
    
    private static void segmentizeFrequencyList (String frequencyListPath, String limit, String outputFilePath) throws FileNotFoundException, InterruptedException {
        genericTest (
            ACTION_SEGMENTIZE_FWL
                + " --" + ARG_FREQUENCY_LIST_PATH + " " + frequencyListPath
                + " --" + ARG_FREQUENCY_LIST_LIMIT + " " + limit,
            fileInput (frequencyListPath),
            fileOutput (outputFilePath)
        );
    }
    
    private static InputStream stringInput (String string) {
        return new ByteArrayInputStream (string.getBytes ());
    }
    
    private static void testArff () throws FileNotFoundException, InterruptedException {
        System.err.println ("== Creating ARFF ==");
        genericTest (
            ACTION_CREATE_TRAIN_DATA
                + " --input-files"
                    + " " + DataPaths.FREQUENCY_CS_ALL2020 + "::" + DataPaths.TEST_SET_CS_PLCH
                    // + "," + DataPaths.FREQUENCY_EN_ALL + "::" + DataPaths.TEST_SET_EN_PLCH
                    // + "," + DataPaths.FREQUENCY_HU_ALL + "::" + DataPaths.TEST_SET_HU_PLCH
                + " --frequency-list-limit 200k",
            System.in,
            fileOutput (DataPaths.TEST_DIR + "/test_arff.arff")
        );
    }
    
    private static void testBasicFunctionality () throws FileNotFoundException, InterruptedException {
        System.err.println ("== Version info ==");
        genericTest ("--version", System.in, System.out);
        
        System.err.println ("== Segmenting standard input ==");
        genericTest (
            ACTION_SEGMENTIZE_WORDS
                + " --frequency-list " + DataPaths.FREQUENCY_CS_ALL2016
                + " --frequency-list-limit 50k"
                + " --output-format binary"
                + " --verbosity 0",
            fileInput (DataPaths.TEST_DIR + "/test_00_stdin.txt"),
            System.out
        );
        genericTest (
            ACTION_SEGMENTIZE_WORDS
                + " --frequency-list " + DataPaths.FREQUENCY_CS_ALL2016
                + " --frequency-list-limit 50k"
                + " --normalization-mean 0.6"
                + " --output-format binary"
                + " --verbosity 0",
            fileInput (DataPaths.TEST_DIR + "/test_00_stdin.txt"),
            System.out
        );
        
        System.err.println ("== Tokenizing standard input ==");
        genericTest (
            ACTION_TOKENIZE
                + " --" + ARG_TOKEN_FILE + " " + DataPaths.TUNED_TOKENS_DIR + "/cs_lingeaAll_d2m_n2_nl12_tl10_tf100_tp4_ordered_exp_length_mult_fixed_tf_ratio_u54924.tnl"
                + " --" + ARG_OUTPUT_FORMAT + " space",
            fileInput (DataPaths.TEST_DIR + "/test_00_stdin.txt"),
            System.out
        );
    }
    
    private static void testEvaluation () throws FileNotFoundException, InterruptedException {
        System.err.println ("== Testing settings ==");
        genericTest (
            ACTION_TEST_SETTINGS
                + " --frequency-list " + DataPaths.FREQUENCY_CS_ALL2016
                + " --frequency-list-limit 50k"
                + " --test-set " + DataPaths.TEST_SET_CS_PLCH,
            System.in,
            fileOutput (DataPaths.TEST_DIR + "/test_of_settings.txt")
        );
        
        System.err.println ("== Evaluating settings ==");
        genericTest (
            ACTION_EVALUATE_SETTINGS
                + " --input-files"
                    + " " + DataPaths.TEST_DIR + "/test_of_settings.txt"
                    + "::" + DataPaths.TEST_SET_CS_PLCH
                + " --verbosity 2",
            System.in,
            System.out
        );
    }
    
    private static void testHelp () throws FileNotFoundException, InterruptedException {
        System.err.println ("== No arguments ==");
        genericTest ("", System.in, System.out);
        
        System.err.println ("== Basic help ==");
        genericTest ("--help", System.in, System.out);
        
        StringList actions =
            StringList.ofStrings (ACTION_SEGMENTIZE_WORDS,
                ACTION_TOKENIZE,
                // ACTION_CONVERT_FWL,
                // ACTION_CREATE_FWL,
                ACTION_SEGMENTIZE_FWL,
                // ACTION_SEGMENTIZE_MC2010,
                ACTION_BEAUTIFY_SFWL,
                ACTION_SHOW_TOKEN_USAGE,
                ACTION_FIND_TOKENS,
                ACTION_TUNE_TOKENS
                // ACTION_CREATE_TRAIN_DATA,
                // ACTION_TEST_SETTINGS,
                // ACTION_EVALUATE_SETTINGS,
                // ACTION_EVALUATE_TOKEN_FILES
            );
        
        for (String action : actions) {
            System.err.println ("== Help: " + action + " ==");
            genericTest (action + " --help", System.in, System.out);
        }
    }
    
    private static void testTokens () throws FileNotFoundException, InterruptedException {
        System.err.println ("== Segmenting frequency list ==");
        genericTest (
            ACTION_SEGMENTIZE_FWL
                + " --frequency-list " + DataPaths.FREQUENCY_CS_ALL2016
                + " --frequency-list-limit 50k",
            fileInput (DataPaths.FREQUENCY_CS_ALL2016),
            fileOutput (DataPaths.TEST_DIR + "/test_segmented_frequency_list." + GlobalSettings.EXTENSION_SFWL)
        );
        
        System.err.println ("== Searching for tokens ==");
        genericTest (
            ACTION_FIND_TOKENS
                + " --frequency-list-limit 50k"
                + " --max-ngram-level 3"
                + " --min-ngram-frequency 1",
            fileInput (DataPaths.TEST_DIR + "/test_segmented_frequency_list." + GlobalSettings.EXTENSION_SFWL),
            fileOutput (DataPaths.TEST_DIR + "/test_raw_tokens." + GlobalSettings.EXTENSION_TNL)
        );
        
        System.err.println ("== Tuning tokens ==");
        genericTest (
            ACTION_TUNE_TOKENS
                + " --max-ngram-level 3"
                + " --min-ngram-frequency 1"
                + " --min-ngram-productivity 2"
                + " --output-dir " + DataPaths.TEST_DIR
                + " --output-prefix test_tokens",
            fileInput (DataPaths.TEST_DIR + "/test_raw_tokens." + GlobalSettings.EXTENSION_TNL),
            System.out
        );
        
        System.err.println ("== Evaluating tokens ==");
        StringList testedFiles =
            TokenTuner.orderingFunctions ().mapToString (f ->
                DataPaths.TEST_DIR + "/test_tokens_ordered_" + f.getName () + "." + GlobalSettings.EXTENSION_TNL
            );
        genericTest (
            ACTION_EVALUATE_TOKEN_FILES
                + " --input-files " + testedFiles.join (",")
                + " --test-set cs",
            System.in,
            System.out
        );
    }
    
    private static void testWordGenerator (String inputFilePath) throws IOException {
        FunctionalList <ParametrizedNgram> ngrams = TokenTuner.readFile (inputFilePath);
        for (int i = 0; i < 10; i++) {
            StringList word = RandomWordGenerator.generateWord (ngrams);
            System.out.println (word.join () + " ... " + word.unwords ());
        }
    }
}
