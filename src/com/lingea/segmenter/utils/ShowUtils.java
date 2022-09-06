package com.lingea.segmenter.utils;

import com.github.josefplch.utils.data.string.AlignmentUtils;
import com.github.josefplch.utils.data.string.DoubleFormatter;
import com.github.josefplch.utils.data.string.StringUtils;

/**
 * @author  Josef Plch
 * @since   2019-11-14
 * @version 2019-12-08
 */
public abstract class ShowUtils {
    private static final int COLUMN_WIDTH = 20;
    private static final boolean SHOW_TABLES_AS_HTML = false;
    
    public static String horizontalLine (int numberOfColumns) {
        return StringUtils.horizontalLine (numberOfColumns * COLUMN_WIDTH);
    }
    
    /**
     * Append spaces to reach the COLUMN_WIDTH, at least two.
     * 
     * @param string Any string.
     * @return       The string extended to column width.
     */
    public static String makeColumn (String string) {
        String result;
        if (SHOW_TABLES_AS_HTML) {
            result = "<td>" + (string.isEmpty () ? "&nbsp;" : string) + "</td>";
        }
        else {
            if (string.length () + 2 <= COLUMN_WIDTH) {
                result = AlignmentUtils.toLeft (string, COLUMN_WIDTH);
            }
            else {
                result = string + "  ";
            }
        }
        return result;
    }
    
    public static String showAsPercent (Double number) {
        return (number.isNaN () ? "NaN" : (showDouble (100 * number) + "%"));
    }
    
    public static String showDouble (Double number) {
        return (number.isNaN () ? "NaN" : DoubleFormatter.POINT_2.format (number));
    }
}
