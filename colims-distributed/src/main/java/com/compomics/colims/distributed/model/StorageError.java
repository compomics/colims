
package com.compomics.colims.distributed.model;

/**
 *
 * @author Niels Hulstaert
 */
public class StorageError {

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
    
}
