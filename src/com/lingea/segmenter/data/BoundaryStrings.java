package com.lingea.segmenter.data;

/**
 * @author  Josef Plch
 * @since   2018-05-09
 * @version 2019-02-28
 */
public enum BoundaryStrings {
    NONE    ("none",  "",    ""),
    INITIAL ("init",  "<w>", ""),
    FINAL   ("final", "",    "</w>"),
    BOTH    ("both",  "<w>", "</w>");
    private final String finalString;
    private final String initialString;
    private final String name;
    
    private BoundaryStrings (String name, String initialString, String finalString) {
        this.finalString = finalString;
        this.initialString = initialString;
        this.name = name;
    }
    
    public String getFinal () {
        return finalString;
    }
    
    public String getInitial () {
        return initialString;
    }
    
    public String getName () {
        return name;
    }
    
    public String wrap (String string) {
        return (initialString + string + finalString);
    }
}
