/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.util;

import java.util.function.Function;

/**
 * Class with utility functions.
 *
 * @author Niels Hulstaert
 */
public class Functions {

    /**
     * Private constructor to prevent initialization.
     */
    private Functions() {
    }

    /**
     * This function replaces a null value with an empty String.
     */
    public static Function<Object, String> replaceNullWithEmptyString = (Object t) -> {
        if (t == null) {
            return "";
        } else {
            return t.toString();
        }
    };

}
