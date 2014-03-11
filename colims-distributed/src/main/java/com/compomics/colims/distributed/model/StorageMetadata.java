package com.compomics.colims.distributed.model;

import com.compomics.colims.distributed.model.enums.StorageType;
import com.compomics.colims.model.Sample;
import java.io.Serializable;
import java.util.Objects;

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

    public StorageMetadata() {
    }

    public StorageMetadata(StorageType storageType, String description, String userName, Sample sample) {
        this.storageType = storageType;
        this.description = description;
        this.userName = userName;
        this.sample = sample;
        submissionTimestamp = System.currentTimeMillis();
    }

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.storageType);
        hash = 47 * hash + Objects.hashCode(this.submissionTimestamp);
        hash = 47 * hash + Objects.hashCode(this.description);
        hash = 47 * hash + Objects.hashCode(this.userName);
        hash = 47 * hash + Objects.hashCode(this.sample);
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
        final StorageMetadata other = (StorageMetadata) obj;
        if (this.storageType != other.storageType) {
            return false;
        }
        if (!Objects.equals(this.submissionTimestamp, other.submissionTimestamp)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.userName, other.userName)) {
            return false;
        }
        if (!Objects.equals(this.sample, other.sample)) {
            return false;
        }
        return true;
    }

}
