package com.github.josefplch.utils.system;

import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.tuple.Pair;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A helper class for more comfortable access to command line arguments.
 * 
 * @author  Josef Plch
 * @since   2019-03-28
 * @version 2020-12-11
 */
public class Args {
    private final StringList args;
    
    public Args (String [] args) {
        this.args = StringList.ofStrings (args);
    }
    
    /**
     * Do the arguments contain the given one? Useful for switchers, e.g. --verbose.
     * 
     * @param key Name of the attribute, without the "-" or "--" prefix.
     * @return    True if arguments contain the given key.
     */
    public boolean contains (String key) {
        return args.any (arg -> Objects.equals (getName (arg), Optional.of (key)));
    }
    
    public Optional <Double> getDouble (String key) {
        return this.read (key, Double :: parseDouble);
    }
    
    public Double getDouble (String key, String errorMessage) {
        return this.read (key, Double :: parseDouble, errorMessage);
    }
    
    public Optional <Float> getFloat (String key) {
        return this.read (key, Float :: parseFloat);
    }
    
    public Float getFloat (String key, String errorMessage) {
        return this.read (key, Float :: parseFloat, errorMessage);
    }
    
    private static Optional <String> getName (String arg) {
        return (
            ! isKey (arg)
            ? Optional.empty ()
            : Optional.of (arg.substring (arg.startsWith ("--") ? 2 : 1))
        );
    }
    
    public Optional <Integer> getInteger (String key) {
        return this.read (key, Args :: readInteger);
    }
    
    public Integer getInteger (String key, String errorMessage) {
        return this.read (key, Args :: readInteger, errorMessage);
    }
    
    public Optional <Long> getLong (String key) {
        return this.read (key, Args :: readLong);
    }
    
    public Long getLong (String key, String errorMessage) {
        return this.read (key, Args :: readLong, errorMessage);
    }
    
    public Optional <String> getString (String key) {
        return this.read (key, Function.identity ());
    }
    
    public String getString (String key, String errorMessage) throws NoSuchElementException {
        return this.read (key, Function.identity (), errorMessage);
    }
    
    private static boolean isKey (String arg) {
        return arg.startsWith ("-");
    }
    
    private static Pair <String, Integer> preprocessNumber (String string) {
        Integer base;
        String number;
        if (string.endsWith ("k")) {
            base = 1_000;
            number = string.substring (0, string.length () - 1);
        }
        else if (string.endsWith ("M")) {
            base = 1_000_000;
            number = string.substring (0, string.length () - 1);
        }
        else if (string.endsWith ("G")) {
            base = 1_000_000_000;
            number = string.substring (0, string.length () - 1);
        }
        else {
            base = 1;
            number = string;
        }
        return Pair.of (number, base);
    }
    
    /**
     * Get value of the specified attribute.
     * 
     * Examples:
     * Args ("--width", "8").get ("height", Integer :: parseInt) == Nothing.
     * Args ("--width", "8").get ("width",  Integer :: parseInt) == Just (8).
     * 
     * @param <T>    Type of the attribute value.
     * @param key    Name of the attribute, without the "-" or "--" prefix.
     * @param reader The function to read the value.
     * @return       Value of the attribute, i.e. the attribute right to the key.
     */
    public <T> Optional <T> read (String key, Function <String, T> reader) throws NumberFormatException {
        Optional <T> result = Optional.empty ();
        for (int i = 0; i < args.size () - 1; i++) {
            if (Objects.equals (getName (args.get (i)), Optional.of (key))) {
                String nextArg = args.get (i + 1);
                if (! isKey (nextArg)) {
                    try {
                        result = Optional.of (reader.apply (nextArg));
                    }
                    catch (NumberFormatException exception) {
                        throw new NumberFormatException ("Invalid " + key + ": " + nextArg);
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Get value of the specified attribute, throw an exception if not found.
     * 
     * @param <T>          Type of the attribute value.
     * @param key          Name of the attribute, without the "-" or "--" prefix.
     * @param reader       The function to read the value.
     * @param errorMessage Error message thrown if the key is not found.
     * @return             Value of the attribute, i.e. the attribute right to the key.
     * @throws NoSuchElementException If the key is not found.
     */
    public <T> T read (String key, Function <String, T> reader, String errorMessage) throws NoSuchElementException {
        return (
            this.read (key, reader)
            .orElseThrow (() -> new NoSuchElementException (errorMessage))
        );
    }
    
    private static Integer readInteger (String string) {
        Pair <String, Integer> pair = preprocessNumber (string);
        return (Integer.parseInt (pair.get1 ()) * pair.get2 ());
    }
    
    private static Long readLong (String string) {
        Pair <String, Integer> pair = preprocessNumber (string);
        return (Long.parseLong (pair.get1 ()) * pair.get2 ());
    }
}
