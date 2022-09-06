package com.lingea.segmenter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author  Josef Plch
 * @since   2018-05-24
 * @version 2019-11-27
 */
public abstract class DataPaths {
    // public static final String DATA_DIR = findDataDir ();
    public static final String DATA_DIR = "/media/jozka/Data/DATA/Programy/java/Substitus/data";
    public static final String BPE_MODEL_DIR      = DATA_DIR + "/bpe_models";
    public static final String DATA_SET_DIR       = DATA_DIR + "/data_sets";
    public static final String FREQUENCY_LIST_DIR = DATA_DIR + "/frequency_lists";
    public static final String RAW_TOKENS_DIR     = DATA_DIR + "/raw_tokens";
    public static final String TUNED_TOKENS_DIR   = DATA_DIR + "/tuned_tokens";
    public static final String TEST_DIR           = DATA_DIR + "/test_files";
    
    public static final String FREQUENCY_CS_ALL2016 = FREQUENCY_LIST_DIR + "/cs_lingeaAll2016_lc_5m." + GlobalSettings.EXTENSION_FWL;
    public static final String FREQUENCY_CS_ALL2020 = FREQUENCY_LIST_DIR + "/cs_lingeaAll2020_lc_5m." + GlobalSettings.EXTENSION_FWL;
    public static final String FREQUENCY_CS_WIKI    = FREQUENCY_LIST_DIR + "/cs_lingeaWiki_ci." + GlobalSettings.EXTENSION_FWL;
    public static final String FREQUENCY_DE_ALL     = FREQUENCY_LIST_DIR + "/de_lingeaAll_lc_5m." + GlobalSettings.EXTENSION_FWL;
    public static final String FREQUENCY_EN_ALL     = FREQUENCY_LIST_DIR + "/en_lingeaAll_lc_5m." + GlobalSettings.EXTENSION_FWL;
    public static final String FREQUENCY_EN_MC2010  = FREQUENCY_LIST_DIR + "/en_mc2010_lc_870k." + GlobalSettings.EXTENSION_FWL;
    public static final String FREQUENCY_EN_WIKI    = FREQUENCY_LIST_DIR + "/en_lingeaWiki_lc_5m." + GlobalSettings.EXTENSION_FWL;
    public static final String FREQUENCY_HU_ALL     = FREQUENCY_LIST_DIR + "/hu_lingeaAll_lc_5m." + GlobalSettings.EXTENSION_FWL;
    public static final String FREQUENCY_FI_MC2010  = FREQUENCY_LIST_DIR + "/fi_mc2010_lc." + GlobalSettings.EXTENSION_FWL;
    public static final String FREQUENCY_SK_ALL     = FREQUENCY_LIST_DIR + "/sk_lingeaAllSum2016_lc_5m." + GlobalSettings.EXTENSION_FWL;
    public static final String FREQUENCY_TR_MC2010  = FREQUENCY_LIST_DIR + "/tr_mc2010_specialCase." + GlobalSettings.EXTENSION_FWL;
    
    public static final String TEST_SET_CS_PLCH   = DATA_SET_DIR + "/cs_plch." + GlobalSettings.EXTENSION_SDS;
    public static final String TEST_SET_EN_MC2010 = DATA_SET_DIR + "/en_mc2010gold_devtrain." + GlobalSettings.EXTENSION_MCDS;
    public static final String TEST_SET_EN_PLCH   = DATA_SET_DIR + "/en_plch." + GlobalSettings.EXTENSION_SDS;
    public static final String TEST_SET_FI_MC2010 = DATA_SET_DIR + "/fi_mc2010gold_train." + GlobalSettings.EXTENSION_MCDS;
    public static final String TEST_SET_HU_PLCH   = DATA_SET_DIR + "/hu_plch." + GlobalSettings.EXTENSION_SDS;
    public static final String TEST_SET_TR_MC2010 = DATA_SET_DIR + "/tr_mc2010gold_devtrain." + GlobalSettings.EXTENSION_MCDS;
    
    private static String findDataDir () {
        Path workingDirectory = Paths.get (System.getProperty ("user.dir"));
        Path dataDirectory = Paths.get (workingDirectory.getParent () + "/data");
        if (! Files.exists (dataDirectory)) {
            System.err.println (
                "Warning: The data directory was not found. It is expected to be"
                + " located at <working-directory>/../data, i.e. " + dataDirectory
            );
            return "[DATA_DIRECTORY_NOT_FOUND]";
        }
        else {
            return dataDirectory.toString ().replace ("\\", "/");
        }
    }
    
    public static String tunedTokenFile (String name) {
        return (TUNED_TOKENS_DIR + "/" + name);
    }
    
    public static String localFile (String localPath) {
        return (DATA_DIR + "/" + localPath);
    }
}
