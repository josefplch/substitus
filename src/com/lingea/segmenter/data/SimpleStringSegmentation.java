package com.lingea.segmenter.data;

import com.github.josefplch.utils.data.list.CharList;
import com.github.josefplch.utils.data.list.GluedList;
import com.lingea.segmenter.GlobalSettings;
import java.util.Objects;
import java.util.Optional;

/**
 * @author  Josef Plch
 * @since   2019-11-25
 * @version 2021-01-18
 */
public class SimpleStringSegmentation extends GluedList <Character, Optional <Boolean>> {
    public SimpleStringSegmentation (Character character) {
        super (character);
    }
    
    public static SimpleStringSegmentation combine (
        GluedList <Character, Optional <Boolean>> segmentationA,
        GluedList <Character, Optional <Boolean>> segmentationB
    ) {
        SimpleStringSegmentation result = new SimpleStringSegmentation (segmentationA.getElement ());
        if (! segmentationA.isLast ()) {
            Optional <Boolean> glueA = segmentationA.getNext ().get ().get1 ();
            Optional <Boolean> glueB = segmentationB.getNext ().get ().get1 ();
            GluedList <Character, Optional <Boolean>> nextA =
                segmentationA.getNext ().get ().get2 ();
            GluedList <Character, Optional <Boolean>> nextB =
                segmentationB.getNext ().get ().get2 ();
            if (Objects.equals (glueA, glueB)) {
                result.glue = glueA;
            }
            else {
                result.glue = Optional.empty ();
            }
            result.next = combine (nextA, nextB);
        }
        return result;
    }
    
    @Override
    public CharList elements () {
        return super.elements (CharList :: new);
    }
    
    public static SimpleStringSegmentation read (String string) {
        return read (string, GlobalSettings.HARD_DELIMITER, GlobalSettings.SOFT_DELIMITER);
    }
    
    public static SimpleStringSegmentation read (String string, char hardDelimiter, char softDelimiter) {
        if (string.isEmpty ()) {
            throw new IllegalArgumentException ("Cannot read an empty string.");
        }
        else {
            SimpleStringSegmentation result = new SimpleStringSegmentation (string.charAt (0));
            int i = 1;
            while (i < string.length ()) {
                char c1 = string.charAt (i);
                if (c1 == hardDelimiter || c1 == softDelimiter) {
                    i++;
                    if (i == string.length ()) {
                        throw new IllegalArgumentException ("Illegal segmentation: " + string);
                    }
                    else {
                        char c2 = string.charAt (i);
                        if (c1 == hardDelimiter) {
                            result.addLast (Optional.of (Boolean.TRUE), c2);
                        }
                        else {
                            result.addLast (Optional.empty (), c2);
                        }
                    }
                }
                else {
                    result.addLast (Optional.of (Boolean.FALSE), c1);
                }
                i++;
            }
            return result;
        }
    }
    
    @Override
    public String toString () {
        return super.toString (
            character -> character.toString (),
            category ->
                category.map (value -> value ? GlobalSettings.HARD_DELIMITER_STRING : "")
                .orElse (GlobalSettings.SOFT_DELIMITER_STRING)
        );
    }
}
