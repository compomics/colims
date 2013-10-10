package com.compomics.colims.client.event;

import javax.swing.JOptionPane;

/**
 *
 * @author Niels Hulstaert
 */
public class DbConstraintMessageEvent extends MessageEvent {

    private static final String CONSTRAINT_MESSAGE = "The entity %s can't be deleted because of a database constraint."
                + " Try to remove any existing relations between this entity and other database entities.";
    
    public DbConstraintMessageEvent(String entityName){
        super("database constraint violation", String.format(CONSTRAINT_MESSAGE, entityName) , JOptionPane.ERROR_MESSAGE);
    }
}
