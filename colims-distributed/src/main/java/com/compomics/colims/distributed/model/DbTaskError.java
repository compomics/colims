package com.compomics.colims.distributed.model;

import java.util.Objects;

/**
 * An instance of this class is sent after an error occurred while storing a
 * DbTask.
 *
 * @author Niels Hulstaert
 */
public class DbTaskError extends CompletedDbTask {

    private static final long serialVersionUID = -1862176468945938652L;

    /**
     * The cause of the error.
     */
    private Exception cause;

    /**
     * No-arg constructor.
     */
    public DbTaskError() {
        super();
    }

    /**
     * Constructor.
     *
     * @param startedTimestamp the start timestamp
     * @param endedTimestamp the end timestamp
     * @param dbTask the DbTask
     * @param cause the cause of the error
     */
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.cause);
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
        final DbTaskError other = (DbTaskError) obj;
        return Objects.equals(this.cause, other.cause);
    }

}
