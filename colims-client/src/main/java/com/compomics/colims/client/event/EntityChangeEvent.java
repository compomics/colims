
package com.compomics.colims.client.event;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class EntityChangeEvent {
    
    /**
     *
     */
    public enum Type {

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
        UPDATED,

        /**
         * One or more runs have been added.
         */
        RUNS_ADDED
    }
    
    /**
     * The type of change event.
     */
    protected final Type type;

    /**
     * Constructor.
     *
     * @param type the change type
     */
    public EntityChangeEvent(final Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }             

}
