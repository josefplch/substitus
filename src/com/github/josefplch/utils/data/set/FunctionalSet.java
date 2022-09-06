package com.github.josefplch.utils.data.set;

import com.github.josefplch.utils.data.foldable.Foldable;
import com.github.josefplch.utils.data.Functor;
import com.github.josefplch.utils.data.list.FunctionalList;
import com.github.josefplch.utils.data.set.number.LongSet;
import com.github.josefplch.utils.data.tuple.Pair;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @param <A> The type of elements in the set.
 * 
 * @author  Josef Plch
 * @since   2015-10-30
 * @version 2022-03-21
 */
public class FunctionalSet <A> implements Set <A>, Foldable <A>, Functor <A>, Serializable {
    protected final Set <A> delegate = new HashSet <> ();
    
    public FunctionalSet () {
    }
    
    protected FunctionalSet (Collection <? extends A> collection) {
        delegate.addAll (collection);
    }
    
    public static <A> FunctionalSet <A> from (Collection <? extends A> collection) {
        return new FunctionalSet <> (collection);
    }
    
    @SafeVarargs
    public static <A> FunctionalSet <A> of (A ... elements) {
        return FunctionalSet.from (Arrays.asList (elements));
    }
    
    @SafeVarargs
    public static <A, SA extends Set <? extends A>> FunctionalSet <A> union (SA set1, SA set2, SA ... others) {
        List <SA> sets = new FunctionalList <> ();
        sets.add (set1);
        sets.add (set2);
        sets.addAll (Arrays.asList (others));
        return FunctionalSet.union (sets);
    }
    
    // Compare to FunctionalList.concat.
    public static <A, SA extends Set <? extends A>> FunctionalSet <A> union (List <SA> listOfSets) {
        FunctionalSet <A> result = new FunctionalSet <> ();
        for (SA sublist : listOfSets) {
            result.addAll (sublist);
        }
        return result;
    }
    
    // Standard Java set function.
    @Override
    public boolean add (A element) {
        return delegate.add (element);
    }
    
    // Standard Java set function.
    @Override
    public boolean addAll (Collection <? extends A> collection) {
        return delegate.addAll (collection);
    }
    
    // Standard Java set function.
    @Override
    public void clear () {
        delegate.clear ();
    }
    
    // Standard Java set function.
    @Override
    public boolean contains (Object object) {
        return delegate.contains (object);
    }
    
    // Standard Java set function.
    @Override
    public boolean containsAll (Collection <?> c) {
        return delegate.containsAll (c);
    }
    
    @Override
    public boolean equals (Object object) {
        boolean result;
        if (object == this) {
            result = true;
        }
        else if (object == null || ! (object instanceof Set)) {
            result = false;
        }
        else {
            result = Objects.equals (this.delegate, object);
        }
        return result;
    }
    
    /**
     * O(n). Returns a set of those elements that satisfy the predicate.
     * 
     * @param predicate The tested condition.
     * @return Reduced set.
     */
    public FunctionalSet <A> filter (Predicate <A> predicate) {
        return this.filter (predicate, FunctionalSet :: new);
    }
    
    protected <SA extends FunctionalSet <A>> SA filter (Predicate <A> predicate, Supplier <SA> newSet) {
        SA result = newSet.get ();
        for (A a : delegate) {
            if (predicate.test (a)) {
                result.add (a);
            }
        }
        return result;
    }
    
    @Override
    public <B> B foldl (BiFunction <B, A, B> f, B b0) {
        B b = b0;
        for (A a : delegate) {
            b = f.apply (b, a);
        }
        return b;
    }
    
    @Override
    public <B> B foldr (BiFunction <A, B, B> f, B b0) {
        // As the order of the elements is not guaranteed, we can use foldl.
        return foldl ((a, b) -> f.apply (b, a), b0);
    }

    @Override
    public int hashCode () {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode (this.delegate);
        return hash;
    }

    // Standard Java set function.
    @Override
    public boolean isEmpty () {
        return delegate.isEmpty ();
    }
    
    // Standard Java set function.
    @Override
    public Iterator <A> iterator () {
        return delegate.iterator ();
    }
    
    @Override
    public <B> FunctionalSet <B> map (Function <A, B> f) {
        return (this.map (f, FunctionalSet :: new));
    }
    
    public <B, SB extends FunctionalSet <B>> SB map (Function <A, B> f, Supplier <SB> newSet) {
        SB result = newSet.get ();
        for (A a : delegate) {
            try {
                result.add (f.apply (a));
            }
            catch (RuntimeException exception) {
                throw new RuntimeException ("An exception occured on element " + a, exception);
            }
        }
        return result;
    }
    
    public <B> FunctionalSet <B> mapOptional (Function <A, Optional <B>> f) {
        return this.map (f).filter (Optional :: isPresent).map (Optional :: get);
    }
    
    public <B extends Comparable <B>> ComparableSet <B> mapToComparable (Function <A, B> f) {
        return this.map (f, ComparableSet :: new);
    }
    
    public <T1, T2> PairSet <T1, T2> mapToPair (Function <A, Pair <T1, T2>> f) {
        return this.map (f, PairSet :: new);
    }
    
    public LongSet mapToLong (Function <A, Long> f) {
        return this.map (f, LongSet :: new);
    }
    
    public StringSet mapToString () {
        return this.mapToString (Object :: toString);
    }
    
    public StringSet mapToString (Function <A, String> f) {
        return this.map (f, StringSet :: new);
    }
    
    // Standard Java set function.
    @Override
    public boolean remove (Object object) {
        return delegate.remove (object);
    }
    
    // Standard Java set function.
    @Override
    public boolean removeAll (Collection <?> collection) {
        return delegate.removeAll (collection);
    }
    
    public Optional <A> representant () {
        return (
            delegate.isEmpty ()
            ? Optional.empty ()
            : Optional.of (delegate.iterator ().next ())
        );
    }
    
    // Standard Java set function.
    @Override
    public boolean retainAll (Collection <?> collection) {
        return delegate.retainAll (collection);
    }
    
    // Standard Java set function.
    @Override
    public int size () {
        return delegate.size ();
    }
    
    // Standard Java set function.
    @Override
    public Object [] toArray () {
        return delegate.toArray ();
    }
    
    // Standard Java set function.
    @Override
    public <T> T [] toArray (T [] array) {
        return delegate.toArray (array);
    }
    
    @Override
    public FunctionalList <A> toList () {
        return this.toList (FunctionalList :: new);
    }
    
    @Override
    public <LA extends FunctionalList <A>> LA toList (Supplier <LA> constructor) {
        LA result = constructor.get ();
        result.addAll (delegate);
        return result;
    }
    
    @Override
    public String toString () {
        return (
            "{"
            + String.join (
                ", ",
                FunctionalList.from (delegate).map (Object :: toString)
            )
            + "}"
        );
    }
}
