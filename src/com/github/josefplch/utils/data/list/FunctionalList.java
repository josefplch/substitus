package com.github.josefplch.utils.data.list;

import com.github.josefplch.utils.data.Either;
import com.github.josefplch.utils.data.foldable.Foldable;
import com.github.josefplch.utils.data.Functor;
import com.github.josefplch.utils.data.function.Consumer3;
import com.github.josefplch.utils.data.list.number.LongList;
import com.github.josefplch.utils.data.list.number.FloatList;
import com.github.josefplch.utils.data.list.number.DoubleList;
import com.github.josefplch.utils.data.list.number.IntegerList;
import com.github.josefplch.utils.data.set.FunctionalSet;
import com.github.josefplch.utils.data.string.StringUtils;
import com.github.josefplch.utils.data.tuple.Pair;
import com.github.josefplch.utils.data.tuple.Tuple3;
import com.github.josefplch.utils.data.tuple.UniformPair;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @param <A> The type of elements in the list.
 * 
 * @author  Josef Plch
 * @since   2018-05-02
 * @version 2022-09-06
 */
public class FunctionalList <A> implements Foldable <A>, Functor <A>, List <A>, Serializable {
    protected final List <A> delegate;
    
    public FunctionalList () {
        delegate = new ArrayList <> ();
    }
    
    // Probably more efficient than the (iterable) constructor.
    protected FunctionalList (Collection <? extends A> collection) {
        delegate = new ArrayList <> (collection);
    }
    
    protected FunctionalList (Iterable <? extends A> iterable) {
        delegate = new ArrayList <> ();
        for (A element : iterable) {
            delegate.add (element);
        }
    }
    
    @SafeVarargs
    public static <A, LA extends List <? extends A>> FunctionalList <A> concat (LA list1, LA list2, LA ... others) {
        List <LA> lists = new FunctionalList <> ();
        lists.add (list1);
        lists.add (list2);
        lists.addAll (Arrays.asList (others));
        return FunctionalList.concat (lists);
    }
    
    /**
     * Concatenate a list of lists. Compare to FunctionalSet.union.

     * @param <A> Arbitrary type.
     * @param <LA> Arbitrary list type.
     * 
     * @param lists List of lists to be concatenated.
     * @return Single-level list containing all elements of all sublists of the original list.
     */
    public static <A, LA extends List <? extends A>> FunctionalList <A> concat (List <LA> lists) {
        FunctionalList <A> result = new FunctionalList <> ();
        for (LA list : lists) {
            result.addAll (list);
        }
        return result;
    }
    
    public static <A> FunctionalList <A> from (Collection <? extends A> collection) {
        return new FunctionalList <> (collection);
    }
    
    // Also known as implode or intercalate.
    public static <A> FunctionalList <A> join (List <A> xs, List <List <A>> xxs) {
        return FunctionalList.concat (FunctionalList.from (xxs).intersperse (xs));
    }
    
    @SafeVarargs
    public static <A> FunctionalList <A> of (A ... elements) {
        return FunctionalList.from (Arrays.asList (elements));
    }
    
    public static <A> FunctionalList <A> replicate (int n, A element) {
        FunctionalList <A> result = new FunctionalList <> ();
        result.addN (n, element);
        return result;
    }
    
    // Standard List function.
    @Override
    public boolean add (A e) {
        return delegate.add (e);
    }
    
    // Standard List function.
    @Override
    public void add (int index, A element) {
        delegate.add (index, element);
    }
    
    // Standard List function.
    @Override
    public boolean addAll (Collection <? extends A> collection) {
        return delegate.addAll (collection);
    }
    
    // Generalized version of addAll (collection).
    public boolean addAll (Iterable <? extends A> iterable) {
        int originalSize = delegate.size ();
        for (A element : iterable) {
            delegate.add (element);
        }
        return (this.size () > originalSize);
    }
    
    // Standard List function.
    @Override
    public boolean addAll (int index, Collection <? extends A> collection) {
        return delegate.addAll (index, collection);
    }
    
    public void addN (int n, A element) {
        for (int i = 0; i < n; i++) {
            delegate.add (element);
        }
    }
    
    // See also: uniquePairs
    public FunctionalList <UniformPair <A>> bigrams () {
        FunctionalList <UniformPair <A>> result = new FunctionalList <> ();
        for (int i = 0; i < delegate.size () - 1; i++) {
            result.add (
                new UniformPair <> (delegate.get (i), delegate.get (i + 1))
            );
        }
        return result;
    }
    
