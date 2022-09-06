package com.lingea.segmenter.substitus;

import com.github.josefplch.utils.data.list.CharList;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.string.AlignmentUtils;
import com.github.josefplch.utils.data.string.DoubleFormatter;
import com.github.josefplch.utils.data.string.StringUtils;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.system.TextFileUtils;
import com.lingea.segmenter.data.token.ParametrizedNgram;
import com.lingea.segmenter.GlobalSettings;
import com.lingea.segmenter.StringSegmenter;
import com.lingea.segmenter.utils.ShowUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Segmenter based on dynamic programming.
 * 
 * @author  Josef Plch
 * @since   2019-06-19
 * @version 2021-01-14
 */
public class StringViterbiTokenizer implements StringSegmenter {
    private static final int VERBOSE = 0;
    private final Map <StringList, ParametrizedNgram> ngrams;
    
    public StringViterbiTokenizer (FunctionalList <ParametrizedNgram> ngrams) {
        this.ngrams =
            ngrams
            .filter (ngram -> ngram.getIwf ().getNumerator () >= 3)
            .mapToPair (ngram -> Pair.of (ngram.getNgram (), ngram))
            .toMap ();
    }
    
    public static StringViterbiTokenizer load (String filePath) throws IOException {
        return (
            new StringViterbiTokenizer (
                TextFileUtils.readLineList (filePath)
                .map (ParametrizedNgram :: parse)
            )
        );
    }
    
    public static void main (String tokenFilePath) {
        try {
            System.out.println ("Loading ...");
            StringViterbiTokenizer segmenter = StringViterbiTokenizer.load (tokenFilePath);
            System.out.println (segmenter.segmentize ("koval"));
            System.out.println (segmenter.segmentize ("dovykoval"));
            System.out.println (segmenter.segmentize ("povykoval"));
            System.out.println (segmenter.segmentize ("nákladnou"));
            System.out.println (segmenter.segmentize ("roztřídíme"));
            System.out.println (segmenter.segmentize ("turbodmychadlo"));
            System.out.println (segmenter.segmentize ("nejneobhospodářovávatelnějšími"));
            System.out.println (segmenter.segmentize ("dorozpronajímat"));
            System.out.println (segmenter.segmentize ("www.seznam.cz"));
            System.out.println (segmenter.segmentize ("www.petrolejovyprinc.cz"));
        }
        catch (IOException exception) {
            System.err.println (exception);
        }
    }
    
    @Override
    public List <List <Character>> segmentize (List <Character> list) {
        LinkedList <String> tokens = new LinkedList <> ();
        this.segmentize (StringUtils.charListToString (list));
        System.out.println ("Tokens: " + tokens);
        return StringList.from (tokens).map (token -> CharList.fromString (token));
    }
    
    // 00 > {11, 12, 13}, 01 > {22, 23}, 02 > {33},
    // 11 > {22, 23}, 12 > {33},
    // 22 > {33}
    @Override
    public StringList segmentize (String string) {
        Map <Pair <Integer, Integer>, Pair <Double, Integer>> table = new HashMap <> ();
        
        // Fill the dynamic table.
        for (int end = string.length () - 1; end >= 0; end--) {
            for (int start = end; start >= 0; start--) {
                String token = string.substring (start, end + 1);
                if (VERBOSE >= 3) {
                    System.out.println ("Indices: (" + start + ", " + end + "): " + token);
                }
                Double probability;
                Pair <Double, Integer> bestSubresult = Pair.of (Double.NEGATIVE_INFINITY, -1);
                if (end == string.length () - 1) {
                    probability = Math.log (transitionProbability (token, GlobalSettings.WORD_CLOSING_TAG));
                    if (VERBOSE >= 3) {
                        System.out.println ("Direct: " + probability);
                    }
                    bestSubresult.set1 (probability);
                }
                else {
                    for (int nextEnd = string.length () - 1; nextEnd > end; nextEnd--) {
                        String nextToken = string.substring (end + 1, nextEnd + 1);
                        Pair <Double, Integer> next = table.get (Pair.of (end + 1, nextEnd));
                        probability =
                            Math.log (transitionProbability (token, nextToken))
                            + next.get1 ()
                            ;
                        if (VERBOSE >= 3) {
                            System.out.println (
                                "Indices: (" + start + ", " + end + ")"
                                + " -> (" + end + ", " + nextEnd + "): " + nextToken
                                + ", probability: " + probability + ", translation: " + transitionProbability (token, nextToken)
                            );
                        }
                        if (probability > bestSubresult.get1 ()) {
                            bestSubresult = Pair.of (probability, nextEnd);
                        }
                    }
                }
                table.put (Pair.of (start, end), bestSubresult);
            }
        }
        
        // Print the table.
        if (VERBOSE >= 2) {
            System.out.println ("Table size: " + table.size ());
            
            // Header.
            System.out.print (AlignmentUtils.toLeft ("", 8));
            for (int hi = 0; hi < string.length (); hi++) {
                System.out.print (
                    AlignmentUtils.toLeft (
                        "#" + hi + " " + string.substring (hi, hi + 1),
                        30
                    )
                );
            }
            System.out.println ();
            
            // Body.
            for (int vi = 0; vi < string.length (); vi++) {
                System.out.print (
                    AlignmentUtils.toLeft (
                        "#" + vi + " " + string.substring (vi, vi + 1),
                        8
                    )
                );
                for (int hi = 0; hi < string.length (); hi++) {
                    Pair <Double, Integer> cell = table.get (Pair.of (vi, hi));
                    System.out.print (
                        AlignmentUtils.toLeft (
                            Optional.ofNullable (cell)
                            .map (pair -> pair.map1 (p -> DoubleFormatter.POINT_6.format (100 * p))
                            .toString ())
                            .orElse ("--"),
                            30
                        )
                    );
                }
                System.out.println ();
            }
            System.out.println ();
        }
        
        // Compute best segmentation.
        int start = 0;
        int end = 0;
        double maxProbability = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < string.length (); i++) {
            String prefix = string.substring (0, i + 1);
            double p =
                Math.log (transitionProbability (GlobalSettings.WORD_OPENING_TAG, prefix))
                + table.get (Pair.of (0, i)).get1 ();
            if (VERBOSE >= 2) {
                System.out.println ("Prefix " + prefix + ": " + p);
            }
            if (p > maxProbability) {
                maxProbability = p;
                end = i;
            }
        }
        System.out.println (ShowUtils.showAsPercent (Math.exp (maxProbability)));
        
        StringList result = new StringList ();
        while (end != -1) {
            Pair <Double, Integer> pair = table.get (Pair.of (start, end));
            String token = string.substring (start, end + 1);
            result.add (token);
            if (VERBOSE >= 1) {
                System.out.println (
                    "(" + start + ", " + end + "): " + token
                    + " (" + ShowUtils.showAsPercent (Math.exp (pair.get1 ())) + ")"
                );
            }
            start = end + 1;
            end = pair.get2 ();
        }
        return result;
    }
    
    private Double transitionProbability (String tokenA, String tokenB) {
        StringList bigram = StringList.ofStrings (tokenA, tokenB);
        return (
            Optional.ofNullable (
                ngrams.get (
                    ngrams.containsKey (bigram)
                    ? bigram
                    : StringList.ofStrings (tokenA)
                )
            )
            .map (ParametrizedNgram :: getTfRatio)
            // We move backwards, so we must use tokenA.
            .orElse (tokenA.length () == 1 ? 0.001 : 0)
        );
    }
}
