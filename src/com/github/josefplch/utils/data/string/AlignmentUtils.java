package com.github.josefplch.utils.data.string;

/**
 * @author  Josef Plch
 * @since   2015-05-29
 * @version 2019-04-02
 */
public abstract class AlignmentUtils {
    public static String toCenter (String string, int width) {
        int blankSpaceLength = width - string.length ();
        String leftSpace = StringUtils.space (blankSpaceLength / 2);
        String rightSpace = StringUtils.space (blankSpaceLength / 2 + blankSpaceLength % 2);
        return (leftSpace + string + rightSpace);
    }
    
    public static String toLeft (String string, int width) {
        int blankSpaceLength = width - string.length ();
        String space = StringUtils.space (blankSpaceLength);
        return (string + space);
    }
    
    public static String toRight (String string, int width) {
        int blankSpaceLength = width - string.length ();
        String space = StringUtils.space (blankSpaceLength);
        return (space + string);
    }
}
