package com.compomics.colims.model.enums;

import com.compomics.util.experiment.identification.SearchParameters;

/**
 *
 * @author Niels Hulstaert
 */
public enum MassAccuracyType {

    PPM, DA;

    /**
     * Get the MassAccuracyType by the Utilities MassAccuracyType.
     *
     * @param utilitiesMassAccuracyType
     * @return
     */
    public static MassAccuracyType getByUtilitiesMassAccuracyType(SearchParameters.MassAccuracyType utilitiesMassAccuracyType) {
        MassAccuracyType massAccuracyType = null;

        if (utilitiesMassAccuracyType.name().equals(PPM.name())) {
            massAccuracyType = PPM;
        } else if (utilitiesMassAccuracyType.name().equals(DA.name())) {
            massAccuracyType = DA;
        }

        return massAccuracyType;
    }
}
