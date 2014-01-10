package com.compomics.colims.core.exception;

/**
 * Mapping exception, thrown in case of a mapping error.
 *
 * @author Niels Hulstaert
 */
public class MappingException extends Exception {

    public MappingException() {
        super();
    }

    public MappingException(final String message) {
        super(message);
    }

    public MappingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MappingException(final Throwable cause) {
        super(cause);
    }

    public MappingException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
