package com.compomics.colims.client.event;

import javax.swing.JOptionPane;

/**
 *
 * @author Niels Hulstaert
 */
public class DbConstraintMessageEvent extends MessageEvent {

    private static final String CONSTRAINT_MESSAGE = "The entry '%s' can't be deleted because of a database constraint;"
                + "\n" + "it is being used in a relation between the '%s' database table and an another one."
                + "\n" + "Remove any existing relations between this entry and other entries and try again.";
    
    public DbConstraintMessageEvent(String className, String entityName){
        super("database constraint violation", String.format(CONSTRAINT_MESSAGE, entityName, className) , JOptionPane.WARNING_MESSAGE);
    }
}
