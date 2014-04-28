package com.compomics.colims.client.event.message;

import javax.swing.JOptionPane;

/**
 *
 * @author Niels Hulstaert
 */
public class DbConstraintMessageEvent extends MessageEvent {

    private static final String CONSTRAINT_MESSAGE = "The entry '%s' can't be deleted because of a database constraint;"
            + "\n" + "it is being used in a relation between the '%s' database table and another one."
            + "\n" + "Remove any existing relations between this entry and other entries and try again.";

    /**
     *
     * @param className
     * @param entityName
     */
    public DbConstraintMessageEvent(final String className, final String entityName) {
        super("database constraint violation", String.format(CONSTRAINT_MESSAGE, entityName, className), JOptionPane.WARNING_MESSAGE);
    }
}
