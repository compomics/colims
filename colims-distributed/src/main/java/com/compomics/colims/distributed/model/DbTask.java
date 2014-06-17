package com.compomics.colims.distributed.model;

import java.util.Objects;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class DbTask extends QueueMessage {

    /**
     * The datatabe entity class of the task
     */
    protected Class dbEntityClass;
    /**
     * The ID of the database entity. In case of a PersistDbTask, this is the ID
     * of the parent entity.
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
     * @param dbEntityClass
     * @param enitityId
     * @param userId
     */
    public DbTask(Class dbEntityClass, Long enitityId, Long userId) {
        this.dbEntityClass = dbEntityClass;
        this.enitityId = enitityId;
        this.submissionTimestamp = System.currentTimeMillis();
        this.userId = userId;
    }

    public Class getDbEntityClass() {
        return dbEntityClass;
    }

    public void setDbEntityClass(Class dbEntityClass) {
        this.dbEntityClass = dbEntityClass;
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
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.dbEntityClass);
        hash = 79 * hash + Objects.hashCode(this.enitityId);
        hash = 79 * hash + Objects.hashCode(this.submissionTimestamp);
        hash = 79 * hash + Objects.hashCode(this.userId);
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
        if (!Objects.equals(this.dbEntityClass, other.dbEntityClass)) {
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
