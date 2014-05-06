package com.compomics.colims.distributed.model;

import com.compomics.colims.distributed.model.enums.DbEntityType;
import java.util.Objects;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class DbTask extends QueueMessage {

    /**
     * The datatabe entity type of the task
     */
    protected DbEntityType dbEntityType;
    /**
     * The ID of the database entity
     */
    private Long enitityId;
    /**
     * The submission timestamp of the database task
     */
    protected Long submissionTimestamp;
    /**
     * The ID of the user that submitted the task
     */
    protected Long userId;

    /**
     * Constructor.
     */
    public DbTask() {
    }

    /**
     * Constructor.
     *
     * @param dbEntityType
     * @param enitityId
     * @param userId
     */
    public DbTask(DbEntityType dbEntityType, Long enitityId, Long userId) {
        this.dbEntityType = dbEntityType;
        this.enitityId = enitityId;
        this.submissionTimestamp = System.currentTimeMillis();
        this.userId = userId;
    }

    public DbEntityType getDbEntityType() {
        return dbEntityType;
    }

    public void setDbEntityType(DbEntityType dbEntityType) {
        this.dbEntityType = dbEntityType;
    }

    public Long getEnitityId() {
        return enitityId;
    }

    public void setEnitityId(Long enitityId) {
        this.enitityId = enitityId;
    }

    public Long getSubmissionTimestamp() {
        return submissionTimestamp;
    }

    public void setSubmissionTimestamp(Long submissionTimestamp) {
        this.submissionTimestamp = submissionTimestamp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.dbEntityType);
        hash = 29 * hash + Objects.hashCode(this.enitityId);
        hash = 29 * hash + Objects.hashCode(this.submissionTimestamp);
        hash = 29 * hash + Objects.hashCode(this.userId);
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
        final DbTask other = (DbTask) obj;
        if (this.dbEntityType != other.dbEntityType) {
            return false;
        }
        if (!Objects.equals(this.enitityId, other.enitityId)) {
            return false;
        }
        if (!Objects.equals(this.submissionTimestamp, other.submissionTimestamp)) {
            return false;
        }
        if (!Objects.equals(this.userId, other.userId)) {
            return false;
        }
        return true;
    }

}
