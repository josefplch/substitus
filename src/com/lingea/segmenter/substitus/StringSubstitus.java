package com.lingea.segmenter.substitus;

import com.github.josefplch.utils.data.list.CharList;
import com.github.josefplch.utils.data.tuple.Pair;
import com.lingea.segmenter.ProbabilisticStringSegmenter;
import com.lingea.segmenter.data.ProbabilisticStringSegmentation;
import com.lingea.segmenter.data.SimpleStringSegmentation;
import com.lingea.segmenter.data.frequencyList.FrequencyListEntry;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Substitus specialized in strings. There is some extra preprocessing.
 * 
 * @author  Josef Plch
 * @since   2018-05-18
 * @version 2020-12-11
 */
public class StringSubstitus extends Substitus <Character> implements ProbabilisticStringSegmenter {
    public StringSubstitus (
        UnaryOperator <Character> characterPreprocessor,
        Writer outputWriter,
        Optional <Pair <Writer, Boolean>> arffSetting,
        int verbosity
    ) throws IOException {
        super (characterPreprocessor, outputWriter, arffSetting, verbosity);
    }
    
    public static StringSubstitus train (
        UnaryOperator <Character> characterPreprocessor,
        BufferedReader frequencyListReader,
        Optional <Integer> frequencyListLengthLimit,
        Writer outputWriter,
        Optional <Pair <Writer, Boolean>> arffSetting,
        int verbosity
    ) throws IOException {
        StringSubstitus result = new StringSubstitus (characterPreprocessor, outputWriter, arffSetting, verbosity);
        String line;
        while ((line = frequencyListReader.readLine ()) != null) {
            if (frequencyListLengthLimit.isPresent () && result.uniqueCompoundsCount () >= frequencyListLengthLimit.get ()) {
                break;
            }
            FrequencyListEntry entry = FrequencyListEntry.read (line);
            result.rememberCounted (entry.getWord (), entry.getFrequency ());
        }
        return result;
    }
    
    public void rememberCounted (String compound, Long frequency) {
        super.rememberCounted (CharList.fromString (compound), frequency);
    }
    
    @Override
    public ProbabilisticStringSegmentation segmentizeAndPrintArff (
        List <Character> string,
        Optional <SimpleStringSegmentation> correctSegmentation
    ) throws IOException {
        return super.segmentizeAndPrintArff (string, correctSegmentation).mapToChar (c -> c);
    }
    
    public ProbabilisticStringSegmentation segmentizeAndPrintArff (
        String string,
        Optional <SimpleStringSegmentation> correctSegmentation
    ) throws IOException {
        return this.segmentizeAndPrintArff (CharList.fromString (string), correctSegmentation);
    }
    
    @Override
    public ProbabilisticStringSegmentation segmentizeP (List <Character> compound) {
        return super.segmentizeP (compound).mapToChar (c -> c);
    }
    
    @Override
    public ProbabilisticStringSegmentation segmentizeP (String compound) {
        return this.segmentizeP (CharList.fromString (compound));
    }
}
