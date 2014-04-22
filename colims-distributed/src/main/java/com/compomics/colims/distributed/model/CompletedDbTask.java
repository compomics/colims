package com.compomics.colims.distributed.model;

import java.io.Serializable;
import java.util.Objects;

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
     * The db task
     */
    private DbTask dbTask;

    public CompletedDbTask() {
        startedTimestamp = System.currentTimeMillis();
        endedTimestamp = System.currentTimeMillis();
    }

    public CompletedDbTask(Long startedTimestamp, Long endedTimestamp, DbTask dbTask) {
        this.startedTimestamp = startedTimestamp;
        this.endedTimestamp = endedTimestamp;
        this.dbTask = dbTask;
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

    public DbTask getDbTask() {
        return dbTask;
    }

    public void setDbTask(DbTask dbTask) {
        this.dbTask = dbTask;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.startedTimestamp);
        hash = 97 * hash + Objects.hashCode(this.endedTimestamp);
        hash = 97 * hash + Objects.hashCode(this.dbTask);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CompletedDbTask other = (CompletedDbTask) obj;
        if (!Objects.equals(this.startedTimestamp, other.startedTimestamp)) {
            return false;
        }
        if (!Objects.equals(this.endedTimestamp, other.endedTimestamp)) {
            return false;
        }
        if (!Objects.equals(this.dbTask, other.dbTask)) {
            return false;
        }
        return true;
    }
        
}
