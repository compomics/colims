package com.compomics.colims.model.util;

import org.apache.commons.math.util.MathUtils;

/**
 * This class contains utilities methods for comparisons.
 * <p>
 * Created by Niels Hulstaert on 21/10/15.
 */
public class CompareUtils {

    /**
     * The epsilon value use in comparing double values.
     */
    private static final double DOUBLE_EPSILON = 0.001;

    /**
     * Private constructor to prevent instantiation.
     */
    private CompareUtils() {
    }

    /**
     * Check whether the two Double values are the same, with the DOUBLE_EPSILON value used as maximum difference. This
     * method takes null values into account (both null return true, only one of the arguments null returns false).
     *
     * @param one the first Double
     * @param two the second Double
     * @return equals or not
     */
    public static boolean equals(Double one, Double two) {
        if (one == null) {
            return two == null;
        } else if (two == null) {
            return one == null;
        } else {
            return MathUtils.equals(one, two, DOUBLE_EPSILON);
        }
    }
}
