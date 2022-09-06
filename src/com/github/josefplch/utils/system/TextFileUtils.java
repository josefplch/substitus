package com.github.josefplch.utils.system;

import com.github.josefplch.utils.data.list.CharList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.string.StringUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author  Josef Plch
 * @since   2018-05-17
 * @version 2021-01-14
 */
public class TextFileUtils {
    public static void convertLines (
        Function <String, String> lineConverter,
        String inputFilePath,
        String outputFilePath
    ) throws IOException {
        convertLinesFlat (
            lineConverter.andThen (line -> Arrays.asList (line)),
            inputFilePath,
            outputFilePath
        );
    }
    
    public static <SL extends List <String>> void convertLinesFlat (
        Function <String, SL> lineConverter,
        String inputFilePath,
        String outputFilePath
    ) throws IOException {
        BufferedReader reader = new BufferedReader (new FileReader (inputFilePath));
        BufferedWriter writer = new BufferedWriter (new FileWriter (outputFilePath));
        String line;
        while ((line = reader.readLine ()) != null) {
            for (String convertedLine : lineConverter.apply (line)) {
                writer.write (convertedLine);
                writer.newLine ();
            }
        }
        writer.close ();
        reader.close ();
    }
    
    public static StringList readColumn (String filePath, int columnIndex) throws FileNotFoundException, IOException {
        // System.out.println ("Reading column " + columnIndex + " from file " + filePath + " ...");
        return (
            TextFileUtils.readLineList (filePath)
            .mapToString (line -> {
                String [] columns = line.split ("\t");
                if (columnIndex >= columns.length) {
                    throw new IllegalArgumentException (
                        "Illegal line: " + StringUtils.perex (20, line)
                        + " ("
                        + "length: " + line.length ()
                        + ", columns: " + columns.length
                        + ")"
                    );
                }
                else {
                    return columns [columnIndex];
                }
            })
        );
    }
    
    public static CharList readCharacterList (String filePath) throws IOException {
        CharList result = new CharList ();
        BufferedReader reader = new BufferedReader (new FileReader (filePath));
        reader.lines ().forEach (line -> {
            for (Character c : line.toCharArray ()) {
                result.add (c);
            }
            result.add ('\n');
        });
        reader.close ();
        return result;
    }
    
    public static Stream <Character> readCharacterStream (String filePath) throws IOException {
        BufferedReader reader = new BufferedReader (new FileReader (filePath));
        Stream <Character> result =
            reader.lines ().flatMap (line -> {
                Stream.Builder <Character> subStream = Stream.builder ();
                for (Character c : line.toCharArray ()) {
                    subStream.accept (c);
                }
                subStream.accept ('\n');
                return subStream.build ();
            });
        reader.close ();
        return result;
    }
    
    // Equivalent to Files.readAllLines (Paths.get (filePath)).
    public static StringList readLineList (String filePath) throws IOException {
        return readLineList (new BufferedReader (new FileReader (filePath)), 0, Optional.empty ());
    }
    
    public static StringList readLineList (BufferedReader reader) throws IOException {
        return readLineList (reader, 0, Optional.empty ());
    }
    
    public static StringList readLineList (String filePath, int fromLine, Optional <Integer> limit) throws IOException {
        return readLineList (new BufferedReader (new FileReader (filePath)), fromLine, limit);
    }
    
    public static StringList readLineList (BufferedReader reader, int fromLine, Optional <Integer> limit) throws IOException {
        StringList result = new StringList ();
        String line;
        int li = 0;
        while ((line = reader.readLine ()) != null) {
            if (li >= fromLine) {
                if (limit.isPresent () && result.size () == limit.get ()) {
                    break;
                }
                result.add (line);
            }
            li++;
        }
        reader.close ();
        return result;
    }
    
    public static Stream <String> readLineStream (BufferedReader reader, Optional <Integer> limit) throws IOException {
        Stream <String> lines = reader.lines ();
        if (limit.isPresent ()) {
            lines = lines.limit (limit.get ());
        }
        return lines;
    }
    
    public static void writeLines (String outputFilePath, List <String> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter (new FileWriter (outputFilePath));
        writeLines (writer, lines);
        writer.close ();
    }
    
    public static void writeLines (Writer writer, List <String> lines) throws IOException {
        for (String line : lines) {
            try {
                writer.append (line).append ('\n');
            }
            catch (IOException exception) {
                throw new IOException ("Failed to write line: " + line, exception);
            }
        }
        writer.flush ();
    }
    
    public static void writeLines (Writer writer, Stream <String> lines) throws IOException {
        lines.forEach (line -> {
            try {
                writer.append (line).append ('\n');
            }
            catch (IOException exception) {
                throw new UncheckedIOException ("Failed to write line: " + line, exception);
            }
        });
        writer.flush ();
    }
}
