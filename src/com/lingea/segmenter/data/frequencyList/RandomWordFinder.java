package com.lingea.segmenter.data.frequencyList;

import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.nlp.Tokenizer;
import com.github.josefplch.utils.data.tuple.Pair;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author  Josef Plch
 * @since   2018-07-13
 * @version 2021-01-14
 */
public class RandomWordFinder {
    private final PairList <Long, String> frequencyList;
    private final long numberOfWords;
    
    public RandomWordFinder (String filePath, long limit) throws FileNotFoundException, IOException {
        this.frequencyList = new PairList <> ();
        
        BufferedReader reader = new BufferedReader (new FileReader (filePath));
        String line;
        long i = 0;
        long totalWords = 0;
        while (i < limit && (line = reader.readLine ()) != null) {
            FrequencyListEntry entry = FrequencyListEntry.read (line);
            if (Tokenizer.isWord (entry.getWord (), true, false, false)) {
                frequencyList.addPair (entry.getFrequency (), entry.getWord ());
                totalWords += entry.getFrequency ();
                i++;
            }
        }
        reader.close ();
        
        this.numberOfWords = totalWords;
    }
    
    public String getRandomWord () {
        long randomNumber = randomLong (1, numberOfWords);
        long i = 0;
        String result = null;
        for (Pair <Long, String> entry : frequencyList) {
            i += entry.get1 ();
            if (i >= randomNumber) {
                result = entry.get2 ();
                break;
            }
        }
        return result;
    }
    
    public static void main (String frequencyListPath, Long limit) {
        try {
            int nWords = 100;
            int maxLineLength = 80;
            RandomWordFinder finder = new RandomWordFinder (frequencyListPath, limit);
            
            int lineLength = 0;
            for (int i = 0; i < nWords; i++) {
                String word = finder.getRandomWord ();
                System.out.print (word);
                lineLength += word.length ();
                if (i < nWords - 1) {
                    System.out.print (", ");
                    lineLength += 2;
                    if (lineLength > maxLineLength) {
                        System.out.println ();
                        lineLength = 0;
                    }
                }
            }
            System.out.println ();
        }
        catch (IOException exception) {
            System.err.println ("RandomWordFinder.main: " + exception);
        }
    }
    
    private static long randomLong (long leftLimit, long rightLimit) {
        return (leftLimit + (long) (Math.random () * (rightLimit - leftLimit)));
    }
}
