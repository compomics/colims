package com.compomics.colims.model.enums;

/**
 *
 * @author Niels Hulstaert
 */
public enum DefaultUser {
    
    DISTRIBUTED("distributed");
    
    /**
     * The name of the default user in the database.
     */
    private final String dbEntry;
    
    private DefaultUser(final String dbEntry){
        this.dbEntry = dbEntry;
    }

    public String getDbEntry() {
        return dbEntry;
    }        
    
}
