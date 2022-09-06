package com.lingea.segmenter;

import com.github.josefplch.utils.data.list.PairList;
import com.github.josefplch.utils.data.list.StringList;
import com.github.josefplch.utils.data.tuple.Pair;
import com.lingea.segmenter.data.SimpleStringSegmentation;
import com.lingea.segmenter.data.TestSet;

/**
 * @author  Josef Plch
 * @since   2018-05-24
 * @version 2019-12-01
 */
public class TestData {
    public static final String CS_A =
        "chovat kovat opakovat severovýchod"
        + " nejneobhospodařovávatelnější"
        + " gynekologickoporodnického"
        + " specializované"
        + " Známé sakrální stavby, jako je například bazilika na svatém"
        + " Hostýně, už církvi nepatří.";
    public static final String CS_B =
        "chovat choval chovala nevychovaný"
        + " kovat koval kovala nevykovaný"
        + "stavbyvedoucí stavby krvetvorba";
    public static final String CS_C =
        "houpavou severovietnamský neurověda mikrovlnka světelný budou mohou"
        + " článek prostředí ujme ujde ujasní ujedná";
    public static final String CS_HUMAN_RIGHTS =
        "Všichni lidé se rodí svobodní a sobě rovní co do důstojnosti a práv."
        + " Jsou nadáni rozumem a svědomím a mají spolu jednat v duchu bratrství.";
    // Czech: 20 test words
    public static final StringList CS_MORPHOLOGICAL_DICTIONARY =
        StringList.ofStrings (
            "žadatelka", "nedutnouti", "nedutnout", "turistka", "zálibně", "zakrátko",
            "bandaska", "spjatý", "unikum", "heraldický", "snášenlivě",
            "počínati", "počínat", "rozloučení", "říčka", "sešlápnutý", "kraťas",
            "nahmatati", "nahmatat", "axióm", "překrásně", "medový", "kvapiti", "kvapit"
        );
    public static final StringList CS_PHYSICS =
        StringList.ofStrings (
            "Dnešní vědci málokdy objeví něco podstatného mimo oblast své specializace."
            + " Trojici teoretických fyziků pracujících v chicagském Fermilabu se to povedlo."
            + " Není divu, že nevěřili vlastním očím.",
            "„Pořád čekám, že jednoho dne dostanu mail, kde mi někdo řekne:"
            + " Když se podíváte na ten a ten obskurní článek od Cauchyho, najdete to v třetím dodatku jako poznámku pod čarou,“"
            + " řekl Stephen Parke, vedoucí trojice, pro webový vědecký časopis Quanta Magazine."
        );
    public static final String CS_SKAKAL_PES =
        "Skákal pes přes oves, přes zelenou louku, šel za ním myslivec, péro na klobouku.";
    public static final String DE_HUMAN_RIGHTS =
        "Alle Menschen sind frei und gleich an Würde und Rechten geboren."
        + " Sie sind mit Vernunft und Gewissen begabt und sollen einander"
        + " im Geist der Brüderlichkeit begegnen.";
    public static final String DE_COMPOUND_A =
        "Donaudampfschiffahrtselektrizitätenhauptbetriebswerkbauunterbeamtengesellschaft";
    public static final String DE_COMPOUND_B =
        "Rindfleischetikettierungsüberwachungsaufgabenübertragungsgesetz";
    public static final String DE_COMPOUND_C =
        "Bundespräsidentenstichwahlwiederholungsverschiebung";
    public static final String EN_HEAD_22 =
        "This idiom comes from Joseph Heller’s novel of the same name. In the"
        + " book, an army psychiatrist uses the term to explain the regulations"
        + " that made it impossible for pilots to be excused from flying"
        + " dangerous missions. The pilots need to be declared insane in order"
        + " to be excused from service, but any pilot who wants to be excused"
        + " from these harrowing (troubling and distressing) missions must be"
        + " sane. So there’s no escape.";
    public static final String EN_HUMAN_RIGHTS =
        "All human beings are born free and equal in dignity and rights. They"
        + " are endowed with reason and conscience and should act towards one"
        + " another in a spirit of brotherhood.";
    public static final String HU_HUMAN_RIGHTS =
        "Minden emberi lény szabadon születik és egyenlő méltósága és joga van."
        + " Az emberek, ésszel és lelkiismerettel bírván, egymással szemben"
        + " testvéri szellemben kell hogy viseltessenek.";
    // Slovak: 20 test words
    public static final StringList SK_MORPHOLOGICAL_DICTIONARY =
        StringList.ofStrings (
            "zatínať", "kvantový", "domodra", "známosť", "pietny",
            "cenzúra", "furunklový", "rižský", "postsocialistický", "tadiaľ",
            "zrumenieť", "lombardský", "prasacina", "medziplanetárny", "škrobárka",
            "potentát", "pravičiar", "kmitočet", "zabudnutie", "imatrikulovať"
        );
    
    // Version 2018-09-24.
    @Deprecated
    public static TestSet getCsText () {
        String text =
            "V po=sluch=árn-ě 3-. lék=ař-sk-é fakult-y v Pra-ze urč=en-é pro"
            + " dvacet lidí se tísn-í skor=o padesát stud=ent-ů medicín-y-."
            + " Míst=n=ost-i m=ají nízk-é strop-y a stud=ent-i si po-znám-k-y"
            + " píš-í na kolen-ou-. „-Kdy=ž sed-í-te v tak-ov-é po=sl=uch=árn-ě"
            + " pět hodin-, s=těž=í se sou-střed-í-te na vý=uk-u-,-“ říká"
            + " stud=ent třetí-ho ro=ční=k-u Nikol=a=s Petr-ov-. Zoufalá je"
            + " podl-e medi=k-ů i samot-n-á vý=uk-a stud=ent-ů-. „-Vedou ji"
            + " lék=ař-i-, kte-ří zároveň sl=ouž-í sl=už=b-y-. Tak=že lék=ař"
            + " psa-n-ý v ambulan-c-i si od-sko=č-í na dv-ě hodin-y u=č-i-t a"
            + " pak se vr=át-í do ambulan=c-e-,-“ řík-á Petr-ov-.-";
        return (
            preprocessData (
                StringList.split (" ", text)
                .mapToPair (word -> Pair.of (word.replaceAll ("[-=]", ""), word))
            )
        );
    }
    
    private static TestSet preprocessData (PairList <String, String> entries) {
        return (
            new TestSet (
                entries.mapToPair (entry ->
                    entry.map2 (correctSegmentation ->
                        SimpleStringSegmentation.read (
                            correctSegmentation.replace (
                                "-",
                                GlobalSettings.HARD_DELIMITER_STRING
                            )
                        )
                    )
                )
            )
        );
    }
}
