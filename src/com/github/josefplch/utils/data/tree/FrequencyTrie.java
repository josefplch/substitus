package com.github.josefplch.utils.data.tree;

import com.github.josefplch.utils.data.Either;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.map.FrequencyCounter;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.tuple.UniformPair;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * In each node, count two frequencies: prefix frequency and whole sequence
 * frequency (in this order).
 * 
 * @param <A> Type of the atomic elements.
 * 
 * @author  Josef Plch
 * @since   2018-05-12
 * @version 2021-01-18
 */
public class FrequencyTrie <A> extends HashTrie <A, Long, UniformPair <Long>> implements FrequencyCounter <List <A>> {
    public FrequencyTrie () {
        super (0L);
    }
    
    public FrequencyTrie (int initialCapacity) {
        super (initialCapacity, 0L);
    }
    
    public UniformPair <Long> getFrequencies (List <A> key) {
        return (
            super.get (key)
            .map (node -> node.either (n -> new UniformPair <> (n, 0L), pair -> pair))
            .orElse (new UniformPair <> (0L, 0L))
        );
    }
    
    public Long getFrequency (List <A> key) {
        return super.get (key).map (FrequencyTrie :: sequenceFrequency).orElse (0L);
    }
    
    public Long getPrefixFrequency (List <A> key) {
        return super.get (key).map (FrequencyTrie :: prefixFrequency).orElse (0L);
    }
    
    @Override
    public void modifyFrequency (List <A> key, Long difference) {
        // Modify prefix frequencies.
        super.mergePrefixes (
            key,
            Either.left (difference),
            FrequencyTrie :: sumNodes
        );
        // Modify whole sequence frequency.
        super.merge (
            key,
            Either.right (new UniformPair <> (0L, difference)),
            FrequencyTrie :: sumNodes
        );
    }
    
    public static Long prefixFrequency (Either <Long, UniformPair <Long>> nodeValue) {
        return nodeValue.either (n -> n, Pair :: get1);
    }
    
    public void pruneEmpty () {
        super.prune (trie -> trie.getNodeValue ().either (n -> n, Pair :: get1) == 0);
    }
    
    public static Long sequenceFrequency (Either <Long, UniformPair <Long>> nodeValue) {
        return nodeValue.either (n -> 0L, Pair :: get2);
    }
    
    private static Either <Long, UniformPair <Long>> sumNodes (Either <Long, UniformPair <Long>> x, Either <Long, UniformPair <Long>> y) {
        Either <Long, UniformPair <Long>> result;
        if (x.isLeft () && y.isLeft ()) {
            result = Either.left (x.getLeft () + y.getLeft ());
        }
        else {
            result =
                Either.right (
                    new UniformPair <> (
                        prefixFrequency (x) + prefixFrequency (y),
                        sequenceFrequency (x) + sequenceFrequency (y)
                    )
                );
        }
        return result;
    }
    
    private <LA extends FunctionalList <A>> PairList <LA, Long> sequenceFrequencies (Supplier <LA> constructor) {
        return (
            super.entrySet (constructor)
            .mapToPair (entry -> entry.map2 (Pair :: get2))
            .toList ()
        );
    }
    
    @Override
    public PairList <? extends FunctionalList <A>, Long> toAscList () {
        return this.toAscList (FunctionalList :: new);
    }
    
    // Not very effective.
    protected <LA extends FunctionalList <A>> PairList <LA, Long> toAscList (Supplier <LA> constructor) {
        return (
            this.sequenceFrequencies (constructor)
            .sortBy (Comparator.comparing (Pair :: get2))
        );
    }
    
    @Override
    public PairList <? extends FunctionalList <A>, Long> toAscListBy (Comparator <List <A>> keyComparator) {
        return (
            super.entrySet ()
            .mapToPair (entry -> Pair.of (entry.get2 ().get2 (), entry.get1 ()))
            .toList ()
            .sortBy (
                Pair.LexicographicalComparator.basedOn (
                    Comparator.naturalOrder (),
                    keyComparator
                )
            )
            .mapToPair (Pair :: swap)
        );
    }
    
    @Override
    public PairList <? extends FunctionalList <A>, Long> toDescList () {
        return (this.toDescList (FunctionalList :: new));
    }
    
    protected <LA extends FunctionalList <A>> PairList <LA, Long> toDescList (Supplier <LA> constructor) {
        return (this.toAscList (constructor).reverse ());
    }
    
    @Override
    public PairList <? extends FunctionalList <A>, Long> toDescListBy (Comparator <List <A>> keyComparator) {
        return (
            super.entrySet ()
            .mapToPair (entry -> Pair.of (entry.get2 ().get2 (), entry.get1 ()))
            .toList ()
            .sortBy (
                Pair.LexicographicalComparator.basedOn (
                    Comparator.reverseOrder (),
                    keyComparator
                )
            )
            .mapToPair (Pair :: swap)
        );
    }
}
