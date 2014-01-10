
package com.compomics.colims.client.event;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class EntityChangeEvent {
    
    public enum Type {

        CREATED, DELETED, UPDATED;                
    }
    
    /**
     * The type of change event
     */
    protected Type type;
    /**
     * Are children collections affected by the change event?
     */
    protected boolean childrenAffected;

    public EntityChangeEvent(final Type type, final boolean childrenAffected) {
        this.type = type;
        this.childrenAffected = childrenAffected;
    }    
    
    public Type getType() {
        return type;
    }        

    public boolean areChildrenAffected() {
        return childrenAffected;
    }        

}