    // Standard List function.
    @Override
    public void clear () {
        delegate.clear ();
    }
    
    // Standard List function. For sublist version, see hasPrefix / hasInfix / hasSuffix.
    @Override
    public boolean contains (Object object) {
        return delegate.contains (object);
    }
    
    // Standard List function.
    @Override
    public boolean containsAll (Collection <?> collection) {
        return delegate.containsAll (collection);
    }
    
    public FunctionalList <A> drop (int n) {
        return this.drop (n, FunctionalList :: new);
    }
    
    protected <LA extends FunctionalList <A>> LA drop (int n, Supplier <LA> constructor) {
        return (
            this.subList (
                Math.min (n, delegate.size ()),
                delegate.size (),
                constructor
            )
        );
    }
    
    public FunctionalList <A> dropWhile (Predicate <A> predicate) {
        return this.dropWhile (predicate, FunctionalList :: new);
    }
    
    protected <LA extends FunctionalList <A>> LA dropWhile (Predicate <A> predicate, Supplier <LA> constructor) {
        LA result = constructor.get ();
        boolean dropPhase = true;
        for (A element : delegate) {
            if (! predicate.test (element)) {
                dropPhase = false;
            }
            if (! dropPhase) {
                result.add (element);
            }
        }
        return result;
    }
    
    @Override
    public boolean equals (Object object) {
        boolean result;
        if (object == this) {
            result = true;
        }
        else if (object == null || ! (object instanceof List)) {
            result = false;
        }
        else {
            result = Objects.equals (delegate, object);
        }
        return result;
    }
    
    /**
     * O(n). Returns a list of those elements that satisfy the predicate.
     * 
     * @param predicate The tested condition.
     * @return Reduced list.
     */
    public FunctionalList <A> filter (Predicate <A> predicate) {
        return this.filter (predicate, FunctionalList :: new);
    }
    
    protected <LA extends FunctionalList <A>> LA filter (Predicate <A> predicate, Supplier <LA> constructor) {
        LA result = constructor.get ();
        for (int i = 0; i < delegate.size (); i++) {
            try {
                A element = delegate.get (i);
                if (predicate.test (element)) {
                    result.add (element);
                }
            }
            catch (RuntimeException exception) {
                throw new RuntimeException (
                    "An exception occured on element #" + i
                    + " (" + delegate.get (i) + ")",
                    exception
                );
            }
        }
        return result;
    }
    
    // Monadic function, Haskell's "bind", aka "concatMap".
    // (Java is not expressive enough to describe a monad.)
    public <B> FunctionalList <B> flatMap (Function <A, ? extends List <? extends B>> f) {
        return this.flatMapI ((i, x) -> f.apply (x), FunctionalList :: new);
    }
    
    public <B, LB extends FunctionalList <B>> LB flatMap (Function <A, ? extends List <? extends B>> f, Supplier <LB> constructor) {
        return this.flatMapI ((i, x) -> f.apply (x), constructor);
    }
    
    public <B> FunctionalList <B> flatMapI (BiFunction <Integer, A, ? extends List <? extends B>> f) {
        return this.flatMapI (f, FunctionalList :: new);
    }
    
    public <B, LB extends FunctionalList <B>> LB flatMapI (BiFunction <Integer, A, ? extends List <? extends B>> f, Supplier <LB> constructor) {
        return this.genericMap ((i, x, list) -> list.addAll (f.apply (i, x)), constructor);
    }
    
    @Override
    public <B> B foldl (BiFunction <B, A, B> f, B defaultValue) {
        B result = defaultValue;
        for (A element : delegate) {
            result = f.apply (result, element);
        }
        return result;
    }
    
    @Override
    public <B> B foldr (BiFunction <A, B, B> f, B defaultValue) {
        B result = defaultValue;
        for (int i = delegate.size () - 1; i >= 0; i--) {
            result = f.apply (delegate.get (i), result);
        }
        return result;
    }
    
