package com.lingea.segmenter.data.token;

import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.math.fraction.LongFraction;
import com.github.josefplch.utils.data.string.DoubleFormatter;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.tuple.UniformPair;
import com.lingea.segmenter.GlobalSettings;

/**
 * Token n-gram with some characteristics.
 * 
 * @author  Josef Plch
 * @since   2019-06-14
 * @version 2021-01-12
 */
public class ParametrizedNgram extends Pair <StringList, NgramPropertiesB> {
    public static final String EXAMPLES_DELIMITER = " ";
    public static final String EXAMPLES_NEGATIVE = "NEG: ";
    public static final String EXAMPLES_POSITIVE = "POS: ";
    
    public ParametrizedNgram (StringList ngram, NgramPropertiesB properties) {
        super (ngram, properties);
    }
    
    public UniformPair <StringList> getExamples () {
        return e2.getExamples ();
    }
    
    public Double getFixedIwfRatio () {
        return e2.getFixedIwfRatio ();
    }
    
    public Double getFixedTfRatio () {
        return e2.getFixedTfRatio ();
    }
    
    public LongFraction getIwf () {
        return e2.getIwf ();
    }
    
    public Long getIwfCount () {
        return e2.getIwfCount ();
    }
    
    public Double getIwfRatio () {
        return e2.getIwfRatio ();
    }
    
    public long getLength () {
        return e1.lengthSum ();
    }
    
    public StringList getNgram () {
        return e1;
    }
    
    public NgramPropertiesB getProperties () {
        return e2;
    }
    
    public LongFraction getTf () {
        return e2.getTf ();
    }
    
    public Long getTfCount () {
        return e2.getTfCount ();
    }
    
    public Double getTfRatio () {
        return e2.getTfRatio ();
    }
    
    public static ParametrizedNgram parse (String string) {
        StringList columns = StringList.split ("\t", string);
        return new ParametrizedNgram (
            StringList.split (GlobalSettings.HARD_DELIMITER_STRING, columns.get (0)),
            new NgramPropertiesB (
                // Ignore column 1, it is n-gram length.
                LongFraction.valueOf (columns.get (2)),
                // Ignore column 3, it is a ratio.
                LongFraction.valueOf (columns.get (4)),
                // Ignore column 5, it is a ratio.
                new UniformPair <> (
                    StringList.split (
                        ParametrizedNgram.EXAMPLES_DELIMITER,
                        columns.get (6).substring (ParametrizedNgram.EXAMPLES_POSITIVE.length ())
                    ),
                    StringList.split (
                        ParametrizedNgram.EXAMPLES_DELIMITER,
                        columns.get (7).substring (ParametrizedNgram.EXAMPLES_NEGATIVE.length ())
                    )
                )
            )
        );
    }
    
    public String serialize () {
        return (
            e1.unwords ()
            + "\t" + this.getLength ()
            + "\t" + this.getTf ()
            + "\t" + DoubleFormatter.POINT_3.format (this.getTfRatio ())
            + "\t" + this.getIwf ()
            + "\t" + DoubleFormatter.POINT_3.format (this.getIwfRatio ())
            + "\t" + EXAMPLES_POSITIVE + this.getExamples ().get1 ().join (EXAMPLES_DELIMITER)
            + "\t" + EXAMPLES_NEGATIVE + this.getExamples ().get2 ().join (EXAMPLES_DELIMITER)
        );
    }
}
