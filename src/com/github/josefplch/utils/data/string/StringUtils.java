package com.github.josefplch.utils.data.string;

import com.github.josefplch.utils.data.list.StringList;
import java.text.Normalizer;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * See also: Tokenizer.
 * 
 * @author  Josef Plch
 * @since   2015-05-29
 * @version 2020-12-09
 */
public abstract class StringUtils {
    private static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile ("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

    public static StringList breakToLines (int lineLength, String text) {
        StringList result = new StringList ();
        StringList words = StringList.split ("[ \t\n]+", text.trim ());
        if (! words.isEmpty ()) {
            StringBuilder line = new StringBuilder ();
            line.append (words.get (0));
            int currentLineLength = words.get (0).length ();
            for (int i = 1; i < words.size (); i++) {
                String word = words.get (i);
                if (currentLineLength + 1 + word.length () <= lineLength) {
                    line.append (' ');
                    currentLineLength += 1;
                }
                else {
                    result.add (line.toString ());
                    line = new StringBuilder ();
                    currentLineLength = 0;
                }
                line.append (word);
                currentLineLength += word.length ();
            }
            result.add (line.toString ());
        }
        return result;
    }
    
    public static String charListToString (List <Character> characters) {
        StringBuilder stringBuilder = new StringBuilder ();
        for (Character c : characters) {
            stringBuilder.append (c);
        }
        return stringBuilder.toString ();
    }
    
    public static String horizontalLine (int width) {
        return repeat ('-', width);
    }
    
    /**
     * Levensthein distance is edit distance with following allowed operations:
     * - insert character
     * - delete character
     * - change character
     * 
     * Example:
     * levenshteinDistance ("black", "blue") = 3
     * 
     * Source: http://www.java2s.com/Code/Java/Data-Type/FindtheLevenshteindistancebetweentwoStrings.htm
     * License: Apache License, version 2.0 (in short: without warranties or conditions of any kind)
     * Retrieved: 2015-03-09
     * 
     * @param s String #1.
     * @param t String #2.
     * @return Levensthein distance between s and t.
     */
    public static int levenshteinDistance (CharSequence s, CharSequence t) {
        if (s == null || t == null) {
            throw new NullPointerException ("Strings must not be null");
        }

        int n = s.length ();
        int m = t.length ();

        if (n == 0) {
            return m;
        }
        else if (m == 0) {
            return n;
        }

        if (n > m) {
            // Swap the input strings to consume less memory.
            CharSequence tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length ();
        }

        int p [] = new int [n + 1]; // 'Previous' cost array, horizontally.
        int d [] = new int [n + 1]; // Cost array, horizontally.
        int _d []; // Placeholder to assist in swapping p and d.

        // Indexes into strings s and t.
        int i; // Iterates through s.
        int j; // Iterates through t.

        char t_j; // The j-th character of t.

        int cost;

        for (i = 0; i <= n; i++) {
            p [i] = i;
        }

        for (j = 1; j <= m; j++) {
            t_j = t.charAt (j - 1);
            d [0] = j;

            for (i = 1; i <= n; i++) {
                cost = s.charAt (i - 1) == t_j ? 0 : 1;
                // Minimum of cell to the left + 1, to the top + 1, diagonally left and up + cost.
                d [i] = Math.min (Math.min (d [i - 1] + 1, p [i] + 1),  p [i - 1] + cost);
            }
            // Copy current distance counts to 'previous row' distance counts.
            _d = p;
            p = d;
            d = _d;
        }
        // Our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts.
        return p [n];
    }
    
    public static StringList matches (String regex, CharSequence string) {
        StringList result = new StringList ();
        Matcher matcher = Pattern.compile (regex).matcher (string);
        while (matcher.find ()) {
            result.add (matcher.group ());
        }
        return result;
    }
    
    public static String modifyAll (String regex, Function <String, String> f, String string) {
        StringBuilder resultBuilder = new StringBuilder ();
        Matcher matcher = Pattern.compile (regex).matcher (string);
        int previousGroupEnd = 0;
        while (matcher.find ()) {
            // Append the not-matched text.
            resultBuilder.append (
                string.substring (
                    previousGroupEnd,
                    matcher.start ()
                )
            );
            // Append the modified matched text.
            resultBuilder.append (f.apply (matcher.group ()));
            previousGroupEnd = matcher.end ();
        }
        resultBuilder.append (string.substring (previousGroupEnd));
        return resultBuilder.toString ();
    }
    
    public static String perex (int length, String string) {
        String result;
        if (string.length () <= length) {
            result = string;
        }
        else if (length >= 10) {
            result = string.substring (0, length - 3) + "...";
        }
        else {
            result = string.substring (0, length);
        }
        return result;
    }
    
    public static String removeDiacritics (CharSequence string) {
        String normalized = Normalizer.normalize (string, Normalizer.Form.NFD);
        return (DIACRITICS_AND_FRIENDS.matcher (normalized).replaceAll (""));
    }

    public static String repeat (char character, int count) {
        StringBuilder stringBuilder = new StringBuilder ();
        for (int i = 0; i < count; i++) {
            stringBuilder.append (character);
        }
        return stringBuilder.toString ();
    }
    
    public static String repeat (CharSequence string, int count) {
        StringBuilder stringBuilder = new StringBuilder ();
        for (int i = 0; i < count; i++) {
            stringBuilder.append (string);
        }
        return stringBuilder.toString ();
    }
    
    public static String reverse (CharSequence sequence) {
        return new StringBuilder (sequence).reverse ().toString ();
    }
    
    public static String space (int width) {
        return repeat (' ', width);
    }
    
    public static String space4 (int width) {
        return space (4 * width);
    }
    
    public static String ucfirst (String string) {
        String result;
        if (string.isEmpty ()) {
            result = string;
        }
        else {
            result = string.substring (0, 1).toUpperCase () + string.substring (1);
        }
        return result;
    }
}
