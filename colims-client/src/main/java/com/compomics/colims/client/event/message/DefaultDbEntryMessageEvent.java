package com.compomics.colims.client.event.message;

import javax.swing.JOptionPane;

/**
 *
 * @author Niels Hulstaert
 */
public class DefaultDbEntryMessageEvent extends MessageEvent {

    private static final String DEFAULT_DB_ENTRY_MESSAGE = "The entry '%s' of type '%s' can't be deleted because it is used in colims as a default value.";

    public DefaultDbEntryMessageEvent(final String className, final String entityName) {
        super("default database entry", String.format(DEFAULT_DB_ENTRY_MESSAGE, entityName, className), JOptionPane.WARNING_MESSAGE);
    }
}
