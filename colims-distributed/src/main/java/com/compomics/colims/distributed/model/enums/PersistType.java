package com.compomics.colims.distributed.model.enums;

/**
 * This enum contains the different persist data types.
 *
 * @author Niels Hulstaert
 */
public enum PersistType {

    RESPIN("respin"), PEPTIDESHAKER("PeptideShaker"), MAX_QUANT("MaxQuant");

    /**
     * The user friendly name of the persist type.
     */
    private final String userFriendlyName;

    /**
     * Constructor.
     *
     * @param userFriendlyName the user friendly name
     */
    private PersistType(final String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }

    public String userFriendlyName() {
        return userFriendlyName;
    }

    /**
     * Get the PersistType enum by its user friendly name. Return null if no
     * enum value could be matched.
     *
     * @param userFriendlyName the user friendly name
     * @return the PersistType enum value
     */
    public static PersistType getByUserFriendlyName(final String userFriendlyName) {
        PersistType foundPersistType = null;

        //iterate over enum values
        for (PersistType persistType : values()) {
            if (persistType.userFriendlyName.equals(userFriendlyName)) {
                foundPersistType = persistType;
            }
        }

        return foundPersistType;
    }

}
