package com.compomics.colims.distributed.model;

import java.util.Objects;

/**
 * An instance of this class is sent after successfully completing a DbTask.
 *
 * @author Niels Hulstaert
 */
public class CompletedDbTask extends QueueMessage {

    private static final long serialVersionUID = -2308086951810097990L;

    /**
     * The storage start timestamp.
     */
    protected Long startedTimestamp;
    /**
     * The storage end timestamp.
     */
    protected Long endedTimestamp;
    /**
     * The db task.
     */
    protected DbTask dbTask;

    /**
     * Constructor.
     */
    public CompletedDbTask() {
        startedTimestamp = System.currentTimeMillis();
        endedTimestamp = System.currentTimeMillis();
    }

    /**
     * Constructor.
     *
     * @param startedTimestamp the start timestamp
     * @param endedTimestamp the end timestamp
     * @param dbTask the DbTask instance
     */
    public CompletedDbTask(final Long startedTimestamp, final Long endedTimestamp, final DbTask dbTask) {
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
