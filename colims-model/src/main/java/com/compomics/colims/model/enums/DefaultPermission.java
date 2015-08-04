package com.compomics.colims.model.enums;

/**
 * This enum contains the default permissions used in the authorization process.
 *
 * @author Niels Hulstaert
 */
public enum DefaultPermission {

    READ("read"), CREATE("create"), UPDATE("update"), DELETE("delete");

    /**
     * The name of the default permission in the database.
     */
    private final String dbEntry;

    /**
     * Constructor.
     *
     * @param dbEntry the entry in the database
     */
    DefaultPermission(final String dbEntry) {
        this.dbEntry = dbEntry;
    }

    public String dbEntry() {
        return dbEntry;
    }

}
