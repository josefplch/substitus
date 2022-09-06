package com.github.josefplch.utils.data.list;

import com.github.josefplch.utils.data.Bifunctor;
import com.github.josefplch.utils.data.function.Function3;
import com.github.josefplch.utils.data.tuple.Pair;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Glued list is list of elements interspersed with glues:
 * [element1, glue1, element2, glue2, ..., elementN]
 * 
 * There is always at least one element and |glues| = |elements| - 1.
 * 
 * The implementation is similar to linked list.
 * 
 * @param <E> Type of the elements.
 * @param <G> Type of the "glue" connecting the elements.
 * 
 * @author  Josef Plch
 * @since   2019-04-12
 * @version 2019-11-16
 */
public class GluedList <E, G> implements Bifunctor <E, G>, Serializable {
    // The element is never null.
    protected E element;
    // It is not possible to use Optional <Pair <G, GluedList <E, G>>> due to
    // inheritance issues. Both glue and next may be null (at the same time).
    protected G glue;
    protected GluedList <E, G> next;
    
    public GluedList (E element) {
        this (element, null, null);
    }
    
    protected GluedList (E element, G glue, GluedList <E, G> next) {
        Objects.requireNonNull (element);
        this.element = element;
        this.glue = glue;
        this.next = next;
    }
    
    public void addFirst (E element, G glue) {
        Objects.requireNonNull (element);
        Objects.requireNonNull (glue);
        
        E oldElement = this.element;
        if (this.isLast ()) {
            this.element = element;
            this.glue = glue;
            this.next = new GluedList <> (oldElement);
        }
        else {
            G oldGlue = this.glue;
            this.element = element;
            this.glue = glue;
            next.addFirst (oldElement, oldGlue);
        }
    }
    
    public void addLast (G glue, E element) {
        Objects.requireNonNull (glue);
        Objects.requireNonNull (element);
        
        if (this.isLast ()) {
            this.glue = glue;
            this.next = new GluedList <> (element);
        }
        else {
            next.addLast (glue, element);
        }
    }
    
    @Override
    public <A2, G2> GluedList <A2, G2> bimap (Function <E, A2> af, Function <G, G2> gf) {
        return this.bimap (af, gf, GluedList :: new);
    }
    
    protected <A2, G2, S extends GluedList <A2, G2>> S bimap (
        Function <E, A2> af,
        Function <G, G2> gf,
        Function3 <A2, G2, S, S> constructor
    ) {
        return (
            this.isLast ()
            ? constructor.apply (af.apply (element), null, null)
            : constructor.apply (
                af.apply (element),
                gf.apply (glue),
                next.bimap (af, gf, constructor)
            )
        );
    }
    
    public FunctionalList <E> elements () {
        return this.elements (FunctionalList :: new);
    }
    
    protected <LE extends FunctionalList <E>> LE elements (Supplier <LE> constructor) {
        LE result = constructor.get ();
        GluedList <E, G> pointer = this;
        while (pointer.hasNext ()) {
            result.add (pointer.element);
            pointer = pointer.next;
        }
        result.add (pointer.element);
        return result;
    }

    @Override
    public boolean equals (Object object) {
        boolean result;
        if (this == object) {
            result = true;
        }
        else if (object == null || ! (object instanceof GluedList)) {
            result = false;
        }
        else {
            final GluedList <?, ?> other = (GluedList <?, ?>) object;
            result =
                Objects.equals    (this.element, other.element)
                && Objects.equals (this.glue,    other.glue)
                && Objects.equals (this.next,    other.next);
        }
        return result;
    }
    
    public E getElement () {
        return element;
    }
    
    public Optional <Pair <G, GluedList <E, G>>> getNext () {
        return (next == null) ? Optional.empty () : Optional.of (Pair.of (glue, next));
    }
    
    public FunctionalList <G> glues () {
        return this.glues (FunctionalList :: new);
    }
    
    public <LG extends FunctionalList <G>> LG glues (Supplier <LG> constructor) {
        LG result = constructor.get ();
        GluedList <E, G> pointer = this;
        while (pointer.hasNext ()) {
            result.add (pointer.glue);
            pointer = pointer.next;
        }
        return result;
    }
    
    @Override
    public int hashCode () {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode (this.element);
        hash = 71 * hash + Objects.hashCode (this.glue);
        hash = 71 * hash + Objects.hashCode (this.next);
        return hash;
    }
    
    public boolean hasNext () {
        return (next != null);
    }
    
    public boolean isLast () {
        return (next == null);
    }
    
    @Override
    public <B> Bifunctor <B, G> map1 (Function <E, B> f) {
        return this.bimap (f, g -> g);
    }
    
    @Override
    public <H> Bifunctor <E, H> map2 (Function <G, H> f) {
        return this.bimap (e -> e, f);
    }
    
    public void setElement (E element) {
        this.element = element;
    }
    
    public int size () {
        int result = 1;
        GluedList <E, G> pointer = this;
        while (pointer.hasNext ()) {
            result++;
            pointer = pointer.next;
        }
        return result;
    }
    
    @Override
    public String toString () {
        return (element + (this.isLast () ? "" : (", " + glue + ", " + next)));
    }
    
    public String toString (
        Function <E, String> elementFunction,
        Function <G, String> glueFunction
    ) {
        StringBuilder result = new StringBuilder ();
        result.append (elementFunction.apply (element));
        if (this.hasNext ()) {
            result.append (glueFunction.apply (glue));
            result.append (next.toString (elementFunction, glueFunction));
        }
        return result.toString ();
    }
}
