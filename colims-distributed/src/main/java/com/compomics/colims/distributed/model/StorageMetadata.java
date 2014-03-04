
package com.compomics.colims.distributed.model;

import com.compomics.colims.distributed.model.enums.StorageType;
import com.compomics.colims.model.Sample;
import java.io.Serializable;

/**
 *
 * @author Niels Hulstaert
 */
public class StorageMetadata implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * The storage type of the task
     */
    private StorageType storageType;
    /**
     * The submission timestamp
     */
    private Long submissionTimestamp;
    /**
     * The storage task description
     */
    private String description;
    /**
     * The name of the user that submitted the task
     */
    private String userName;
    /**
     * The sample the run(s) will be added to
     */
    private Sample sample;

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public Long getSubmissionTimestamp() {
        return submissionTimestamp;
    }

    public void setSubmissionTimestamp(Long submissionTimestamp) {
        this.submissionTimestamp = submissionTimestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }        

}
