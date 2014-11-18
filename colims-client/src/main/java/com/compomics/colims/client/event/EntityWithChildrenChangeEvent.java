
package com.compomics.colims.client.event;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class EntityWithChildrenChangeEvent extends EntityChangeEvent {
       
    /**
     * Are child collections affected by the change event.
     */
    protected final boolean childrenAffected;

    /**
     * Constructor.
     *
     * @param type the change type
     * @param childrenAffected are the entity child collections affected
     */
    public EntityWithChildrenChangeEvent(final Type type, final boolean childrenAffected) {
        super(type);        
        this.childrenAffected = childrenAffected;
    }                  

    /**
     * Are child collections affected by the change event.
     * 
     * @return whether the entity child collections are affected
     */
    public boolean areChildrenAffected() {
        return childrenAffected;
    }        

}
