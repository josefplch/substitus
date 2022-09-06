package com.lingea.segmenter.bpe;

import com.github.josefplch.utils.data.list.CharList;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.string.StringUtils;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.tuple.UniformPair;
import com.github.josefplch.utils.system.TextFileUtils;
import com.lingea.segmenter.StringSegmenter;
import com.lingea.segmenter.bpe.metric.AssociationMetric;
import com.lingea.segmenter.data.BoundaryStrings;
import com.lingea.segmenter.data.token.SequenceToken;
import com.lingea.segmenter.data.token.Token;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * @author  Josef Plch
 * @since   2019-02-27
 * @version 2021-01-14
 */
public class StringBpe extends Bpe <Character> implements StringSegmenter {
    private final BoundaryStrings boundaryStrings;
    
    protected StringBpe (
        AssociationMetric metric,
        int minFrequency,
        FunctionalList <UniformPair <String>> mergedPairs,
        BoundaryStrings boundaryStrings
    ) {
        super (
            metric,
            minFrequency,
            mergedPairs.map (pair ->
                pair
                .map (CharList :: fromString)
                .map (SequenceToken :: fromAtoms)
            )
        );
        this.boundaryStrings = boundaryStrings;
    }
    
    public static StringBpe loadModel (BoundaryStrings boundaryStrings, String filePath) throws IOException {
        return (
            new StringBpe (
                null,
                0,
                TextFileUtils.readColumn (filePath, 0)
                .map (line -> {
                    String [] chunks = line.split (" ");
                    return (new UniformPair <> (chunks [0], chunks [1]));
                }),
                boundaryStrings
            )
        );
    }
    
    public static StringBpe loadTrainStrings (
        AssociationMetric metric,
        int minFrequency,
        BoundaryStrings boundaryStrings,
        Stream <Pair <String, Long>> trainData
    ) {
        StringBpe bpe = new StringBpe (metric, minFrequency, new FunctionalList <> (), boundaryStrings);
        bpe.loadTrainData (
            trainData.map (pair ->
                pair.map1 (string ->
                    CharList.fromString (boundaryStrings.wrap (string))
                )
            )
        );
        return bpe;
    }
    
    public void saveModel (String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter (new FileWriter (filePath));
        for (UniformPair <Token <Character>> mergedTokens : this.getMergedPairs ()) {
            UniformPair <String> mergedStrings =
                mergedTokens
                .map (Token :: getAtoms)
                .map (StringUtils :: charListToString);
            String token1 = mergedStrings.get1 ();
            String token2 = mergedStrings.get2 ();
            writer.write (token1 + " " + token2 + "\t" + token1 + token2);
            writer.newLine ();
        }
        writer.close ();
    }
}
