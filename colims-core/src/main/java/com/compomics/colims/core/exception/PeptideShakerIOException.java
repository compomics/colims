
package com.compomics.colims.core.exception;

/**
 * Wrapper exception for PeptideShaker related exceptions
 * 
 * @author Niels Hulstaert
 */
public class PeptideShakerIOException extends Exception {
        
    public PeptideShakerIOException() {
    }

    public PeptideShakerIOException(final String message) {
        super(message);
    }

    public PeptideShakerIOException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PeptideShakerIOException(final Throwable cause) {
        super(cause);
    }

    public PeptideShakerIOException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
        
}
