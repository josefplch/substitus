package com.github.josefplch.utils.data.string;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * @author  Josef Plch
 * @since   2018-08-31
 * @version 2018-10-16
 */
public class DoubleFormatter extends DecimalFormat {
    public static final DoubleFormatter COMMA_1 = comma ("0.0");
    public static final DoubleFormatter COMMA_2 = comma ("0.00");
    public static final DoubleFormatter COMMA_3 = comma ("0.000");
    public static final DoubleFormatter COMMA_6 = comma ("0.000000");
    public static final DoubleFormatter COMMA_9 = comma ("0.000000000");
    public static final DoubleFormatter POINT_1 = point ("0.0");
    public static final DoubleFormatter POINT_2 = point ("0.00");
    public static final DoubleFormatter POINT_3 = point ("0.000");
    public static final DoubleFormatter POINT_6 = point ("0.000000");
    public static final DoubleFormatter POINT_9 = point ("0.000000000");
    
    private DoubleFormatter (char decimalSeparator, String format) {
        super (format);
        DecimalFormatSymbols decimalSymbols = DecimalFormatSymbols.getInstance ();
        decimalSymbols.setDecimalSeparator (decimalSeparator);
        super.setDecimalFormatSymbols (decimalSymbols);
    }
    
    private static DoubleFormatter comma (String format) {
        return new DoubleFormatter (',', format);
    }
    
    private static DoubleFormatter point (String format) {
        return new DoubleFormatter ('.', format);
    }
}
