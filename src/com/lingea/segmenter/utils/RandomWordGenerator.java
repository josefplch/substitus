package com.lingea.segmenter.utils;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.tuple.Pair;
import com.lingea.segmenter.data.token.ParametrizedNgram;
import java.io.IOException;

/**
 * @author  Josef Plch
 * @since   2019-06-13
 * @version 2019-12-10
 */
public abstract class RandomWordGenerator {
    public static StringList generateWord (FunctionalList <ParametrizedNgram> ngrams) throws IOException {
        StringList word = new StringList ();
        String token = "^";
        word.add (token);
        int step = 0;
        while (! token.equals ("$") && step < 50) {
            String t = token;
            PairList <String, Number> nextTfs =
                ngrams
                .filter (ngram -> ngram.get1 ().size () == 2 && ngram.get1 ().head ().equals (t))
                .filter (ngram -> ngram.get1 ().any (x -> x.length () >= 2))
                .mapToPair (ngram -> Pair.of (
                    ngram.getNgram ().get (1),
                    // ngram.get1 ().concat ().length () // Length: too long sequences
                    ngram.getTfCount () // TF: acceptable
                    // ngram.get3 () // IDF: acceptable
                    // ngram.get4 ().doubleValue () // Reliability: bad
                    // Math.log (ngram.get2 ()) * Math.log (ngram.get3 ()) + ngram.get4 ().doubleValue ()
                ));
            
            PairList <String, Number> nextTfs2 = nextTfs.filter (pair -> pair.get1 ().length () >= 2);
            if (nextTfs2.isEmpty ()) {
                // nextTfs = nextTfs2;
            }
            
            if (nextTfs.isEmpty ()) {
                System.err.println ("No known bigram:");
                System.err.flush ();
                return word;
            }
            else {
                double randomIndex = Math.random () * nextTfs.mapToDouble (x -> x.get2 ().doubleValue ()).sum ();
                double currentSum = 0;
                for (Pair <String, Number> nextTf : nextTfs) {
                    currentSum += nextTf.get2 ().doubleValue ();
                    if (currentSum > randomIndex) {
                        token = nextTf.get1 ();
                        break;
                    }
                }
                word.add (token);
                step++;
            }
        }
        // word = word.subList (1, word.size () - 1);
        return word;
    }
}
