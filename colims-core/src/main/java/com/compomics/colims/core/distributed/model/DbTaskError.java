package com.compomics.colims.core.distributed.model;

/**
 * An instance of this class is sent after an error occurred while storing a DbTask.
 *
 * @author Niels Hulstaert
 */
public class DbTaskError extends CompletedDbTask {

    private static final long serialVersionUID = -1862176468945938652L;

    /**
     * The error class simple class name.
     */
    private String errorClassSimpleName;
    /**
     * The error description.
     */
    private String errorDescription;

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
     * @param endedTimestamp   the end timestamp
     * @param dbTask           the DbTask
     * @param cause            the cause of the error
     */
    public DbTaskError(Long startedTimestamp, Long endedTimestamp, DbTask dbTask, Exception cause) {
        super(startedTimestamp, endedTimestamp, dbTask);
        this.errorClassSimpleName = cause.getClass().getSimpleName();
        this.errorDescription = cause.getMessage();
    }

    public String getErrorClassSimpleName() {
        return errorClassSimpleName;
    }

    public void setErrorClassSimpleName(String errorClassSimpleName) {
        this.errorClassSimpleName = errorClassSimpleName;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DbTaskError that = (DbTaskError) o;

        if (errorClassSimpleName != null ? !errorClassSimpleName.equals(that.errorClassSimpleName) : that.errorClassSimpleName != null)
            return false;
        return !(errorDescription != null ? !errorDescription.equals(that.errorDescription) : that.errorDescription != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (errorClassSimpleName != null ? errorClassSimpleName.hashCode() : 0);
        result = 31 * result + (errorDescription != null ? errorDescription.hashCode() : 0);
        return result;
    }
}
