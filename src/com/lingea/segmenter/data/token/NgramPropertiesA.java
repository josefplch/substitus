package com.lingea.segmenter.data.token;

import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.math.fraction.LongFraction;
import com.github.josefplch.utils.data.tuple.UniformPair;
import com.lingea.segmenter.data.ProbabilisticStringSegmentation;
import java.util.Comparator;

/**
 * @author  Josef Plch
 * @since   2019-11-21
 * @version 2021-01-12
 */
public class NgramPropertiesA extends GenericNgramProperties <PairList <ProbabilisticStringSegmentation, Long>> {
    int maxExampes;
    
    public NgramPropertiesA (LongFraction tf, LongFraction iwf, UniformPair <PairList <ProbabilisticStringSegmentation, Long>> examples) {
        super (tf, iwf, examples);
    }
    
    public NgramPropertiesB binarizeExamples () {
        return (
            new NgramPropertiesB (
                e1,
                e2,
                e3.map (examples ->
                    examples.mapToString (example ->
                        example.get1 ().elements ().toString ()
                    )
                )
            )
        );
    }
    
    public static NgramPropertiesA combine (NgramPropertiesA a, NgramPropertiesA b, int maxExamples) {
        UniformPair <PairList <ProbabilisticStringSegmentation, Long>> examples = a.get3 ();
        examples.get1 ().addAll (b.get3 ().get1 ());
        examples.get2 ().addAll (b.get3 ().get2 ());

        return new NgramPropertiesA (
            a.e1.sumElementWise (b.e1),
            a.e2.sumElementWise (b.e2),
            // Order the examples.
            examples.map (list ->
                list
                // cs, 25k / ov (next: al)

                // POS: zprostředkování zprostředkovatelské silvestrovských specializovaných redistribuovány
                // NEG: nespecializovaných garantovanápenze.cz sportovnínoviny.cz nejdiskutovanější finančnínoviny.cz
                // .sortBy (Comparator.comparing (p -> (-1) * p.get1 ().size ()))

                // POS: uplatňovat inkoustové počítačového řetězové ovlivňovat
                // NEG: nonwovens a-znovinky křovinořezy monika.jakoubkova českénoviny.cz
                // .sortBy (Comparator.comparing (p -> p.get1 ().entropy ().arithmeticMean ()))

                // POS: zprostředkovatelské redistribuovány zpřístupňování zaregistrován přeregistrovat
                // NEG: garantovanápenze.cz sportovnínoviny.cz nejdiskutovanější finančnínoviny.cz monika.jakoubkova
                // .sortBy (Comparator.comparing (p -> p.get1 ().entropy ().product ()))

                // POS: ubytování pracovní nového nové nový
                // NEG: cestovní ostrovy neregistrovaný ovšem odpovědět
                // .sortBy (Comparator.comparing (p -> (-1) * p.get2 ()))

                // POS: ubytování pracovní nového nové nový
                // NEG: cestovní neregistrovaný dovolená ovšem odpovědět
                // .sortBy (Comparator.comparing (p -> (p.get1 ().entropy ().arithmeticMean () + 1) / p.get2 ()))

                // POS: ubytování nového nové stravování nový
                // NEG: cestovní neregistrovaný dovolená ovšem odpovědět
                // .sortBy (Comparator.comparing (p -> (p.get1 ().entropy ().arithmeticMean () + 1) / Math.sqrt (p.get2 ())))

                // The best found variant: prefer low entropy and high frequency.
                // POS: aktualizováno pobytové stravování klávesové takový
                // NEG: neregistrovaný dovolená eurovíkendy odpovědět českénoviny.cz
                .sortBy (Comparator.comparing (p -> (p.get1 ().entropy ().arithmeticMean () + 1) / Math.log (p.get2 ())))

                // POS: zaregistrovat stravování klávesové vyplňovat takový
                // NEG: neregistrovaný finančnínoviny.cz eurovíkendy ovlivnit českénoviny.cz
                // .sortBy (Comparator.comparing (p -> (p.get1 ().entropy ().arithmeticMean () + 0.5) / Math.log (p.get2 ())))

                // POS: uplatňovat inkoustové počítačového řetězové ovlivňovat
                // NEG: nonwovens a-znovinky křovinořezy monika.jakoubkova českénoviny.cz
                // .sortBy (Comparator.comparing (p -> (p.get1 ().entropy ().arithmeticMean () + 0.01) / Math.log (p.get2 ())))
                .take (maxExamples)
            )
        );
    }
}
