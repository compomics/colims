package com.compomics.colims.distributed.model.enums;

/**
 *
 * @author Niels Hulstaert
 */
public enum DbEntityType {

    PROJECT("project"), EXPERIMENT("experiment"), SAMPLE("sample"), ANALYTICAL_RUN("analytical run");

    /**
     * The user friendly name of the delete type
     */
    private final String userFriendlyName;

    private DbEntityType(final String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }

    public String userFriendlyName() {
        return userFriendlyName;
    }

}
