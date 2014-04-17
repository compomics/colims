package com.compomics.colims.distributed.model;

import java.io.Serializable;

/**
 *
 * @author Niels Hulstaert
 */
public class CompletedDbTask extends QueueMessage implements Serializable {

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
    private PersistDbTask storageTask;

    public CompletedDbTask() {
        startedTimestamp = System.currentTimeMillis();
        endedTimestamp = System.currentTimeMillis();
    }

    public CompletedDbTask(Long startedTimestamp, Long endedTimestamp, PersistDbTask storageTask) {
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

    public PersistDbTask getStorageTask() {
        return storageTask;
    }

    public void setStorageTask(PersistDbTask storageTask) {
        this.storageTask = storageTask;
    }

}
