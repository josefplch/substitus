package com.github.josefplch.utils.data.list;

import com.github.josefplch.utils.data.foldable.OperatorFoldable;
import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * @param <A> The type of the operand and result of the operators in the list.
 * 
 * @author  Josef Plch
 * @since   2019-07-22
 * @version 2021-01-08
 */
public class OperatorList <A> extends FunctionalList <UnaryOperator <A>> implements OperatorFoldable <A> {
    public OperatorList () {
        super ();
    }
    
    // Probably more efficient than the (iterable) constructor.
    protected OperatorList (Collection <? extends UnaryOperator <A>> collection) {
        super (collection);
    }
    
    protected OperatorList (Iterable <? extends UnaryOperator <A>> iterable) {
        super (iterable);
    }
}
