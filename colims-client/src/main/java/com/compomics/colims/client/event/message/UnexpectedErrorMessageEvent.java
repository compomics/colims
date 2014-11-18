package com.compomics.colims.client.event.message;

import javax.swing.JOptionPane;

/**
 *
 * @author Niels Hulstaert
 */
public class UnexpectedErrorMessageEvent extends MessageEvent {

    /**
     * Constructor.
     *
     * @param message the error message
     */
    public UnexpectedErrorMessageEvent(final String message) {
        super("Unexpected Error", "An unexpected error occured: "
                + System.lineSeparator() + message + "."
                + System.lineSeparator() + "Please try to rerun the application.", JOptionPane.ERROR_MESSAGE);
    }
}
