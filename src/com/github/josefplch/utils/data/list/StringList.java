package com.github.josefplch.utils.data.list;

import com.github.josefplch.utils.data.foldable.StringFoldable;
import com.github.josefplch.utils.data.set.StringSet;
import com.github.josefplch.utils.data.tuple.UniformPair;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * List of strings.
 * 
 * @author  Josef Plch
 * @since   2019-03-29
 * @version 2022-01-19
 */
public class StringList extends ComparableList <String> implements StringFoldable {
    public StringList () {
        super ();
    }
    
    // Probably more efficient than the (iterable) constructor.
    public StringList (Collection <String> collection) {
        super (collection);
    }
    
    public StringList (Iterable <String> iterable) {
        super (iterable);
    }
    
    // Split by tab.
    public static StringList columns (String string) {
        return StringList.split ('\t', string);
    }
    
    // Split by new line character.
    public static StringList lines (String string) {
        return StringList.split ('\n', string);
    }
    
    public static StringList ofStrings (String ... strings) {
        return (new StringList (Arrays.asList (strings)));
    }
    
    public static StringList split (char delimiter, String string) {
        return StringList.ofStrings (string.split ("[" + delimiter + "]"));
    }
    
    public static StringList split (String regex, String string) {
        return StringList.ofStrings (string.split (regex));
    }
    
    // Split by space.
    public static StringList words (String string) {
        return StringList.split (' ', string);
    }
    
    @Override
    public StringList drop (int n) {
        return super.drop (n, StringList :: new);
    }
    
    @Override
    public StringList dropWhile (Predicate <String> predicate) {
        return super.dropWhile (predicate, StringList :: new);
    }
    
    @Override
    public StringList filter (Predicate <String> predicate) {
        return super.filter (predicate, StringList :: new);
    }
    
    public StringList filterNonBlank () {
        return this.filter (s -> ! s.isBlank ());
    }
    
    public StringList filterNonEmpty () {
        return this.filter (s -> ! s.isEmpty ());
    }
    
    @Override
    public FunctionalList <StringList> group () {
        return super.group (StringList :: new);
    }
    
    @Override
    public FunctionalList <StringList> groupBy (BiPredicate <String, String> predicate) {
        return super.groupBy (predicate, StringList :: new);
    }
    
    @Override
    public <K> FunctionalList <StringList> groupByKey (Function <? super String, ? extends K> keyExtractor) {
        return this.groupByKey (keyExtractor, StringList :: new);
    }
    
    public StringList intersperse (char character) {
        return this.intersperse (String.valueOf (character));
    }
    
    @Override
    public StringList intersperse (String element) {
        return this.intersperse (element, StringList :: new);
    }
    
    // Concatenate.
    public String join () {
        return this.join ("");
    }
    
    public String join (char character) {
        return this.join (String.valueOf (character));
    }
    
    public String join (CharSequence delimiter) {
        return String.join (delimiter, delegate);
    }
    
    // Shall be called before sorting.
    public StringList normalize () {
        return super.mapToString (string -> Normalizer.normalize (string, Normalizer.Form.NFD));
    }
    
    @Override
    public UniformPair <StringList> partition (Predicate <String> predicate) {
        return super.partition (predicate, StringList :: new);
    }
    
    @Override
    public StringList reverse () {
        return super.reverse (StringList :: new);
    }
    
    @Override
    public StringList shuffle () {
        return super.shuffle (StringList :: new);
    }
    
    @Override
    public StringList sortAsc () {
        return super.sortAsc (StringList :: new);
    }
    
    @Override
    public StringList sortBy (Comparator <? super String> comparator) {
        return super.sortBy (comparator, StringList :: new);
    }
    
    @Override
    public StringList sortDesc () {
        return super.sortDesc (StringList :: new);
    }
    
    @Override
    public StringList subList (int fromIndex, int toIndex) {
        return super.subList (fromIndex, toIndex, StringList :: new);
    }
    
    @Override
    public FunctionalList <StringList> sublists () {
        return super.sublistsBy (this :: subList);
    }
    
    @Override
    public FunctionalList <StringList> sublists (int minLength, int maxLength) {
        return super.sublistsBy (minLength, maxLength, this :: subList);
    }
    
    @Override
    public StringList tail () throws IllegalStateException {
        return super.tail (StringList :: new);
    }
    
    @Override
    public StringList take (int n) {
        return super.take (n, StringList :: new);
    }
    
    @Override
    public StringList takeWhile (Predicate <String> predicate) {
        return super.takeWhile (predicate, StringList :: new);
    }
    
    @Override
    public StringList toList () {
        return super.toList (StringList :: new);
    }
    
    @Override
    public StringSet toSet () {
        return super.toSet (StringSet :: new);
    }
    
    public String uncolumns () {
        return this.join ('\t');
    }
    
    public String unlines () {
        return this.join ('\n');
    }
    
    public String unwords () {
        return this.join (' ');
    }
}