    private <B, LB extends FunctionalList <B>> LB genericMap (Consumer3 <Integer, A, LB> f, Supplier <LB> constructor) {
        LB result = constructor.get ();
        for (int i = 0; i < delegate.size (); i++) {
            A x = delegate.get (i);
            try {
                f.accept (i, x, result);
            }
            catch (RuntimeException exception) {
                throw new RuntimeException (
                    "An exception occured on element #" + i
                    + " (" + x + ")",
                    exception
                );
            }
        }
        return result;
    }
    
    private <B, LB extends FunctionalList <B>> LB genericMapBackup (BiConsumer <A, LB> f, Supplier <LB> constructor) {
        LB result = constructor.get ();
        for (int i = 0; i < delegate.size (); i++) {
            try {
                f.accept (delegate.get (i), result);
            }
            catch (RuntimeException exception) {
                throw new RuntimeException (
                    "An exception occured on element #" + i
                    + " (" + delegate.get (i) + ")",
                    exception
                );
            }
        }
        return result;
    }
    
    // Standard List function.
    @Override
    public A get (int index) {
        return delegate.get (index);
    }
    
    public FunctionalList <? extends FunctionalList <A>> group () {
        return this.group (FunctionalList :: new);
    }
    
    protected <LA extends FunctionalList <A>> FunctionalList <LA> group (Supplier <LA> constructor) {
        return this.groupBy (Objects :: equals, constructor);
    }
    
    public FunctionalList <? extends FunctionalList <A>> groupBy (BiPredicate <A, A> predicate) {
        return this.groupBy (predicate, FunctionalList :: new);
    }
    
    protected <LA extends FunctionalList <A>> FunctionalList <LA> groupBy (BiPredicate <A, A> predicate, Supplier <LA> constructor) {
        FunctionalList <LA> result = new FunctionalList <> ();
        LA currentGroup = null;
        A lastElement = null;
        for (A element : delegate) {
            if (currentGroup == null || ! predicate.test (lastElement, element)) {
                currentGroup = constructor.get ();
                result.add (currentGroup);
            }
            currentGroup.add (element);
            lastElement = element;
        }
        return result;
    }
    
    public <K> FunctionalList <? extends FunctionalList <A>> groupByKey (Function <? super A, ? extends K> keyExtractor) {
        return this.groupByKey (keyExtractor, FunctionalList :: new);
    }
    
    protected <K, LA extends FunctionalList <A>> FunctionalList <LA> groupByKey (Function <? super A, ? extends K> keyExtractor, Supplier <LA> constructor) {
        return (
            this.groupBy ((x, y) ->
                Objects.equals (
                    keyExtractor.apply (x),
                    keyExtractor.apply (y)
                ),
                constructor
            )
        );
    }
    
    @Override
    public int hashCode () {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode (delegate);
        return hash;
    }
    
    /**
     * The hasInfix function takes a list and returns true iff it is contained,
     * wholly and intact, anywhere within this list.
     * 
     * See also: subIndex
     * 
     * @param needle The list to search for.
     * @return       Is the needle infix of this list?
     */
    public boolean hasInfix (List <A> needle) {
        return this.subIndex (needle).isPresent ();
    }
    
