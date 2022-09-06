package com.github.josefplch.utils.data.list;

import com.github.josefplch.utils.data.Bifunctor;
import com.github.josefplch.utils.data.Either;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.tuple.UniformPair;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The object always contains either the left element or the right one.
 * They cannot be present at the same time.
 * 
 * @param <L> Type of the left element.
 * @param <R> Type of the right element.
 * 
 * @author  Josef Plch
 * @since   2019-03-21
 * @version 2021-01-18
 */
public class EitherList <L, R> extends FunctionalList <Either <L, R>> implements Bifunctor <L, R> {
    public EitherList () {
        super ();
    }
    
    // Probably more efficient than the (iterable) constructor.
    protected EitherList (Collection <? extends Either <L, R>> collection) {
        super (collection);
    }
    
    protected EitherList (Iterable <? extends Either <L, R>> iterable) {
        super (iterable);
    }
    
    @Override
    public <L2, R2> Bifunctor <L2, R2> bimap (Function <L, L2> f, Function <R, R2> g) {
        return super.mapToEither (element -> element.bimap (f, g));
    }

    @Override
    public EitherList <L, R> drop (int n) {
        return super.drop (n, EitherList :: new);
    }
    
    @Override
    public EitherList <L, R> dropWhile (Predicate <Either <L, R>> predicate) {
        return super.dropWhile (predicate, EitherList :: new);
    }
    
    @Override
    public EitherList <L, R> filter (Predicate <Either <L, R>> predicate) {
        return super.filter (predicate, EitherList :: new);
    }
    
    @Override
    public FunctionalList <? extends EitherList <L, R>> group () {
        return super.group (EitherList :: new);
    }
    
    @Override
    public FunctionalList <? extends EitherList <L, R>> groupBy (BiPredicate <Either <L, R>, Either <L, R>> predicate) {
        return super.groupBy (predicate, EitherList :: new);
    }
    
    @Override
    public <K> FunctionalList <? extends EitherList <L, R>> groupByKey (Function <? super Either <L, R>, ? extends K> keyExtractor) {
        return this.groupByKey (keyExtractor, EitherList :: new);
    }
    
    @Override
    public EitherList <L, R> intersperse (Either <L, R> element) {
        return this.intersperse (element, EitherList :: new);
    }
    
    public FunctionalList <L> lefts () {
        return this.lefts (FunctionalList :: new);
    }
    
    /**
     * Extracts from a list of Either all the left elements.
     * 
     * @param <LL> Left list type.
     * @param constructor Left list constructor.
     * @return  All the left elements contained in the list.
     */
    public <LL extends FunctionalList <L>> LL lefts (Supplier <LL> constructor) {
        return super.mapOptional (Either :: safeLeft, constructor);
    }
    
    @Override
    public <C> Bifunctor <C, R> map1 (Function <L, C> f) {
        return super.mapToEither (element -> element.map1 (f));
    }

    @Override
    public <C> Bifunctor <L, C> map2 (Function <R, C> f) {
        return super.mapToEither (element -> element.map2 (f));
    }
    
    @Override
    public UniformPair <EitherList <L, R>> partition (Predicate <Either <L, R>> predicate) {
        return super.partition (predicate, EitherList :: new);
    }
    
    public Pair <? extends FunctionalList <L>, ? extends FunctionalList <R>> partitionEithers () {
        return this.partitionEithers (FunctionalList :: new, FunctionalList :: new);
    }
    
    /**
     * Partitions a list of Either into two lists.All the left elements are
     * extracted, in order, to the first component of the output.Similarly the
     * right elements are extracted to the second component of the output.
     * 
     * See also: PairList.unzip
     *
     * @param <LL> Type of the left list.
     * @param <LR> Type of the right list.
     * @param leftConstructor Constructor of the left list.
     * @param rightConstructor Constructor of the right list.
     * @return Pair of two lists, one containing the left elements and the other
     *         containing the right elements.
     */
    public <LL extends FunctionalList <L>, LR extends FunctionalList <R>> Pair <LL, LR> partitionEithers (
        Supplier <LL> leftConstructor,
        Supplier <LR> rightConstructor
    ) {
        LL lefts = leftConstructor.get ();
        LR rights = rightConstructor.get ();
        for (Either <L, R> either : delegate) {
            if (either.isLeft ()) {
                lefts.add (either.getLeft ());
            }
            else {
                rights.add (either.getRight ());
            }
        }
        return Pair.of (lefts, rights);
    }
    
    @Override
    public EitherList <L, R> reverse () {
        return super.reverse (EitherList :: new);
    }
    
    public FunctionalList <R> rights () {
        return this.rights (FunctionalList :: new);
    }
    
    /**
     * Extracts from a list of Either all the right elements.
     * 
     * @param <LR> Type of the right list.
     * @param constructor Constructor of the right list.
     * @return All the right elements contained in the list.
     */
    public <LR extends FunctionalList <R>> LR rights (Supplier <LR> constructor) {
        return super.mapOptional (Either :: safeRight, constructor);
    }
    
    @Override
    public  EitherList <L, R> shuffle () {
        return super.shuffle (EitherList :: new);
    }
    
    @Override
    public EitherList <L, R> sortBy (Comparator <? super Either <L, R>> comparator) {
        return super.sortBy (comparator, EitherList :: new);
    }
    
    @Override
    public EitherList <L, R> subList (int fromIndex, int toIndex) {
        return super.subList (fromIndex, toIndex, EitherList :: new);
    }
    
    @Override
    public FunctionalList <EitherList <L, R>> sublists () {
        return super.sublistsBy (this :: subList);
    }
    
    @Override
    public FunctionalList <EitherList <L, R>> sublists (int minLength, int maxLength) {
        return super.sublistsBy (minLength, maxLength, this :: subList);
    }
    
    @Override
    public EitherList <L, R> tail () throws IllegalStateException {
        return super.tail (EitherList :: new);
    }
    
    @Override
    public EitherList <L, R> take (int n) {
        return super.take (n, EitherList :: new);
    }
    
    @Override
    public EitherList <L, R> takeWhile (Predicate <Either <L, R>> predicate) {
        return super.takeWhile (predicate, EitherList :: new);
    }
}
