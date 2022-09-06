package com.lingea.segmenter;

import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.list.number.IntegerList;
import com.lingea.segmenter.utils.SegmentationUtils;
import static com.lingea.segmenter.utils.SegmentationUtils.decompose;
import java.util.Optional;

/**
 * @author  Josef Plch
 * @since   2019-11-21
 * @version 2019-11-21
 */
public class SegmentationUtilsTest {
    // Test of effectivity.
    public static void main (String [] args) {
        String word = "0123456789abcdefghij";
        
        System.out.println (decompose (word, Optional.of (2)).take (10).mapToString ().unlines ());
        System.out.println ();
        
        IntegerList lengths = IntegerList.interval (1, word.length ());
        
        System.out.println ("\t" + lengths.mapToString (n -> "len=" + n).join ("\t"));
        // Measure the (space) complexity.
        for (int lim = 0; lim < word.length (); lim++) {
            System.out.print ("lim=" + lim);
            for (int len : lengths) {
                FunctionalList <StringList> decompositions =
                    SegmentationUtils.decompose (
                        word.substring (0, len),
                        Optional.of (lim)
                    );
                System.out.print ("\t" + decompositions.size ());
            }
            System.out.println ();
        }
    }
}
