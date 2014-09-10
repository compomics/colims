package com.compomics.colims.distributed.model;

import java.util.Objects;

/**
 *
 * @author Niels Hulstaert
 */
public class DbTaskError extends CompletedDbTask {

    private static final long serialVersionUID = -1862176468945938652L;
    
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
        if (!Objects.equals(this.cause, other.cause)) {
            return false;
        }
        return true;
    }
        
}
