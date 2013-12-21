package com.compomics.colims.model.enums;

/**
 *
 * @author Niels Hulstaert
 */
public enum DefaultRole {
    
    ADMIN("admin");
    
    /**
     * The name of the default role in the database.
     */
    private final String dbEntry;
    
    private DefaultRole(String dbEntry){
        this.dbEntry = dbEntry;
    }

    public String getDbEntry() {
        return dbEntry;
    }        
    
}
