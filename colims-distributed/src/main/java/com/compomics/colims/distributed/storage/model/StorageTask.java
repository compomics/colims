package com.compomics.colims.distributed.storage.model;

import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.distributed.storage.model.enums.StorageType;
import com.compomics.colims.model.Sample;
import java.io.Serializable;

/**
 *
 * @author Niels Hulstaert
 */
public class StorageTask implements Serializable {

    public static final String STORAGE_TYPE = "storage_type";
    public static final String SUBMISSION_TIMESTAMP = "submission_timestamp";
    public static final String DESCRIPTION = "description";
    public static final String USER_NAME = "user_name";
    public static final String SAMPLE_NAME = "sample_name";
    
    private static final long serialVersionUID = 1L;
    /**
     * The storage type of the task
     */
    protected StorageType storageType;
    /**
     * The submission timestamp
     */
    protected Long submissionTimestamp;
    /**
     * The storage task description
     */
    protected String description;
    /**
     * The name of the user that submitted the task
     */
    protected String userName;
    /**
     * The sample the run(s) will be added to
     */
    protected Sample sample;
    /**
     * The resources necessary for storing
     */
    protected DataImport dataImport;

    public StorageTask() {
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public DataImport getDataImport() {
        return dataImport;
    }

    public void setDataImport(DataImport dataImport) {
        this.dataImport = dataImport;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getSubmissionTimestamp() {
        return submissionTimestamp;
    }

    public void setSubmissionTimestamp(Long submissionTimestamp) {
        this.submissionTimestamp = submissionTimestamp;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
