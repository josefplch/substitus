package com.lingea.segmenter.substitus.data;

/**
 * @author  Josef Plch
 * @since   2019-12-19
 * @version 2019-12-19
 */
public class TokenizerSettings implements Comparable <TokenizerSettings> {
    private final String fileName;
    private final int maxNgramLevel;
    private final int maxNgramLength;
    private final int minNgramFrequency;
    private final int minNgramProductivity;
    private final int maxTokenLength;
    private final int minTokenFrequency;
    private final int minTokenProductivity;

    public TokenizerSettings (String fileName) {
        this.fileName = fileName;
        
        if (fileName.contains ("_n1_")) {
            maxNgramLevel = 1;
        }
        else if (fileName.contains ("_n2_")) {
            maxNgramLevel = 2;
        }
        else if (fileName.contains ("_n3_")) {
            maxNgramLevel = 3;
        }
        else {
            maxNgramLevel = -1;
        }
        
        if (fileName.contains ("_nl5_")) {
            maxNgramLength = 5;
        }
        else if (fileName.contains ("_nl6_")) {
            maxNgramLength = 6;
        }
        else if (fileName.contains ("_nl8_")) {
            maxNgramLength = 8;
        }
        else if (fileName.contains ("_nl10_")) {
            maxNgramLength = 10;
        }
        else if (fileName.contains ("_nl12_")) {
            maxNgramLength = 12;
        }
        else if (fileName.contains ("_nl14_")) {
            maxNgramLength = 14;
        }
        else {
            maxNgramLength = -1;
        }
        
        if (fileName.contains ("_nf10_")) {
            minNgramFrequency = 10;
        }
        else if (fileName.contains ("_nf100_")) {
            minNgramFrequency = 100;
        }
        else if (fileName.contains ("_nf1000_")) {
            minNgramFrequency = 1_000;
        }
        else if (fileName.contains ("_nf10000_")) {
            minNgramFrequency = 10_000;
        }
        else {
            minNgramFrequency = -1;
        }

        if (fileName.contains ("_np1_")) {
            minNgramProductivity = 1;
        }
        else if (fileName.contains ("_np3_")) {
            minNgramProductivity = 3;
        }
        else if (fileName.contains ("_np10_")) {
            minNgramProductivity = 10;
        }
        else if (fileName.contains ("_np30_")) {
            minNgramProductivity = 30;
        }
        else if (fileName.contains ("_np100_")) {
            minNgramProductivity = 100;
        }
        else {
            minNgramProductivity = -1;
        }
        
        if (fileName.contains ("_tl5_")) {
            maxTokenLength = 5;
        }
        else if (fileName.contains ("_tl6_")) {
            maxTokenLength = 6;
        }
        else if (fileName.contains ("_tl8_")) {
            maxTokenLength = 8;
        }
        else if (fileName.contains ("_tl10_")) {
            maxTokenLength = 10;
        }
        else if (fileName.contains ("_tl12_")) {
            maxTokenLength = 12;
        }
        else if (fileName.contains ("_tl14_")) {
            maxTokenLength = 14;
        }
        else {
            maxTokenLength = -1;
        }
        
        if (fileName.contains ("_tf10_")) {
            minTokenFrequency = 10;
        }
        else if (fileName.contains ("_tf100_")) {
            minTokenFrequency = 100;
        }
        else if (fileName.contains ("_tf1000_")) {
            minTokenFrequency = 1_000;
        }
        else if (fileName.contains ("_tf10000_")) {
            minTokenFrequency = 10_000;
        }
        else {
            minTokenFrequency = -1;
        }
        
        if (fileName.contains ("_tp1_")) {
            minTokenProductivity = 1;
        }
        else if (fileName.contains ("_tp2_")) {
            minTokenProductivity = 2;
        }
        else if (fileName.contains ("_tp3_")) {
            minTokenProductivity = 3;
        }
        else if (fileName.contains ("_tp4_")) {
            minTokenProductivity = 4;
        }
        else if (fileName.contains ("_tp8_")) {
            minTokenProductivity = 8;
        }
        else if (fileName.contains ("_tp10_")) {
            minTokenProductivity = 10;
        }
        else if (fileName.contains ("_tp16_")) {
            minTokenProductivity = 16;
        }
        else if (fileName.contains ("_tp30_")) {
            minTokenProductivity = 30;
        }
        else if (fileName.contains ("_tp32_")) {
            minTokenProductivity = 32;
        }
        else if (fileName.contains ("_tp100_")) {
            minTokenProductivity = 100;
        }
        else {
            minTokenProductivity = -1;
        }
    }
    
    public TokenizerSettings (
        String fileName,
        int maxNgramLevel,
        int maxNgramSize,
        int minNgramFrequency,
        int minNgramProductivity,
        int maxTokenSize,
        int minTokenFrequency,
        int minTokenProductivity
    ) {
        this.fileName = fileName;
        this.maxNgramLevel = maxNgramLevel;
        this.maxNgramLength = maxNgramSize;
        this.minNgramFrequency = minNgramFrequency;
        this.minNgramProductivity = minNgramProductivity;
        this.maxTokenLength = maxTokenSize;
        this.minTokenFrequency = minTokenFrequency;
        this.minTokenProductivity = minTokenProductivity;
    }

    @Override
    public int compareTo (TokenizerSettings other) {
        return this.fileName.compareTo (other.fileName);
    }

    public String getFileName () {
        return fileName;
    }

    public int getMaxNgramLevel () {
        return maxNgramLevel;
    }

    public int getMaxNgramLength () {
        return maxNgramLength;
    }

    public int getMinNgramFrequency () {
        return minNgramFrequency;
    }

    public int getMinNgramProductivity () {
        return minNgramProductivity;
    }

    public int getMaxTokenLength () {
        return maxTokenLength;
    }

    public int getMinTokenFrequency () {
        return minTokenFrequency;
    }

    public int getMinTokenProductivity () {
        return minTokenProductivity;
    }
    
    @Override
    public String toString () {
        return (
            TokenizerSettings.class.getSimpleName ()
            + " (N " + maxNgramLevel
            + ", NL " + maxNgramLength
            + ", NF " + minNgramFrequency
            + ", NP " + minNgramProductivity
            + ", TL " + maxTokenLength
            + ", TF " + minTokenFrequency
            + ", TP " + minTokenProductivity
            + ", file " + fileName
            + ")"
        );
    }
}
