package com.compomics.colims.client.event.message;

import javax.swing.JOptionPane;

/**
 *
 * @author Niels Hulstaert
 */
public class UnexpectedErrorMessageEvent extends MessageEvent {

    public UnexpectedErrorMessageEvent(final String message) {
        super("Unexpected Error", "An unexpected error occured: "
                + "\n" + message + "."
                + "\n" + "Please try to rerun the application.", JOptionPane.ERROR_MESSAGE);
    }
}
