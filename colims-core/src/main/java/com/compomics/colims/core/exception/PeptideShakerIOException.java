
package com.compomics.colims.core.exception;

/**
 * Wrapper exception for PeptideShaker related exceptions
 * 
 * @author Niels Hulstaert
 */
public class PeptideShakerIOException extends Exception {
        
    public PeptideShakerIOException() {
    }

    public PeptideShakerIOException(String message) {
        super(message);
    }

    public PeptideShakerIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public PeptideShakerIOException(Throwable cause) {
        super(cause);
    }

    public PeptideShakerIOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
        
}
