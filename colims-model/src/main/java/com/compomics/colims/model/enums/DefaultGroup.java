package com.compomics.colims.model.enums;

/**
 *
 * @author Niels Hulstaert
 */
public enum DefaultGroup {
    
    ADMIN("admin"), DISTRIBUTED("distributed");
    
    /**
     * The name of the default group in the database.
     */
    private final String dbEntry;
    
    private DefaultGroup(final String dbEntry){
        this.dbEntry = dbEntry;
    }

    public String dbEntry() {
        return dbEntry;
    }        
    
}
