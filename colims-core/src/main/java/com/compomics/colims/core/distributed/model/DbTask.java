package com.compomics.colims.core.distributed.model;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.Objects;

/**
 * This abstract class holds all common fields for the different DbTask
 * subclasses.
 *
 * @author Niels Hulstaert
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PersistDbTask.class, name = "persistDbTask"),
        @JsonSubTypes.Type(value = DeleteDbTask.class, name = "deleteDbTask")})
public abstract class DbTask extends QueueMessage {

    private static final long serialVersionUID = -3571758804390850866L;

    /**
     * The datatase entity class of the task.
     */
    protected Class dbEntityClass;
    /**
     * The ID of the database entity. In case of a PersistDbTask, this is the ID
     * of the parent entity.
     */
    protected Long enitityId;
    /**
     * The submission timestamp of the database task.
     */
    protected Long submissionTimestamp;
    /**
     * The ID of the user that submitted the task.
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
     * @param dbEntityClass the entity class
     * @param enitityId the entity ID
     * @param userId the user ID
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
        return Objects.equals(this.userId, other.userId);
    }

}
