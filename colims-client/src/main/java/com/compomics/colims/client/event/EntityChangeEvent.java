
package com.compomics.colims.client.event;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class EntityChangeEvent {
    
    /**
     *
     */
    public static enum Type {

        /**
         * Created entity event.
         */
        CREATED,                

        /**
         * Deleted entity event.
         */
        DELETED,                

        /**
         * Updated entity event.
         */
        UPDATED;                
    }
    
    /**
     * The type of change event
     */
    protected Type type;

    /**
     *
     * @param type
     */
    public EntityChangeEvent(final Type type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public Type getType() {
        return type;
    }             

}
