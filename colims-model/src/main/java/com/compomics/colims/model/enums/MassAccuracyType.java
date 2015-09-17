package com.compomics.colims.model.enums;

import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;

/**
 * This enum contains the different mass accuracy types.
 *
 * @author Niels Hulstaert
 */
public enum MassAccuracyType {

    PPM, DA;

    /**
     * Get the MassAccuracyType by the Utilities MassAccuracyType.
     *
     * @param utilitiesMassAccuracyType the Compomics Utilities mass accuracy type
     * @return the mapped MassAccuracyType instance
     */
    public static MassAccuracyType getByUtilitiesMassAccuracyType(final SearchParameters.MassAccuracyType utilitiesMassAccuracyType) {
        MassAccuracyType massAccuracyType = null;

        if (utilitiesMassAccuracyType.name().equals(PPM.name())) {
            massAccuracyType = PPM;
        } else if (utilitiesMassAccuracyType.name().equals(DA.name())) {
            massAccuracyType = DA;
        }

        return massAccuracyType;
    }
}
