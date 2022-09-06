package com.github.josefplch.utils.data.arff;

import com.github.josefplch.utils.data.tuple.Pair;
import java.util.Arrays;
import java.util.List;

/**
 * @author  Josef Plch
 * @since   2018-07-09
 * @version 2019-03-26
 */
public class ArffAttribute extends Pair <String, String> {
    private ArffAttribute (String name, String type) {
        super (name, type);
    }
    
    // A special case of enumeration.
    public static ArffAttribute bool (String name) {
        // The order matters because of colours: blue, red.
        return enumeration (name, Arrays.asList ("true", "false"));
    }
    
    public static ArffAttribute enumeration (String name, String ... values) {
        return enumeration (name, Arrays.asList (values));
    }
    
    public static ArffAttribute enumeration (String name, List <String> values) {
        return (
            new ArffAttribute (
                name,
                "{" + String.join (", ", values) + "}"
            )
        );
    }
    
    public static ArffAttribute numeric (String name) {
        return (new ArffAttribute (name, "numeric"));
    }
    
    public static ArffAttribute string (String name) {
        return (new ArffAttribute (name, "string"));
    }

    public String serialize () {
        return ("@attribute " + this.e1 + " " + this.e2);
    }
}
