package com.compomics.colims.distributed.model;

/**
 *
 * @author Niels Hulstaert
 */
public class DbTaskError extends CompletedDbTask {

    /**
     * The cause of the error
     */
    private Exception cause;

    public DbTaskError() {
        super();
    }    
    
    public DbTaskError(Long startedTimestamp, Long endedTimestamp, DbTask dbTask, Exception cause) {
        super(startedTimestamp, endedTimestamp, dbTask);
        this.cause = cause;
    }       

    public Exception getCause() {
        return cause;
    }

    public void setCause(Exception cause) {
        this.cause = cause;
    }

}
