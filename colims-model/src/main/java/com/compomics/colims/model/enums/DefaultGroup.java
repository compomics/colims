package com.compomics.colims.model.enums;

/**
 *
 * @author Niels Hulstaert
 */
public enum DefaultGroup {
    
    ADMIN("admin");
    
    /**
     * The name of the default permission in the database.
     */
    private final String dbEntry;
    
    private DefaultGroup(String dbEntry){
        this.dbEntry = dbEntry;
    }

    public String getDbEntry() {
        return dbEntry;
    }        
    
}
