package com.compomics.colims.model.enums;

/**
 * This enum contains the default users used in the authorization process.
 *
 * @author Niels Hulstaert
 */
public enum DefaultUser {

    DISTRIBUTED("distributed");

    /**
     * The name of the default user in the database.
     */
    private final String dbEntry;

    /**
     * Constructor.
     *
     * @param dbEntry the entry in the database
     */
    DefaultUser(final String dbEntry) {
        this.dbEntry = dbEntry;
    }

    public String dbEntry() {
        return dbEntry;
    }

}
