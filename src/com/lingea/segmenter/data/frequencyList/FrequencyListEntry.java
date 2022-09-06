package com.lingea.segmenter.data.frequencyList;

import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.set.StringSet;
import com.github.josefplch.utils.data.tuple.Tuple3;
import com.lingea.segmenter.GlobalSettings;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * @author  Josef Plch
 * @since   2020-12-11
 * @version 2020-12-11
 */
public class FrequencyListEntry extends Tuple3 <Long, String, StringSet> implements Comparable <FrequencyListEntry> {
    private static final Comparator <Tuple3 <Long, String, StringSet>> COMPARATOR =
        Tuple3.LexicographicalComparator.basedOn (
            Comparator.reverseOrder (),
            Collator.getInstance (new Locale (GlobalSettings.LOCALE)),
            (x, y) -> 0
        );
    
    public FrequencyListEntry (Long frequency, String word, StringSet lemmata) {
        super (frequency, word, lemmata);
    }
    
    public static FrequencyListEntry read (String line) throws IllegalArgumentException {
        // Line format: frequency [ws] word [ws] lemma1 [ws] ... [ws] lemmaN where [ws] = white space
        StringList columns = StringList.split ("(\t| +)", line);
        if (columns.size () < 2) {
            throw new IllegalArgumentException ("Illegal line: " + line);
        }
        else {
            return (
                new FrequencyListEntry (
                    Long.parseLong (columns.get (0)),
                    columns.get (1),
                    columns.drop (2).toSet ()
                )
            );
        }
    }
    
    @Override
    public int compareTo (FrequencyListEntry other) {
        return COMPARATOR.compare (this, other);
    }
    
    public Long getFrequency () {
        return e1;
    }
    
    public StringSet getLemmata () {
        return e3;
    }
    
    public StringList getLemmataAsc () {
        return e3.toList ().sortAsc ();
    }
    
    public String getWord () {
        return e2;
    }
}
