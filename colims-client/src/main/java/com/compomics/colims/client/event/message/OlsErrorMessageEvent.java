package com.compomics.colims.client.event.message;

import javax.swing.JOptionPane;

/**
 *
 * @author Niels Hulstaert
 */
public class OlsErrorMessageEvent extends MessageEvent {

    /**
     * The enum represents the different types of OLS errors.
     */
    public enum OlsError {

        CONNECTION_ERROR("The Ontology Lookup Service (OLS) could not be reached."
                + System.lineSeparator() + System.lineSeparator() + "As a consequence, you can't look up ontology terms."),
        NOT_FOUND_ERROR("The Ontology Lookup Service (OLS) returned a not found error."),
        PARSE_ERROR("There was a problem with parsing the results from the Ontology Lookup Service (OLS).");

        /**
         * The user friendly error message.
         */
        private final String message;

        /**
         * Constructor.
         *
         * @param message the error message
         */
        private OlsError(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

    }

    /**
     * Constructor.
     *
     * @param olsError the OLS error type enum
     */
    public OlsErrorMessageEvent(final OlsError olsError) {
        super("Ontology Lookup Service (OLS) problem", olsError.getMessage(), JOptionPane.ERROR_MESSAGE);
    }
}
