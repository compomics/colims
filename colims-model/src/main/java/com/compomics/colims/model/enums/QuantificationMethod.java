/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model.enums;

/**
 * This enum contains the different quantification methods.
 *
 * @author Niels Hulstaert
 */
public enum QuantificationMethod {

    LABEL_FREE("label free"),
    TMT("TMT"),
    ITRAQ("iTRAQ"),
    SILAC("SILAC"),
    ICAT("ICAT"),
    SRM("SRM");

    /**
     * The user friendly name of the quantification method. This name matches the name of the quantification_mappings
     * entries in the ontology-mapping.json file in colims-core.
     */
    private final String userFriendlyName;

    /**
     * Constructor.
     *
     * @param userFriendlyName the user friendly name
     */
    QuantificationMethod(final String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }

    public String userFriendlyName() {
        return userFriendlyName;
    }

    /**
     * Get the {@link QuantificationMethod} enum value by its user friendly name. Return null if no enum value
     * could be matched.
     *
     * @param userFriendlyName the user friendly name
     * @return the {@link QuantificationMethod} enum value
     */
    public static QuantificationMethod getByUserFriendlyName(final String userFriendlyName) {
        QuantificationMethod quantificationMethod = null;

        //iterate over enum values
        for (QuantificationMethod persistType : values()) {
            if (persistType.userFriendlyName.equals(userFriendlyName)) {
                quantificationMethod = persistType;
            }
        }

        return quantificationMethod;
    }
}
