package com.github.josefplch.utils.data.list;

import com.github.josefplch.utils.data.string.StringUtils;
import com.github.josefplch.utils.data.tuple.UniformPair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author  Josef Plch
 * @since   2018-05-17
 * @version 2021-01-11
 */
public class CharList extends ComparableList <Character> implements CharSequence {
    public CharList () {
    }
    
    // Probably more efficient than the (iterable) constructor.
    public CharList (Collection <Character> collection) {
        super (collection);
    }
    
    public CharList (Iterable <Character> iterable) {
        super (iterable);
    }
    
    public static CharList fromString (String string) {
        CharList result = new CharList ();
        for (char c : string.toCharArray ()) {
            result.add (c);
        }
        return result;
    }
    
    public static CharList ofCharacters (Character ... characters) {
        return new CharList (Arrays.asList (characters));
    }
    
    public static CharList replicateChar (int n, char character) {
        CharList result = new CharList ();
        result.addN (n, character);
        return result;
    }
    
    @Override
    public char charAt (int index) {
        return super.get (index);
    }
    
    @Override
    public CharList drop (int n) {
        return super.drop (n, CharList :: new);
    }
    
    @Override
    public CharList dropWhile (Predicate <Character> predicate) {
        return super.dropWhile (predicate, CharList :: new);
    }
    
    @Override
    public FunctionalList <CharList> group () {
        return super.group (CharList :: new);
    }
    
    @Override
    public FunctionalList <CharList> groupBy (BiPredicate <Character, Character> predicate) {
        return super.groupBy (predicate, CharList :: new);
    }
    
    @Override
    public <K> FunctionalList <CharList> groupByKey (Function <? super Character, ? extends K> keyExtractor) {
        return this.groupByKey (keyExtractor, CharList :: new);
    }
    
    @Override
    public CharList intersperse (Character element) {
        return this.intersperse (element, CharList :: new);
    }
    
    @Override
    public CharList filter (Predicate <Character> predicate) {
        return super.filter (predicate, CharList :: new);
    }
    
    @Override
    public int length () {
        return super.size ();
    }
    
    public boolean matches​ (String regex) {
        return this.toString ().matches (regex);
    }
    
    @Override
    public UniformPair <CharList> partition (Predicate <Character> predicate) {
        return super.partition (predicate, CharList :: new);
    }
    
    @Override
    public CharList reverse () {
        return super.reverse (CharList :: new);
    }
    
    @Override
    public CharList shuffle () {
        return super.shuffle (CharList :: new);
    }
    
    @Override
    public CharList sortAsc () {
        return super.sortAsc (CharList :: new);
    }
    
    @Override
    public CharList sortBy (Comparator <? super Character> comparator) {
        return super.sortBy (comparator, CharList :: new);
    }
    
    @Override
    public CharList sortDesc () {
        return super.sortDesc (CharList :: new);
    }
    
    @Override
    public CharList subList (int fromIndex, int toIndex) {
        return super.subList (fromIndex, toIndex, CharList :: new);
    }
    
    @Override
    public FunctionalList <CharList> sublists () {
        return super.sublistsBy (this :: subList);
    }
    
    @Override
    public FunctionalList <CharList> sublists (int minLength, int maxLength) {
        return super.sublistsBy (minLength, maxLength, this :: subList);
    }
    
    @Override
    public CharList subSequence (int start, int end) {
        return this.subList (start, end);
    }
    
    /**
     * Get all elements except the first one.
     * 
     * @return The list except the first element.
     * @throws IllegalStateException If the list is empty.
     */
    @Override
    public CharList tail () throws IllegalStateException {
        return super.tail (CharList :: new);
    }
    
    @Override
    public CharList take (int n) {
        return super.take (n, CharList :: new);
    }
    
    @Override
    public CharList takeWhile (Predicate <Character> predicate) {
        return super.takeWhile (predicate, CharList :: new);
    }
    
    public CharList toLower () {
        return super.mapToChar (Character :: toLowerCase);
    }
    
    @Override
    public String toString () {
        return StringUtils.charListToString (delegate);
    }
    
    public CharList toUpper () {
        return super.mapToChar (Character :: toUpperCase);
    }
    
    public CharList trim () {
        return this.trimLeft ().trimRight ();
    }
    
    public CharList trimLeft () {
        return this.dropWhile (Character :: isWhitespace);
    }
    
    public CharList trimRight () {
        return this.reverse ().trimLeft ().reverse ();
    }
}
