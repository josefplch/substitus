package com.lingea.segmenter;

/**
 * @author  Josef Plch
 * @since   2019-12-04
 * @version 2019-12-05
 */
public abstract class CharPreprocessor {
    /**
     * Convert a character to lower case.
     * 
     * There is quite a problem with dotted and dotless I:
     * ASCII:   I =~ i
     * Turkish: I =~ ı, İ =~ i
     * Probably the best we can do is to convert the upper case İ to i and
     * ignore the I/ı problem.
     * 
     * It seems that for single character conversion the problem does not occur.
     * 
     * @param c The character to be converted.
     * @return  The lower case equivalent of the character, if any; otherwise,
     *          the character itself.
     */
    public static char convertToLower (char c) {
        return (c == 'İ' ? 'i' : Character.toLowerCase (c));
    }
}
