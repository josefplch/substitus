package com.lingea.segmenter.utils;

import com.github.josefplch.utils.data.list.CharList;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.string.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author  Josef Plch
 * @since   2018-07-24
 * @version 2021-01-06
 */
public abstract class SegmentationUtils {
    public static <A> FunctionalList <StringList> decompose (String string, Optional <Integer> splitLimit) {
        return (
            SegmentationUtils.decompose (CharList.fromString (string), splitLimit)
            .map (segmentation ->
                FunctionalList.from (segmentation)
                .mapToString (StringUtils :: charListToString)
            )
        );
    }
    
    /**
     * Find all possible decompositions of the given list. Example:
     * decompose
     * 
     * Approximate complexity: O (length ^ limit / limit!)
     * - limit = 0 ... O (1)
     * - limit = 1 ... O (length)
     * - limit = 2 ... O (length ^ 2)
     * Exact solution: https://oeis.org/A008949
     * 
     * @param <A>        The type of elements in the list.
     * @param list       The list to be decomposed.
     * @param splitLimit Maximum number of splits, i.e. chunks == splits + 1.
     * @return           All possible decompositions.
     */
    public static <A> FunctionalList <List <List <A>>> decompose (List <A> list, Optional <Integer> splitLimit) {
        if (list.isEmpty ()) {
            return new FunctionalList <> ();
        }
        else {
            return SegmentationUtils.decompose (list, splitLimit, 0);
        }
    }
    
    private static <A> FunctionalList <List <List <A>>> decompose (List <A> list, Optional <Integer> splitLimit, int index) {
        FunctionalList <List <List <A>>> result = new FunctionalList <> ();
        A headElement = list.get (index);
        FunctionalList <A> headSegment = FunctionalList.of (headElement);
        
        if (splitLimit.orElse (1) <= 0 || index == list.size () - 1) {
            result.add (
                FunctionalList.of (
                    list.subList (index, list.size ())
                )
            );
        }
        else {
            List <List <List <A>>> tailSegmentationsA =
                SegmentationUtils.decompose (
                    list,
                    splitLimit,
                    index + 1
                );
            
            for (List <List <A>> tailSegmentation : tailSegmentationsA) {
                List <List <A>> compactSegmentation = new ArrayList <> (tailSegmentation);
                compactSegmentation.set (
                    0,
                    FunctionalList.concat (headSegment, compactSegmentation.get (0))
                );
                result.add (compactSegmentation);
            }
            
            List <List <List <A>>> tailSegmentationsB =
                SegmentationUtils.decompose (
                    list,
                    splitLimit.map (n -> n - 1),
                    index + 1
                );
            
            for (List <List <A>> tailSegmentation : tailSegmentationsB) {
                List <List <A>> fragmentedSegmentation = new ArrayList <> ();
                fragmentedSegmentation.add (headSegment);
                fragmentedSegmentation.addAll (tailSegmentation);
                result.add (fragmentedSegmentation);
            }
        }
        return result;
    }
}
