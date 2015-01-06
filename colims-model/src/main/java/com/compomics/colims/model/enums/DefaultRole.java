package com.compomics.colims.model.enums;

/**
 * This enum contains the default roles used in the authorization process.
 *
 * @author Niels Hulstaert
 */
public enum DefaultRole {

    ADMIN("admin"), DISTRIBUTED("distributed");

    /**
     * The name of the default role in the database.
     */
    private final String dbEntry;

    /**
     * Constructor.
     *
     * @param dbEntry the entry in the database
     */
    private DefaultRole(final String dbEntry) {
        this.dbEntry = dbEntry;
    }

    public String dbEntry() {
        return dbEntry;
    }

}
