package com.compomics.colims.distributed.model;

import java.io.Serializable;

/**
 *
 * @author Niels Hulstaert
 */
public class StoredTask extends AbstractMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The storage start timestamp
     */
    private Long startedTimestamp;
    /**
     * The storage end timestamp
     */
    private Long endedTimestamp;
    /**
     * The storage task
     */
    private StorageTask storageTask;

    public StoredTask() {
        startedTimestamp = System.currentTimeMillis();
        endedTimestamp = System.currentTimeMillis();
    }

    public StoredTask(Long startedTimestamp, Long endedTimestamp, StorageTask storageTask) {
        this.startedTimestamp = startedTimestamp;
        this.endedTimestamp = endedTimestamp;
        this.storageTask = storageTask;
    }

    public Long getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Long startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    public Long getEndedTimestamp() {
        return endedTimestamp;
    }

    public void setEndedTimestamp(Long endedTimestamp) {
        this.endedTimestamp = endedTimestamp;
    }

    public StorageTask getStorageTask() {
        return storageTask;
    }

    public void setStorageTask(StorageTask storageTask) {
        this.storageTask = storageTask;
    }

}
