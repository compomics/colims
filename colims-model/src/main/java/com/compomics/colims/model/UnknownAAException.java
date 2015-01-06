package com.compomics.colims.model;

/**
 * @author Florian Reisinger
 *         Date: 20-Aug-2009
 * @since 0.1
 */
public class UnknownAAException extends Exception {

    /**
     * no-arg constructor.
     */
    public UnknownAAException() {
    }

    /**
     * Constructor.
     *
     * @param msg the message string
     */
    public UnknownAAException(final String msg) {
        super(msg);
    }

    /**
     * Constructor.
     *
     * @param msg the message string
     * @param t the Throwable instance
     */
    public UnknownAAException(String msg, final Throwable t) {
        super(t);
    }

    /**
     * Constructor.
     *
     * @param t the Throwable instance
     */
    public UnknownAAException(final Throwable t) {
        super(t);
    }
}
