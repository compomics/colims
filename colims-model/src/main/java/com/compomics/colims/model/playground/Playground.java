package com.compomics.colims.model.playground;

import com.compomics.colims.model.util.CompareUtils;

/**
 * Created by Niels Hulstaert on 5/10/16.
 */
public class Playground {

    public static void main(String[] args) {
        try {
            System.out.println(CompareUtils.equals(0.2, 0.2));
        } catch (Exception e) {
            e.toString();
        }
    }

}
