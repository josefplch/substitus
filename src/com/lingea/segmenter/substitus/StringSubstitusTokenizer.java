package com.lingea.segmenter.substitus;

import com.github.josefplch.utils.data.list.CharList;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.system.TextFileUtils;
import com.lingea.segmenter.GlobalSettings;
import com.lingea.segmenter.StringSegmenter;
import java.io.IOException;
import java.util.function.UnaryOperator;

/**
 * TODO: Case sensitivity.
 * 
 * @author  Josef Plch
 * @since   2019-02-28
 * @version 2021-01-14
 */
public class StringSubstitusTokenizer extends SubstitusTokenizer <Character> implements StringSegmenter {
    public StringSubstitusTokenizer (UnaryOperator <Character> characterPreprocessor, FunctionalList <StringList> knownTokenSequences) {
        super (characterPreprocessor, knownTokenSequences.map (s -> s.map (CharList :: fromString)));
    }
    
    /**
     * Create a new tokenizer based on provided token file.
     * 
     * @param characterPreprocessor
     * @param tokenFilePath A file containing token n-grams in the first column.
     * @return              A new tokenizer based on the provided file.
     * @throws IOException  If somethink goes wrong.
     */
    public static StringSubstitusTokenizer loadTokenFile (UnaryOperator <Character> characterPreprocessor, String tokenFilePath) throws IOException {
        return new StringSubstitusTokenizer (
            characterPreprocessor,
            // The tokens are separated by space.
            TextFileUtils.readColumn (tokenFilePath, 0)
            .map (tokenNgram -> StringList.split (GlobalSettings.HARD_DELIMITER_STRING, tokenNgram))
        );
    }
}
