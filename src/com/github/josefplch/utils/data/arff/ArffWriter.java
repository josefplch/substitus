package com.github.josefplch.utils.data.arff;

import com.github.josefplch.utils.data.string.DoubleFormatter;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.StringList;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Attribute relation file format (ARFF) is format used by Weka.
 * 
 * @author  Josef Plch
 * @since   2018-07-09
 * @version 2021-07-27
 */
public class ArffWriter implements Closeable, Flushable {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat ("yyyy-MM-dd");
    private final FunctionalList <ArffAttribute> attributes;
    private final Writer delegate;
    
    public ArffWriter (Writer writer, List <ArffAttribute> attributes) {
        this.attributes = FunctionalList.from (attributes);
        this.delegate = writer;
    }
    
    @Override
    public void close () throws IOException {
        delegate.close ();
    }

    @Override
    public void flush () throws IOException {
        delegate.flush ();
    }
    
    private static String serializeInstance (List <Object> instance, int weight) {
        return (
            String.join (
                ",\t",
                FunctionalList.from (instance).map (ArffWriter :: showValue)
            )
            + (weight == 0 ? "" : (",\t{" + weight + "}"))
        );
    }
    
    private static String showValue (Object value) {
        String result;
        if (value instanceof Double) {
            result = DoubleFormatter.POINT_6.format (value);
        }
        else if (value instanceof String) {
            String string = (String) value;
            result = "\"" + string.replace ("\"", "\\\"") + "\"";
        }
        else {
            result = Objects.toString (value);
        }
        return result;
    }
    
    public void writeHead (String relationName, StringList comment) throws IOException {
        String today = DATE_FORMATTER.format (new Date ());
        String head =
            comment.mapToString (line -> "% " + line).unlines ()
            + (comment.isEmpty () ? "" : "\n% ")
            + "\n% @version " + today
            + "\n"
            + "\n@relation " + relationName
            + "\n"
            + "\n" + attributes.mapToString (ArffAttribute :: serialize).unlines ()
            + "\n"
            + "\n@data";
        
        delegate.append (head + "\n");
        delegate.flush ();
    }
    
    public void writeInstance (List <Object> instance) throws IOException {
        this.writeInstance (instance, 1);
    }
    
    // Weight is supported since Weka 3.5.8.
    public void writeInstance (List <Object> instance, int weight) throws IOException {
        if (instance.size () != attributes.size ()) {
            throw new IllegalArgumentException (
                "Illegal instance: Trying to add instance with "
                + instance.size () + " attributes to model with "
                + attributes.size () + " attriuttes: " + instance
            );
        }
        else if (weight < 1) {
            throw new IllegalArgumentException ("The instance weight must be positive.");
        }
        else {
            delegate.append (serializeInstance (instance, weight) + "\n");
            delegate.flush ();
        }
    }
}
