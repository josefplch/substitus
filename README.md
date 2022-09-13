# About
Substitus is a computer program for linguistics-based word decomposition. The name is an abbreviation of the phrase substitutive segmenter which references to the used “method of squares” proposed by Joseph Harold Greenberg and independently reinvented and further developed while writing this work. Primarily, the application is designed to segment words (in arbitrary language), but it can handle any sequences. All it needs to work is a frequency dictionary of the processed language (100,000 word forms or more). You will find more details in the thesis (see the official archive, including sample data and source code).

Substitus is implemented in Java 8 as a console application. It is distributed in a single JAR file (less than 1 MB), with no library dependencies.

# Example
```
$ echo "strawberry" | java -jar substitus.jar segmentize-words \
    --frequency-list english.fwl \
    --output-format html \
    --verbosity 1

Segmentation s trawberry:   0.00% ... no evidence
Segmentation s trawberry:   0.00% ... no evidence
Segmentation st rawberry:   0.00% ... no evidence
Segmentation str awberry:  15.65% ... [st-] × [-uck, -eaming, -eam]
Segmentation stra wberry:   2.29% ... [co-, cro-, ro-] × [-tton, -ps, -pped]
Segmentation straw berry:  58.05% ... [black-, blue-, gold-] × [-ford, -man, -son]
Segmentation strawb erry:   1.79% ... [thi-, m-, v-] × [-ale, -oss, -ridge]
Segmentation strawbe rry:   0.32% ... [so-, sha-, co-] × [-ar]
Segmentation strawber ry:   7.72% ... [count-, ma-, ga-] × [-y]
Segmentation strawberr y:  95.10% ... [universit-, part-, compan-] × [-ies, -ies., -ries]

s 0.00 t 0.00 r 0.16 a 0.02 w 0.58 b 0.02 e 0.00 r 0.08 r 0.95 y
straw berr y
```
The output consists of (optional) segmentation details and two results, probabilistic and binarised. The binarised version is just a simple modification of the probabilistic one.

# Frequency word list
In order to learn grammar, Substitus needs a frequency word list. The file consist of pairs frequency – word. The columns may be divided by either spaces or tabs.

## english.fwl
```
582770  the
268766  to
256531  and
…
1169    attention
1168    45
1166    focus
…
1       university-educated
1       W.K.
1       Zhuan
```

# The most productive tokens found by Substitus
Here, productivity is defined as the number of word forms the token appears in, regardless their frequency. That’s why you will not find tokens like “but” here.

For each language, the tokens were extracted from a large web-corpus-based word list of one million most frequent word forms. The examples, shown in tooltips, are sometimes bad (especially for short tokens), but the tokens themselves should be fine.

## Czech
| **Length** | **#1**     | **#2**     | **#3**     | **#4**     | **#5**     |
|-----------:|------------|------------|------------|------------|------------|
|          1 | a          | i          | m          | e          | u          |
|          2 | ov         | ne         | ch         | ho         | ou         |
|          3 | ost        | pro        | pře        | roz        | při        |
|          4 | teln       | www.       | před       | stav       | prav       |
|          5 | proti      | spolu      | super      | jedno      | multi      |
|          6 | instal     | obchod     | znamen     | středo     | inform     |
|          7 | elektro    | kontrol    | program    | registr    | minutov    |
|          8 | comment-   | několika   | kvalifik   | padesáti   | dokument   |
|          9 | procentní  | prezident  | identifik  | administr  | miliardov  |
|         10 | kilometrov | zprostředk | experiment | spravedliv | sedmdesáti |

## English
| **Length** | **#1**     | **#2**     | **#3**     | **#4**     | **#5**     |
|-----------:|------------|------------|------------|------------|------------|
|          1 | s          | e          | n          | t          | a          |
|          2 | er         | ed         | re         | al         | ly         |
|          3 | ing        | ion        | man        | ist        | non        |
|          4 | ness       | .com       | non-       | over       | able       |
|          5 | ation      | based      | anti-      | inter      | ville      |
|          6 | &apos;     | -style     | master     | school     | single     |
|          7 | comment    | related    | million    | counter    | ability    |
|          8 | american   | -looking   | oriented   | friendly   | specific   |
|          9 | christian  | wikipedia  | sponsored  | conscious  | character  |
|         10 | government | controlled | management | washington | california |

## German
| **Length** | **#1**     | **#2**     | **#3**     | **#4**     | **#5**     |
|-----------:|------------|------------|------------|------------|------------|
|          1 | s          | e          | n          | t          | r          |
|          2 | er         | en         | ge         | be         | an         |
|          3 | ung        | ver        | ein        | end        | aus        |
|          4 | isch       | lich       | chef       | über       | berg       |
|          5 | spiel      | recht      | markt      | sport      | ation      |
|          6 | system     | arbeit     | steuer     | schaft     | gruppe     |
|          7 | konzern    | bereich    | politik    | projekt    | technik    |
|          8 | geschäft   | programm   | internet   | sprecher   | zusammen   |
|          9 | industrie  | präsident  | verfahren  | marketing  | transport  |
|         10 | wirtschaft | hersteller | produktion | sicherheit | management |

## Hungarian
| **Length** | **#1**     | **#2**     | **#3**     | **#4**     | **#5**     |
|-----------:|------------|------------|------------|------------|------------|
|          1 | k          | t          | e          | a          | i          |
|          2 | ás         | és         | et         | at         | ra         |
|          3 | nak        | meg        | nek        | ség        | ben        |
|          4 | ként       | kkel       | nagy       | alap       | szak       |
|          5 | össze      | ással      | vezet      | világ      | hitel      |
|          6 | vissza     | ország     | csapat     | szabad     | jelent     |
|          7 | verseny    | csoport    | kormány    | program    | politik    |
|          8 | rendszer   | ingatlan   | fejleszt   | részvény   | tulajdon   |
|          9 | miniszter  | szövetség  | szerződés  | gyógyszer  | fesztivál  |
|         10 | szolgáltat | élelmiszer | társadalom | információ | tulajdonos |
