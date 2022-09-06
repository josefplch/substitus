package com.lingea.utils.optimizer;

import com.github.josefplch.utils.data.function.Function5;
import com.github.josefplch.utils.data.tuple.Tuple5;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @param <T4>
 * @param <T5>
 * 
 * @author  Josef Plch
 * @since   2018-05-23
 * @version 2019-05-13
 */
public class MaxOptimizer5 <T1, T2, T3, T4, T5> {
    private static final float MIN_FACTOR = 1.01F;
    private final Set <OptimizedArguments5 <T1, T2, T3, T4, T5>> bestArguments = new HashSet <> ();
    private double bestScore;
    private final Set <OptimizedArguments5 <T1, T2, T3, T4, T5>> evaluated = new HashSet <> ();
    private float factor;
    private final Function5 <T1, T2, T3, T4, T5, Double> optimizedFunction;
    private final Set <OptimizedArguments5 <T1, T2, T3, T4, T5>> toEvaluate = new HashSet <> ();
    
    public MaxOptimizer5 (
        Function5 <T1, T2, T3, T4, T5, Double> optimizedFunction,
        Tuple5 <OptimizedValue <T1>, OptimizedValue <T2>, OptimizedValue <T3>, OptimizedValue <T4>, OptimizedValue <T5>> defaultValues,
        float factor
    ) {
        OptimizedArguments5 <T1, T2, T3, T4, T5> arguments 
            = new OptimizedArguments5 <> (
                defaultValues.get1 (),
                defaultValues.get2 (),
                defaultValues.get3 (),
                defaultValues.get4 (),
                defaultValues.get5 ()
            );
        this.bestArguments.add (arguments);
        this.bestScore = optimizedFunction.curry ().apply (normalize (arguments));
        this.factor = factor;
        this.optimizedFunction = optimizedFunction;
        this.toEvaluate.add (arguments);
    }
    
    private void evaluate (OptimizedArguments5 <T1, T2, T3, T4, T5> arguments) {
        if (! evaluated.contains (arguments)) {
            double score = optimizedFunction.curry ().apply (normalize (arguments));
            if (score >= bestScore) {
                // It is possible to use >= here.
                if (score > bestScore) {
                    bestArguments.clear ();
                    bestScore = score;
                    toEvaluate.add (arguments);
                }
                bestArguments.add (arguments);
            }
        }
    }
    
    public Set <Tuple5 <T1, T2, T3, T4, T5>> getBestArguments () {
        return (
            bestArguments.stream ()
            .map (arguments -> normalize (arguments))
            .collect (Collectors.toSet ())
        );
    }
    
    private static <A> A getFirstElement (Set <A> set) {
        return set.iterator ().next ();
    }
    
    private Tuple5 <T1, T2, T3, T4, T5> normalize (OptimizedArguments5 <T1, T2, T3, T4, T5> arguments) {
        return Tuple5.of (
            arguments.get1 ().value (),
            arguments.get2 ().value (),
            arguments.get3 ().value (),
            arguments.get4 ().value (),
            arguments.get5 ().value ()
        );
    }
    
    public void optimize () {
        step ();
        if (! toEvaluate.isEmpty ()) {
            optimize ();
        }
    }
    
    // 1.50 -> 1.25, etc.
    private static float lower (float factor) {
        return ((factor - 1) / 2 + 1);
    }
    
    private void step () {
        if (toEvaluate.isEmpty ()) {
            throw new IllegalStateException ();
        }
        else {
            OptimizedArguments5 <T1, T2, T3, T4, T5> arguments = getFirstElement (toEvaluate);
            OptimizedValue <T1> a1 = arguments.get1 ();
            OptimizedValue <T2> a2 = arguments.get2 ();
            OptimizedValue <T3> a3 = arguments.get3 ();
            OptimizedValue <T4> a4 = arguments.get4 ();
            OptimizedValue <T5> a5 = arguments.get5 ();
            toEvaluate.remove (arguments);
            
            System.out.print (getFirstElement (bestArguments) + ": " + bestScore + ", to evaluate: " + toEvaluate.size () + " ");
            System.out.print (".");
            evaluate (new OptimizedArguments5 <> (a1.multiply (1 * factor), a2, a3, a4, a5));
            System.out.print (".");
            evaluate (new OptimizedArguments5 <> (a1.multiply (1 / factor), a2, a3, a4, a5));
            System.out.print (".");
            evaluate (new OptimizedArguments5 <> (a1, a2.multiply (1 * factor), a3, a4, a5));
            System.out.print (".");
            evaluate (new OptimizedArguments5 <> (a1, a2.multiply (1 / factor), a3, a4, a5));
            System.out.print (".");
            evaluate (new OptimizedArguments5 <> (a1, a2, a3.multiply (1 * factor), a4, a5));
            System.out.print (".");
            evaluate (new OptimizedArguments5 <> (a1, a2, a3.multiply (1 / factor), a4, a5));
            System.out.print (".");
            evaluate (new OptimizedArguments5 <> (a1, a2, a3, a4.multiply (1 * factor), a5));
            System.out.print (".");
            evaluate (new OptimizedArguments5 <> (a1, a2, a3, a4.multiply (1 / factor), a5));
            System.out.print (".");
            evaluate (new OptimizedArguments5 <> (a1, a2, a3, a4, a5.multiply (1 * factor)));
            System.out.print (".");
            evaluate (new OptimizedArguments5 <> (a1, a2, a3, a4, a5.multiply (1 / factor)));
            System.out.println ();
            
            evaluated.add (arguments);
            
            if (toEvaluate.isEmpty () && lower (factor) >= MIN_FACTOR) {
                factor = lower (factor);
                System.out.println ("No better arguments found, factor set to " + factor);
                evaluated.clear ();
                toEvaluate.add (arguments);
            }
        }
    }
}
