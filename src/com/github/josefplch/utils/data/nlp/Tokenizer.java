package com.github.josefplch.utils.data.nlp;

import com.github.josefplch.utils.data.list.StringList;

/**
 * See also: StringUtils.
 * 
 * @author  Josef Plch
 * @since   2020-12-23
 * @version 2021-01-07
 */
public abstract class Tokenizer {
    private static final String CLASS_CONTROL = "\\p{C}";
    private static final String CLASS_LETTER = "\\p{L}";
    private static final String CLASS_LETTER_MARK = "\\p{M}";
    private static final String CLASS_NUMERIC = "\\p{N}";
    private static final String CLASS_PUNCTUATION = "\\p{P}";
    private static final String CLASS_SEPARATOR = "\\p{Z}";
    private static final String CLASS_SYMBOL = "\\p{S}";
    
    public static StringList findTokens (String string) {
        return StringList.split ("[" + CLASS_CONTROL + CLASS_SEPARATOR + "]+", string);
    }
    
    public static StringList findWords (String string, boolean acceptConnectors, boolean acceptNumeric, boolean acceptSymbols) {
        return (
            findTokens (string)
            .mapToString (token -> trimChars (token, ! acceptConnectors, ! acceptNumeric, ! acceptSymbols))
            .filter (token -> isWord (token, acceptConnectors, acceptNumeric, acceptSymbols))
        );
    }
    
    // A word must contain at least one letter. There may be some punctuation, but not adjacent.
    public static boolean isWord (String string, boolean acceptConnectors, boolean acceptNumeric, boolean acceptSymbols) {
        final String acceptedAnywhere =
            "("
                + "[" + CLASS_LETTER + "][" + CLASS_LETTER_MARK + "]*"
                + (acceptNumeric ? "|[" + CLASS_NUMERIC + "]" : "")
                + (acceptSymbols ? "|[" + CLASS_SYMBOL  + "]" : "")
            + ")";
        final String wordPattern =
            "^"
            + (acceptConnectors ? "[’']?" : "")
            + acceptedAnywhere + "+"
            + "("
                + (acceptConnectors ? "[’'-]?" : "")
                + acceptedAnywhere + "+"
            + ")*"
            + (acceptConnectors ? "[’']?" : "")
            + "$";
        return string.matches (wordPattern);
    }
    
    public static String stripHtmlTags (String string) {
        return string.replaceAll ("[<][^>]*[>]", " ");
    }
    
    // Caution: The result may be empty.
    public static String trimChars (String string, boolean trimConnectors, boolean trimNumeric, boolean trimSymbols) {
        final String nonWordChar =
            "["
            + CLASS_CONTROL
            + (trimNumeric ? CLASS_NUMERIC : "")
            + CLASS_PUNCTUATION
            + CLASS_SEPARATOR
            + (trimSymbols ? CLASS_SYMBOL : "")
            + (trimConnectors ? "" : "&&[^’']")
            + "]";
        
        return (
            string
            .replaceFirst ("^" + nonWordChar + "+", "")
            .replaceFirst (nonWordChar + "+$", "")
        );
    }
}
