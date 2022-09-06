package com.lingea.segmenter.data;

import com.github.josefplch.utils.data.list.CharList;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.string.StringUtils;
import java.util.function.BiPredicate;

/**
 * @author  Josef Plch
 * @since   2019-03-19
 * @version 2021-01-18
 */
public class ProbabilisticStringSegmentation extends ProbabilisticSegmentation <Character> {
    protected ProbabilisticStringSegmentation (Character character) {
        super (character);
    }
    
    protected ProbabilisticStringSegmentation (Character character, Double probability, ProbabilisticSegmentation <Character> next) {
        super (character, probability, next);
    }
    
    public static ProbabilisticStringSegmentation character (Character character) {
        return new ProbabilisticStringSegmentation (character);
    }
    
    public static ProbabilisticStringSegmentation readDouble (String string) throws NumberFormatException {
        return (
            ProbabilisticSegmentation.readDouble (string, ProbabilisticStringSegmentation :: readChar)
            .mapToChar (c -> c)
        );
    }
    
    // The string must contain just one character, which is the result.
    private static Character readChar (String string) {
        if (string.length () == 1) {
            return string.charAt (0);
        }
        else {
            throw new IllegalArgumentException ("Illegal character: " + string);
        }
    }
    
    public StringList binarizeS (double threshold) {
        return toStrings (super.binarize (threshold));
    }
    
    public StringList binarize50S () {
        return toStrings (super.binarize50 ());
    }
    
    public StringList binarizeIfS (BiPredicate <Integer, Double> predicate) {
        return toStrings (super.binarizeIf (predicate));
    }
    
    @Override
    public CharList elements () {
        return super.elements (CharList :: new);
    }
    
    @Override
    public ProbabilisticStringSegmentation normalize (double meanValue) {
        return super.normalize (meanValue, ProbabilisticStringSegmentation :: new);
    }
    
    public StringList sureTokensS (double lower, double upper) {
        return toStrings (super.sureTokens (lower, upper));
    }
    
    private static StringList toStrings (FunctionalList <FunctionalList <Character>> list) {
        return list.mapToString (StringUtils :: charListToString);
    }
}
