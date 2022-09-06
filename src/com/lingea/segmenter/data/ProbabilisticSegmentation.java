package com.lingea.segmenter.data;

import com.github.josefplch.utils.data.list.GluedList;
import com.github.josefplch.utils.data.function.Function3;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.number.DoubleList;
import com.github.josefplch.utils.data.string.DoubleFormatter;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.tuple.UniformPair;
import com.lingea.segmenter.GlobalSettings;
import com.lingea.segmenter.substitus.Substitus;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Probabilistic segmentation:
 * ['h' 0.05 'a' 0.23 'v' 0.87 'e']
 * 
 * @param <A> Type of the basic, atomic element (typically: byte or character).
 * 
 * @author  Josef Plch
 * @since   2019-03-19
 * @version 2021-01-18
 */
public class ProbabilisticSegmentation <A> extends GluedList <A, Double> {
    protected static final String GLUE_DELIMITER = " ";
    
    protected ProbabilisticSegmentation (A atom) {
        super (atom);
    }
    
    protected ProbabilisticSegmentation (A atom, Double probability, ProbabilisticSegmentation <A> next) {
        super (atom, probability, next);
    }
    
    public static <A> ProbabilisticSegmentation <A> atom (A atom) {
        return new ProbabilisticSegmentation <> (atom);
    }
    
    // Amount of chaos:
    // 
    // entropy
    // 1 ^  /\
    //   | /  \
    //   |/    \
    // 0 --------> probability
    //   0      1
    public DoubleList entropy () {
        return this.glues ().mapToDouble (p -> 1 - Math.abs (2 * p - 1));
    }
    
    public ProbabilisticSegmentation <A> normalize (double meanValue) {
        return this.normalize (meanValue, ProbabilisticSegmentation :: new);
    }
    
    protected <S extends ProbabilisticSegmentation <A>> S normalize (double meanValue, Function3 <A, Double, S, S> constructor) {
        return (
            super.bimap (
                a -> a,
                p -> Substitus.normalize (p, meanValue),
                constructor
            )
        );
    }
    
    // Read the serialized segmentation. TODO: Non-recursive version.
    protected static <A> ProbabilisticSegmentation <A> readDouble (String string, Function <String, A> atomReader) throws NumberFormatException {
        // Chunks: (first atom, probability, tail of the list).
        String [] chunks = string.split (GLUE_DELIMITER, 3);
        ProbabilisticSegmentation <A> result =
            ProbabilisticSegmentation.atom (
                atomReader.apply (chunks [0])
            );
        if (chunks.length == 3) {
            result.glue = Double.valueOf (chunks [1]);
            result.next = ProbabilisticSegmentation.readDouble (chunks [2], atomReader);
        }
        return result;
    }
    
    public FunctionalList <FunctionalList <A>> binarize (double threshold) {
        return this.binarizeIf ((l, p) -> p >= threshold);
    }
    
    public FunctionalList <FunctionalList <A>> binarize50 () {
        return this.binarize (0.5);
    }
    
    /**
     * Convert the probabilistic segmentation into binary: split the list and
     * remove the glue.
     * 
     * @param predicate The predicate to split by.
     * @return          Binary segmentation.
     */
    public FunctionalList <FunctionalList <A>> binarizeIf (BiPredicate <Integer, Double> predicate) {
        return (this.splitIf (predicate).map (GluedList :: elements));
    }
    
    // Example:
    // [u 0.1 p 0.8 s 0.4 e 0.2 t].cut (0.8) = ([u 0.1 p], [s 0.4 e 0.2 t])
    private static <A> UniformPair <GluedList <A, Double>> cutOnProbability (
        GluedList <A, Double> list,
        double cutProbability,
        GluedList <A, Double> first
    ) {
        UniformPair <GluedList <A, Double>> result;
        if (list.isLast ()) {
            throw new IllegalArgumentException ("No such probability: " + cutProbability);
        }
        else {
            Pair <Double, GluedList <A, Double>> next = list.getNext ().get ();
            if (Objects.equals (next.get1 (), cutProbability)) {
                result = new UniformPair <> (first, next.get2 ());
            }
            else {
                first.addLast (next.get1 (), next.get2 ().getElement ());
                result = cutOnProbability (next.get2 (), cutProbability, first);
            }
            return result;
        }
    }
    
    public Double getProbability () {
        return glue;
    }
    
    @Override
    public DoubleList glues () {
        return super.glues (DoubleList :: new);
    }
    
    public ProbabilisticStringSegmentation mapToChar (Function <A, Character> f) {
        return super.bimap (f, p -> p, ProbabilisticStringSegmentation :: new);
    }
    
    public Optional <Double> maxProbability () {
        return maxProbability (this, Optional.empty ());
    }
    
    private static <A> Optional <Double> maxProbability (GluedList <A, Double> list, Optional <Double> knownMax) {
        return list.glues ().mapToDouble (p -> p).maximum ();
    }
    
    public FunctionalList <GluedList <A, Double>> splitIf (BiPredicate <Integer, Double> predicate) {
        return ProbabilisticSegmentation.splitIf (this, predicate);
    }
    
    // Find the position with max probability and try to split there.
    private static <A> FunctionalList <GluedList <A, Double>> splitIf (GluedList <A, Double> list, BiPredicate <Integer, Double> predicate) {
        FunctionalList <GluedList <A, Double>> result;
        Optional <Double> max = maxProbability (list, Optional.empty ());
        if (! max.isPresent () || ! predicate.test (list.size (), max.get ())) {
            result = FunctionalList.of (list);
        }
        else {
            UniformPair <GluedList <A, Double>> cutList =
                cutOnProbability (
                    list,
                    max.get (),
                    new ProbabilisticSegmentation <> (list.getElement ())
                );
            result =
                FunctionalList.concat (
                    ProbabilisticSegmentation.splitIf (cutList.get1 (), predicate),
                    ProbabilisticSegmentation.splitIf (cutList.get2 (), predicate)
                );
        }
        return result;
    }
    
    public FunctionalList <FunctionalList <A>> sureTokens (double lowerThreshold, double upperThreshold) {
        return (
            this.splitIf ((l, p) -> p >= upperThreshold)
            .filter (sublist -> sublist.glues ().all (p -> p <= lowerThreshold))
            .map (GluedList :: elements)
        );
    }
    
    @Override
    public String toString () {
        return this.toStringBinary ((l, p) -> p >= 0.5);
    }
    
    public String toString (Function <Double, String> showProbability) {
        return super.toString (Object :: toString, showProbability);
    }
    
    public String toStringBinary (BiPredicate <Integer, Double> predicate) {
        return (
            String.join (
                GlobalSettings.HARD_DELIMITER_STRING,
                this.binarizeIf (predicate).map (segment -> segment.toString (""))
            )
        );
    }
    
    public String toStringDecimal6 () {
        return this.toString (p -> GLUE_DELIMITER + DoubleFormatter.POINT_6.format (p) + GLUE_DELIMITER);
    }
    
    public String toStringTernary () {
        return this.toStringTernary (1.0 / 3, 2.0 / 3);
    }
    
    public String toStringTernary (double lowerBound, double upperBound) {
        return this.toString (p ->
            p >= upperBound   ? GlobalSettings.HARD_DELIMITER_STRING
            : p >= lowerBound ? GlobalSettings.SOFT_DELIMITER_STRING
            : ""
        );
    }
}
