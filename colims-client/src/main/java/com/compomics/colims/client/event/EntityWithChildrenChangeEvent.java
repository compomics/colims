
package com.compomics.colims.client.event;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class EntityWithChildrenChangeEvent extends EntityChangeEvent {
       
    /**
     * Are children collections affected by the change event?
     */
    protected boolean childrenAffected;

    public EntityWithChildrenChangeEvent(final Type type, final boolean childrenAffected) {
        super(type);        
        this.childrenAffected = childrenAffected;
    }                  

    public boolean areChildrenAffected() {
        return childrenAffected;
    }        

}
