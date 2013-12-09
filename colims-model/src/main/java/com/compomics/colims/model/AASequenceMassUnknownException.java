package com.compomics.colims.model;

/**
 * @author Florian Reisinger
 *         Date: 20-Aug-2009
 * @since 0.1
 */
public class AASequenceMassUnknownException extends Exception {

    public AASequenceMassUnknownException() {
    }

    public AASequenceMassUnknownException(String msg) {
        super(msg);
    }

    public AASequenceMassUnknownException(String msg, Throwable t) {
        super(msg, t);
    }

    public AASequenceMassUnknownException(Throwable t) {
        super(t);
    }

}
