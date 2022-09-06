package com.lingea.utils.io;

import com.github.josefplch.utils.data.tree.StringFrequencyTrie;
import com.github.josefplch.utils.system.BinarySerializer;
import com.lingea.segmenter.DataPaths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author  Josef Plch
 * @since   2019-11-14
 * @version 2021-01-14
 */
public class TrieSerializationTest {
    public static void main (String [] args) {
        try {
            boolean indent = false;
            
            System.out.println ("Creating the trie ...");
            StringFrequencyTrie trie = new StringFrequencyTrie ();
            trie.incrementFrequency ("hello");
            System.out.println (trie.toString (indent));
            
            System.out.println ("Serializing ...");
            byte [] bytes = BinarySerializer.serialize (trie);
            String triePath = DataPaths.localFile ("trie_NEW.trie");
            Path path = Paths.get (triePath);
            Files.write (path, bytes);
            
            System.out.println ("Deserializing A ...");
            trie = BinarySerializer.deserializeFast (triePath);
            System.out.println (trie.toString (indent));
            
            System.out.println ("Deserializing B ...");
            trie = BinarySerializer.deserializeFast (triePath);
            System.out.println (trie.toString (indent));
        }
        catch (IOException | ClassNotFoundException exception) {
            System.err.println (exception);
        }
    }
}
