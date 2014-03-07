package com.compomics.colims.distributed.model;

import java.io.Serializable;

/**
 *
 * @author Niels Hulstaert
 */
public class StorageError implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The storage task that caused the error
     */
    private StorageTask storageTask;
    /**
     * The cause of the storage error
     */
    private Exception cause;

    public StorageError(StorageTask storageTask, Exception cause) {
        this.storageTask = storageTask;
        this.cause = cause;
    }

    public StorageTask getStorageTask() {
        return storageTask;
    }

    public void setStorageTask(StorageTask storageTask) {
        this.storageTask = storageTask;
    }

    public Exception getCause() {
        return cause;
    }

    public void setCause(Exception cause) {
        this.cause = cause;
    }

}
