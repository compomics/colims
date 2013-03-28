
package com.compomics.colims.client.event;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class EntityChangeEvent {
    
    public enum Type {

        CREATED, DELETED, UPDATED;                
    }
    
    protected Type type; 

    public Type getType() {
        return type;
    }        

}
