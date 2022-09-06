package com.github.josefplch.utils.data;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * The Either type represents values with two possibilities: a value of type
 * Either (a, b) is either Left a or Right b.
 * 
 * The Either type is sometimes used to represent a value which is either
 * correct or an error; by convention, the Left constructor is used to hold
 * an error value and the Right constructor is used to hold a correct value
 * (mnemonic: "right" also means "correct").
 * 
 * @param <L> Type of the left element.
 * @param <R> Type of the right element.
 * 
 * @author  Josef Plch
 * @since   2018-04-26
 * @version 2021-01-12
 */
public class Either <L, R> implements Bifunctor <L, R>, Serializable {
    private final L left;
    private final R right;
    
    protected Either (L left, R right) {
        if (Objects.isNull (left) && Objects.isNull (right)) {
            throw new NullPointerException ("Either left or right value must be specified.");
        }
        else if (! Objects.isNull (left) && ! Objects.isNull (right)) {
            throw new IllegalArgumentException ("Left and right values cannot be specified at the same time.");
        }
        this.left = left;
        this.right = right;
    }
    
    /**
     * Create new Either object wrapping a left value.
     * 
     * @param <L> Type of the left element.
     * @param <R> Type of the right element.
     * @param left The left value.
     * @return New Either object.
     */
    public static <L, R> Either <L, R> left (L left) {
        return new Either <> (left, null);
    }
    
    /**
     * Create new Either object wrapping a right value.
     * 
     * @param <L> Type of the left element.
     * @param <R> Type of the right element.
     * @param right The right value.
     * @return New Either object.
     */
    public static <L, R> Either <L, R> right (R right) {
        return new Either <> (null, right);
    }
    
    @Override
    public <L2, R2> Either <L2, R2> bimap (Function <L, L2> f, Function <R, R2> g) {
        return (
            new Either <> (
                Objects.isNull (left)  ? null : f.apply (left),
                Objects.isNull (right) ? null : g.apply (right)
            )
        );
    }
    
    /**
     * Case analysis for the Either type. If the value is left, apply the first
     * function to left; if it is right, apply the second function to right.
     * 
     * @param <A> Result type.
     * @param f Function to be applied to left.
     * @param g Function to be applied to right.
     * @return Value obtained by either f (left) or g (right).
     */
    public <A> A either (Function <L, A> f, Function <R, A> g) {
        return (Objects.isNull (left) ? g.apply (right) : f.apply (left));
    }
    
    @Override
    public boolean equals (Object object) {
        Boolean result;
        if (object == null || ! (object instanceof Either)) {
            result = false;
        }
        else {
            final Either <?, ?> other = (Either <?, ?>) object;
            result =
                Objects.equals    (this.left,  other.left)
                && Objects.equals (this.right, other.right);
        }
        return result;
    }
    
    /**
     * Return contents of the left value or a default value if either is right.
     * 
     * @param defaultValue The default value, used if either is right.
     * @return The left element or default value.
     */
    public L fromLeft (L defaultValue) {
        return (Objects.isNull (left) ? defaultValue : left);
    }
    
    /**
     * Return contents of the right value or a default value if either is left.
     * 
     * @param defaultValue The default value, used if either is left.
     * @return The right element or default value.
     */
    public R fromRight (R defaultValue) {
        return (Objects.isNull (right) ? defaultValue : right);
    }
    
    /**
     * Get the left element. Throw an exception if the either is right.
     * 
     * @return The right element.
     */
    public L getLeft () throws IllegalStateException {
        if (Objects.isNull (left)) {
            throw new IllegalStateException ("Either is right.");
        }
        else {
            return left;
        }
    }
    
    /**
     * Get the right element. Throw an exception if the either is left.
     * 
     * @return The right element.
     */
    public R getRight () throws IllegalStateException {
        if (Objects.isNull (right)) {
            throw new IllegalStateException ("Either is left.");
        }
        else {
            return right;
        }
    }
    
    @Override
    public int hashCode () {
        int hash = 11;
        hash = 103 * hash + Objects.hashCode (left);
        hash = 103 * hash + Objects.hashCode (right);
        return hash;
    }
    
    /**
     * Does the Either contain a left value?
     * 
     * @return Boolean true/false.
     */
    public boolean isLeft () {
        return (! Objects.isNull (left));
    }
    
    /**
     * Does the Either contain a right value?
     * 
     * @return Boolean true/false.
     */
    public boolean isRight () {
        return (! Objects.isNull (right));
    }
    
    @Override
    public <L2> Either <L2, R> map1 (Function <L, L2> f) {
        return this.bimap (f, Function.identity ());
    }
    
    @Override
    public <R2> Either <L, R2> map2 (Function <R, R2> f) {
        return this.bimap (Function.identity (), f);
    }
    
    /**
     * Get the left element.
     * 
     * @return The left element.
     */
    public Optional <L> safeLeft () throws UnsupportedOperationException {
        return Optional.ofNullable (left);
    }
    
    /**
     * Get the right element.
     * 
     * @return The right element.
     */
    public Optional <R> safeRight () {
        return Optional.ofNullable (right);
    }
    
    @Override
    public String toString () {
        String result;
        if (Objects.isNull (left)) {
            result = "Right " + ObjectUtils.show (right);
        }
        else {
            result = "Left " + ObjectUtils.show (left);
        }
        return result;
    }
}
