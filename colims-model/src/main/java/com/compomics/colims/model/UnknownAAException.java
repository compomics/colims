package com.compomics.colims.model;

/**
 * @author Florian Reisinger
 *         Date: 20-Aug-2009
 * @since 0.1
 */
public class UnknownAAException extends Exception {

    public UnknownAAException() {
    }

    public UnknownAAException(final String msg) {
        super(msg);
    }

    public UnknownAAException(String msg, final Throwable t) {
        super(t);
    }

    public UnknownAAException(final Throwable t) {
        super(t);
    }
}
