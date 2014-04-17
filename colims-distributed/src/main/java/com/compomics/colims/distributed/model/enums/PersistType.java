package com.compomics.colims.distributed.model.enums;

/**
 *
 * @author Kenneth
 */
public enum PersistType {

    RESPIN("respin"), PEPTIDESHAKER("PeptideShaker"), MAX_QUANT("MaxQuant");

    /**
     * The user friendly name of the persist type
     */
    private final String userFriendlyName;

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
     * @param userFriendlyName
     * @return the PersistType enum
     */
    public static PersistType getByUserFriendlyName(String userFriendlyName) {
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
