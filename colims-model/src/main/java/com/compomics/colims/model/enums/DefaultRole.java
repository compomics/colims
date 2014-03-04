package com.compomics.colims.model.enums;

/**
 *
 * @author Niels Hulstaert
 */
public enum DefaultRole {
    
    ADMIN("admin"), DISTRIBUTED("distributed");
    
    /**
     * The name of the default role in the database.
     */
    private final String dbEntry;
    
    private DefaultRole(final String dbEntry){
        this.dbEntry = dbEntry;
    }

    public String dbEntry() {
        return dbEntry;
    }        
    
}
