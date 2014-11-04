package com.compomics.colims.core.io;

/**
 * Modification mapping exception, thrown in case of a modification mapping error.
 *
 * @author Niels Hulstaert
 */
public class ModificationMappingException extends MappingException {

    public ModificationMappingException() {
    }

    public ModificationMappingException(String message) {
        super(message);
    }

    public ModificationMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModificationMappingException(Throwable cause) {
        super(cause);
    }

    public ModificationMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
