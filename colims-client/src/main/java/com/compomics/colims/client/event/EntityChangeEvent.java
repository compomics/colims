
package com.compomics.colims.client.event;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class EntityChangeEvent {
    
    public static enum Type {

        CREATED, DELETED, UPDATED;                
    }
    
    /**
     * The type of change event
     */
    protected Type type;

    public EntityChangeEvent(final Type type) {
        this.type = type;
    }    
    
    public Type getType() {
        return type;
    }             

}
