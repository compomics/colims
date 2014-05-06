package com.compomics.colims.distributed.model;

/**
 *
 * @author Niels Hulstaert
 */
public class DbTaskError extends QueueMessage {

    /**
     * The db task that caused the error
     */
    private DbTask dbTask;
    /**
     * The cause of the error
     */
    private Exception cause;

    public DbTaskError(DbTask dbTask, Exception cause) {
        this.dbTask = dbTask;
        this.cause = cause;
    }

    public DbTask getDbTask() {
        return dbTask;
    }

    public void setDbTask(DbTask dbTask) {
        this.dbTask = dbTask;
    }    

    public Exception getCause() {
        return cause;
    }

    public void setCause(Exception cause) {
        this.cause = cause;
    }

}
