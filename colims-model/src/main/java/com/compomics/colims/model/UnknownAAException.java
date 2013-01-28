package com.compomics.colims.model;

/**
 * @author Florian Reisinger
 *         Date: 20-Aug-2009
 * @since 0.1
 */
public class UnknownAAException extends Exception {

    public UnknownAAException() {
    }

    public UnknownAAException(String msg) {
        super(msg);
    }

    public UnknownAAException(String msg, Throwable t) {
        super(t);
    }

    public UnknownAAException(Throwable t) {
        super(t);
    }
}
