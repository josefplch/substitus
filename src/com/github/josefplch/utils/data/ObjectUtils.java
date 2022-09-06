package com.github.josefplch.utils.data;

/**
 * @author  Josef Plch
 * @since   2015-04-18
 * @version 2018-05-18
 */
public abstract class ObjectUtils {
    public static String show (Object object) {
        String result;
        if (object == null) {
            result = "null";
        }
        else if (object instanceof Character) {
            result = "'" + object + "'";
        }
        else if (object instanceof CharSequence) {
            result = "\"" + object + "\"";
        }
        else if (object instanceof Iterable) {
            StringBuilder resultBuilder = new StringBuilder ();
            resultBuilder.append ('[');
            boolean first = true;
            for (Object element : (Iterable) object) {
                if (first) {
                    first = false;
                }
                else {
                    resultBuilder.append (", ");
                }
                resultBuilder.append (show (element));
            }
            resultBuilder.append (']');
            result = resultBuilder.toString ();
        }
        else {
            result = object.toString ();
        }
        return result;
    }
}
