package com.compomics.colims.core.permission;

/**
 * @author Niels Hulstaert
 */
public class PermissionException extends RuntimeException {

    public PermissionException() {
        super();
    }

    public PermissionException(final String message) {
        super(message);
    }

    public PermissionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PermissionException(final Throwable cause) {
        super(cause);
    }

    public PermissionException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
