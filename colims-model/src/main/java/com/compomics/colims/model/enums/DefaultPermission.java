package com.compomics.colims.model.enums;

/**
 *
 * @author niels
 */
public enum DefaultPermission {
    
    READ("read"), CREATE("create"), UPDATE("update"), DELETE("delete");
    
    /**
     * The name of the default permission in the database.
     */
    private final String dbEntry;
    
    private DefaultPermission(String dbEntry){
        this.dbEntry = dbEntry;
    }

    public String getDbEntry() {
        return dbEntry;
    }        
    
}