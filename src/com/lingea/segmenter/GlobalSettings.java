package com.lingea.segmenter;

/**
 * @author  Josef Plch
 * @since   2018-06-02
 * @version 2020-12-09
 */
public abstract class GlobalSettings {
    public static final String COMMENT_MARK = "#";
    public static final String EXTENSION_ARFF = "arff";
    public static final String EXTENSION_FWL  = "fwl";
    public static final String EXTENSION_MCDS = "mcds";
    public static final String EXTENSION_SFWL = "sfwl";
    public static final String EXTENSION_SDS  = "sds";
    public static final String EXTENSION_TNL  = "tnl";
    public static final char HARD_DELIMITER = ' ';
    public static final char HARD_DELIMITER_ALT = '_';
    public static final String HARD_DELIMITER_STRING = String.valueOf (HARD_DELIMITER);
    public static final char LEMMA_DELIMITER = ' ';
    public static final String LOCALE  = "cs";
    public static final char SOFT_DELIMITER = '?';
    public static final String SOFT_DELIMITER_STRING = String.valueOf (SOFT_DELIMITER);
    public static final char WORD_BOUNDARY = '@';
    public static final String WORD_CLOSING_TAG = "$";
    public static final String WORD_OPENING_TAG = "^";
}
