package com.lingea.utils.io;

import com.github.josefplch.utils.data.string.StringUtils;

/**
 * @author  Josef Plch
 * @since   2019-04-30
 * @version 2019-05-22
 */
public abstract class TextFileUtilsTest {
    public static void main (String [] args) {
        System.out.println (
            StringUtils.breakToLines (
                15,
                "Skákal pes přes oves, přes zelenou louku,"
                + " šel za ním myslivec, péro na klobouku."
            )
            .join ("\n")
        );
    }
}
