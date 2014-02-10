package com.compomics.colims.model.enums;

/**
 *
 * @author Niels Hulstaert
 */
public enum DefaultPermission {
    
    READ("read"), CREATE("create"), UPDATE("update"), DELETE("delete");
    
    /**
     * The name of the default permission in the database.
     */
    private final String dbEntry;
    
    private DefaultPermission(final String dbEntry){
        this.dbEntry = dbEntry;
    }

    public String dbEntry() {
        return dbEntry;
    }        
    
}