    /**
     * The hasPrefix function takes a list and returns true iff it is a prefix
     * of this list.
     * 
     * @param needle The list to search for.
     * @return       Is the needle prefix of this list?
     */
    public boolean hasPrefix (List <A> needle) {
        boolean result;
        if (needle.size () > delegate.size ()) {
            result = false;
        }
        else {
            result = true;
            for (int i = 0; i < needle.size (); i++) {
                if (! Objects.equals (needle.get (i), delegate.get (i))) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
    
    /**
     * The hasSuffix function takes a list and returns true iff it is a suffix
     * of this list.
     * 
     * @param needle The list to search for.
     * @return       Is the needle suffix of this list?
     */
    public boolean hasSuffix (List <A> needle) {
        boolean result;
        if (needle.size () > delegate.size ()) {
            result = false;
        }
        else {
            result = true;
            for (int i = 0; i < needle.size (); i++) {
                if (! Objects.equals (needle.get (i), delegate.get (delegate.size () - needle.size () + i))) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
    
    /**
     * Get the first element.
     * 
     * @return The first element.
     * @throws IndexOutOfBoundsException If the list is empty.
     */
    public A head () throws IndexOutOfBoundsException {
        if (delegate.isEmpty ()) {
            throw new IndexOutOfBoundsException ("The list is empty.");
        }
        else {
            return delegate.get (0);
        }
    }
    
    // Standard List function.
    @Override
    public int indexOf (Object object) {
        return delegate.indexOf (object);
    }
    
    // Similar to indexOf.
    public IntegerList indicesOf (A element) {
        IntegerList result = new IntegerList ();
        for (int i = 0; i < delegate.size (); i++) {
            if (Objects.equals (delegate.get (i), element)) {
                result.add (i);
            }
        }
        return result;
    }
    
    /**
     * The intersperse function takes an element and a list and intersperses
     * that element between the elements of the list. For example:
     * intersperse (',', "abcde") = "a,b,c,d,e"
     * 
     * @param delimiter The element to be interspersed.
     * @return A new list.
     */
    public FunctionalList <A> intersperse (A delimiter) {
        return this.intersperse (delimiter, FunctionalList :: new);
    }
    
    protected <LA extends FunctionalList <A>> LA intersperse (A delimiter, Supplier <LA> constructor) {
        LA result = constructor.get ();
        boolean first = true;
        for (A element : delegate) {
            if (first) {
                first = false;
            }
            else {
                result.add (delimiter);
            }
            result.add (element);
        }
        return result;
    }
    
    // Standard List function.
    @Override
    public boolean isEmpty () {
        return delegate.isEmpty ();
    }
    
    // Standard List function.
    @Override
    public Iterator <A> iterator () {
        return delegate.iterator ();
    }
    
    /**
     * Get the last element.
     * 
     * @return The last element.
     * @throws IndexOutOfBoundsException If the list is empty.
     */
    public A last () throws IndexOutOfBoundsException {
        if (delegate.isEmpty ()) {
            throw new IndexOutOfBoundsException ("The list is empty.");
        }
        else {
            return delegate.get (delegate.size () - 1);
        }
    }
    
    // Standard List function.
    @Override
    public int lastIndexOf (Object o) {
        return delegate.lastIndexOf (o);
    }
    
    // Standard List function.
    @Override
    public ListIterator <A> listIterator () {
        return delegate.listIterator ();
    }
    
    // Standard List function.
    @Override
    public ListIterator <A> listIterator (int index) {
        return delegate.listIterator (index);
    }
    
    @Override
    public <B> FunctionalList <B> map (Function <A, B> f) {
        return this.mapI ((i, x) -> f.apply (x), FunctionalList :: new);
    }
    
    public <B, LB extends FunctionalList <B>> LB map (Function <A, B> f, Supplier <LB> constructor) {
        return this.mapI ((i, x) -> f.apply (x), constructor);
    }
    
    public <B> FunctionalList <B> mapI (BiFunction <Integer, A, B> f) {
        return this.mapI (f, FunctionalList :: new);
    }
    
    public <B, LB extends FunctionalList <B>> LB mapI (BiFunction <Integer, A, B> f, Supplier <LB> constructor) {
        return this.genericMap ((i, x, list) -> list.add (f.apply (i, x)), constructor);
    }
    
    public <B> FunctionalList <B> mapOptional (Function <A, Optional <B>> f) {
        return this.mapOptional ((i, x) -> f.apply (x), FunctionalList :: of);
    }
    
    public <B> FunctionalList <B> mapOptional (BiFunction <Integer, A, Optional <B>> f) {
        return this.mapOptional (f, FunctionalList :: of);
    }
    
    public <B, LB extends FunctionalList <B>> LB mapOptional (Function <A, Optional <B>> f, Supplier <LB> constructor) {
        return this.mapOptional ((i, x) -> f.apply (x), constructor);
    }
    
    public <B, LB extends FunctionalList <B>> LB mapOptional (BiFunction <Integer, A, Optional <B>> f, Supplier <LB> constructor) {
        return this.genericMap ((i, x, list) -> f.apply (i, x).ifPresent (justX -> list.add (justX)), constructor);
    }
    
    public <B extends Comparable <B>> ComparableList <B> mapToComparable (Function <A, B> f) {
        return this.map (f, ComparableList :: new);
    }
    
    public <B extends Comparable <B>> ComparableList <B> mapToComparableI (BiFunction <Integer, A, B> f) {
        return this.mapI (f, ComparableList :: new);
    }
    
    public CharList mapToChar (Function <A, Character> f) {
        return this.map (f, CharList :: new);
    }
    
    public CharList mapToCharI (BiFunction <Integer, A, Character> f) {
        return this.mapI (f, CharList :: new);
    }
    
    public DoubleList mapToDouble (Function <A, Double> f) {
        return this.map (f, DoubleList :: new);
    }
    
    public DoubleList mapToDoubleI (BiFunction <Integer, A, Double> f) {
        return this.mapI (f, DoubleList :: new);
    }
    
    public <L, R> EitherList <L, R> mapToEither (Function <A, Either <L, R>> f) {
        return this.map (f, EitherList :: new);
    }
    
    public <L, R> EitherList <L, R> mapToEitherI (BiFunction <Integer, A, Either <L, R>> f) {
        return this.mapI (f, EitherList :: new);
    }
    
    public FloatList mapToFloat (Function <A, Float> f) {
        return this.map (f, FloatList :: new);
    }
    
    public FloatList mapToFloatI (BiFunction <Integer, A, Float> f) {
        return this.mapI (f, FloatList :: new);
    }
    
    public IntegerList mapToInteger (Function <A, Integer> f) {
        return this.map (f, IntegerList :: new);
    }
    
    public IntegerList mapToIntegerI (BiFunction <Integer, A, Integer> f) {
        return this.mapI (f, IntegerList :: new);
    }
    
    public LongList mapToLong (Function <A, Long> f) {
        return this.map (f, LongList :: new);
    }
    
    public LongList mapToLongI (BiFunction <Integer, A, Long> f) {
        return this.mapI (f, LongList :: new);
    }
    
    public <B> OperatorList <B> mapToOperator (Function <A, UnaryOperator <B>> f) {
        return this.map (f, OperatorList :: new);
    }
    
    public <B> OperatorList <B> mapToOperatorI (BiFunction <Integer, A, UnaryOperator <B>> f) {
        return this.mapI (f, OperatorList :: new);
    }
    
    public <B1, B2> PairList <B1, B2> mapToPair (Function <A, Pair <B1, B2>> f) {
        return this.map (f, PairList :: new);
    }
    
    public <B1, B2> PairList <B1, B2> mapToPairI (BiFunction <Integer, A, Pair <B1, B2>> f) {
        return this.mapI (f, PairList :: new);
    }
    
    public StringList mapToString () {
        return this.mapToString (Object :: toString);
    }
    
    public StringList mapToString (Function <A, String> f) {
        return this.map (f, StringList :: new);
    }
    
    public StringList mapToStringI (BiFunction <Integer, A, String> f) {
        return this.mapI (f, StringList :: new);
    }
    
    /**
     * The partition function takes a predicate a list and returns the pair of
     * lists of elements which do and do not satisfy the predicate,
     * respectively; i.e.:
     * 
     * partition (p, xs) == (filter (p, xs), filter (not (p), xs)
     * 
     * @param predicate  The predicate.
     * @return           Pair of two lists.
     */
    public UniformPair <? extends FunctionalList <A>> partition (Predicate <A> predicate) {
        return this.partition (predicate, FunctionalList :: new);
    }
    
    public <LA extends FunctionalList <A>> UniformPair <LA> partition (Predicate <A> predicate, Supplier <LA> constructor) {
        LA trues  = constructor.get ();
        LA falses = constructor.get ();
        for (A element : delegate) {
            if (predicate.test (element)) {
                trues.add (element);
            }
            else {
                falses.add (element);
            }
        }
        return new UniformPair <> (trues, falses);
    }
    
    // Standard List function.
    @Override
    public boolean remove (Object o) {
        return delegate.remove (o);
    }
    
    // Standard List function.
    @Override
    public A remove (int index) {
        return delegate.remove (index);
    }
    
    // Standard List function.
    @Override
    public boolean removeAll (Collection <?> collection) {
        return delegate.removeAll (collection);
    }
    
    public FunctionalList <A> replaceSequence (List <A> sequence, List <? extends A> replacement) {
        FunctionalList <A> result = new FunctionalList <> ();
        int i = 0;
        while (i < delegate.size ()) {
            boolean sequenceFound =
                i < delegate.size () - sequence.size () + 1
                && Objects.equals (delegate.subList (i, i + sequence.size ()), sequence);
            if (sequenceFound) {
                result.addAll (replacement);
                i += sequence.size ();
            }
            else {
                result.add (delegate.get (i));
                i++;
            }
        }
        return result;
    }
    
    // Standard List function.
    @Override
    public boolean retainAll (Collection <?> collection) {
        return delegate.retainAll (collection);
    }
    
    public FunctionalList <A> reverse () {
        return this.reverse (FunctionalList :: new);
    }
    
    protected <LA extends FunctionalList <A>> LA reverse (Supplier <LA> constructor) {
        LA result = constructor.get ();
        result.addAll (delegate);
        Collections.reverse (result.delegate);
        return result;
    }
    
    // Standard List function.
    @Override
    public A set (int index, A element) {
        return delegate.set (index, element);
    }
    
    public FunctionalList <A> shuffle () {
        return this.shuffle (FunctionalList :: new);
    }
    
    // TODO: Use SecureRandom?
    protected <LA extends FunctionalList <A>> LA shuffle (Supplier <LA> constructor) {
        LA result = constructor.get ();
        result.addAll (
            this.mapToPair (x -> Pair.of (x, Math.random ()))
            .sortBy (Comparator.comparing (Pair :: get2))
            .firsts ()
        );
        return result;
    }
    
    // Standard List function, aka "length".
    @Override
    public int size () {
        return delegate.size ();
    }
    
    public FunctionalList <A> sortBy (Comparator <? super A> comparator) {
        return this.sortBy (comparator, FunctionalList :: new);
    }
    
    protected <LA extends FunctionalList <A>> LA sortBy (Comparator <? super A> comparator, Supplier <LA> constructor) {
        LA result = constructor.get ();
        result.addAll (delegate);
        result.delegate.sort (comparator);
        return result;
    }
    
    /**
     * Similar to indexOf. Instead of looking for one element in a list, this
     * function looks for the first occurance of a sublist in the list, and
     * returns the index of the first element of that occurance. If there is no
     * such list, returns Nothing.
     * <br>
     * If the list to look for is the empty list, will return Optional.of (0)
     * regardless of the content of the list to search.
     * <br>
     * See also: hasInfix
     * 
     * @param sublist The list to look for.
     * @return Index of the first element (if any).
     */
    public Optional <Integer> subIndex (List <A> sublist) {
        Optional <Integer> result = Optional.empty ();
        for (int i = 0; ! result.isPresent () && i < delegate.size () - sublist.size () + 1; i++)
        {
            if (this.drop (i).hasPrefix (sublist)) {
                result = Optional.of (i);
            }
        }
        return result;
    }
    
    // Similar to indicesOf.
    public IntegerList subIndices (List <A> sublist) {
        IntegerList result = new IntegerList ();
        for (int i = 0; i < delegate.size () - sublist.size () + 1; i++) {
            if (this.drop (i).hasPrefix (sublist)) {
                result.add (i);
            }
        }
        return result;
    }
    
    // Standard List function.
    @Override
    public FunctionalList <A> subList (int fromIndex, int toIndex) {
        return this.subList (fromIndex, toIndex, FunctionalList :: new);
    }
    
    protected <LA extends FunctionalList <A>> LA subList (int fromIndex, int toIndex, Supplier <LA> constructor) {
        LA result = constructor.get ();
        for (int i = fromIndex; i < toIndex; i++) {
            result.add (delegate.get (i));
        }
        return result;
    }
    
    public FunctionalList <? extends FunctionalList <A>> sublists () {
        return this.sublistsBy (this :: subList);
    }
    
    public FunctionalList <? extends FunctionalList <A>> sublists (int minLength, int maxLength) {
        return this.sublistsBy (minLength, maxLength, this :: subList);
    }
    
    protected <LA extends FunctionalList <A>> FunctionalList <LA> sublistsBy (
        BiFunction <Integer, Integer, LA> sublistExtractor
    ) {
        return this.sublistsBy (1, delegate.size (), sublistExtractor);
    }
    
    protected <LA extends FunctionalList <A>> FunctionalList <LA> sublistsBy (
        int minLength,
        int maxLength,
        BiFunction <Integer, Integer, LA> sublistExtractor
    ) {
        if (minLength < 0) {
            throw new IllegalArgumentException ("The minimum length cannot be negative.");
        }
        else {
            FunctionalList <LA> result = new FunctionalList <> ();
            for (int i1 = 0; i1 < delegate.size (); i1++) {
                for (int i2 = i1 + minLength - 1; i2 < i1 + maxLength && i2 < delegate.size (); i2++) {
                    result.add (sublistExtractor.apply (i1, i2 + 1));
                }
            }
            return result;
        }
    }
    
    /**
     * Get all elements except the first one.
     * 
     * @return The list except the first element.
     * @throws IndexOutOfBoundsException If the list is empty.
     */
    public FunctionalList <A> tail () throws IndexOutOfBoundsException {
        return this.tail (FunctionalList :: new);
    }
    
    protected <LA extends FunctionalList <A>> LA tail (Supplier <LA> constructor) throws IndexOutOfBoundsException {
        try {
            return this.subList (1, delegate.size (), constructor);
        }
        catch (IndexOutOfBoundsException exception) {
            throw new IndexOutOfBoundsException ("The list is empty.");
        }
    }
    
    public FunctionalList <A> take (int n) {
        return this.take (n, FunctionalList :: new);
    }
    
    protected <LA extends FunctionalList <A>> LA take (int n, Supplier <LA> constructor) {
        return this.subList (
            0,
            Math.min (n, delegate.size ()),
            constructor
        );
    }
    
    public FunctionalList <A> takeWhile (Predicate <A> predicate) {
        return this.takeWhile (predicate, FunctionalList :: new);
    }

    protected <LA extends FunctionalList <A>> LA takeWhile (Predicate <A> predicate, Supplier <LA> constructor) {
        LA result = constructor.get ();
        for (A element : delegate) {
            if (predicate.test (element)) {
                result.add (element);
            }
            else {
                break;
            }
        }
        return result;
    }
    
    // Standard List function.
    @Override
    public Object [] toArray () {
        return delegate.toArray ();
    }
    
    // Standard List function.
    @Override
    public <T> T [] toArray (T [] array) {
        return delegate.toArray (array);
    }
    
    public FunctionalSet <A> toSet () {
        return this.toSet (FunctionalSet :: new);
    }
    
    public <SA extends FunctionalSet <A>> SA toSet (Supplier <SA> constructor) {
        SA result = constructor.get ();
        result.addAll (delegate);
        return result;
    }
    
    @Override
    public String toString () {
        return delegate.toString ();
    }
    
    /**
     * Character list is converted to string. Else, the behavior is equivalent
     * to standard toString ().
     * 
     * @param emptyString Value for empty string.
     * @return            A string representation of the object.
     */
    public String toString (String emptyString) {
        return this.toString (emptyString, false);
    }
    
    /**
     * Character list is converted to string. Else, the behavior is equivalent
     * to standard toString ().
     * 
     * @param emptyString Value for empty string.
     * @param showQuotes  Shall the string be quoted?
     * @return            A string representation of the object.
     */
    @SuppressWarnings ("unchecked")
    public String toString (String emptyString, boolean showQuotes) {
        String result;
        if (delegate.isEmpty ()) {
            result = emptyString;
        }
        else {
            if (this.all (x -> x instanceof Character)) {
                String string = StringUtils.charListToString ((FunctionalList <Character>) this);
                result = showQuotes ? '"' + string + '"' : string;
            }
            else {
                result = delegate.toString ();
            }
        }
        return result;
    }
    
    public FunctionalList <A> unique () {
        return this.toSet ().toList ();
    }
    
    // TODO: Find a better name.
    // See also: bigrams
    public FunctionalList <UniformPair <A>> uniquePairs () {
        FunctionalList <UniformPair <A>> result = new FunctionalList <> ();
        for (int i1 = 0; i1 < delegate.size (); i1++) {
            for (int i2 = i1 + 1; i2 < delegate.size (); i2++) {
                result.add (
                    new UniformPair <> (
                        delegate.get (i1),
                        delegate.get (i2)
                    )
                );
            }
        }
        return result;
    }
    
    // TODO: Find a better name.
    public FunctionalList <Tuple3 <A, A, A>> uniqueTuples3 () {
        FunctionalList <Tuple3 <A, A, A>> result = new FunctionalList <> ();
        for (int i1 = 0; i1 < delegate.size (); i1++) {
            for (int i2 = i1 + 1; i2 < delegate.size (); i2++) {
                for (int i3 = i2 + 1; i3 < delegate.size (); i3++) {
                    result.add (
                        Tuple3.of (
                            delegate.get (i1),
                            delegate.get (i2),
                            delegate.get (i3)
                        )
                    );
                }
            }
        }
        return result;
    }
}
