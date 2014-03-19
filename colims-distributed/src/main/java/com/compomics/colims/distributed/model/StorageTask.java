package com.compomics.colims.distributed.model;

import com.compomics.colims.core.io.DataImport;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Niels Hulstaert
 */
public class StorageTask extends AbstractMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The storage metadata
     */
    private StorageMetadata storageMetadata;
    /**
     * The resources necessary for storing
     */
    private DataImport dataImport;

    public StorageTask() {
    }

    public StorageTask(StorageMetadata storageMetadata, DataImport dataImport) {
        this.storageMetadata = storageMetadata;
        this.dataImport = dataImport;
    }

    public StorageMetadata getStorageMetadata() {
        return storageMetadata;
    }

    public void setStorageMetadata(StorageMetadata storageMetadata) {
        this.storageMetadata = storageMetadata;
    }

    public DataImport getDataImport() {
        return dataImport;
    }

    public void setDataImport(DataImport dataImport) {
        this.dataImport = dataImport;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.storageMetadata);
        hash = 79 * hash + Objects.hashCode(this.dataImport);
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
        final StorageTask other = (StorageTask) obj;
        if (!Objects.equals(this.storageMetadata, other.storageMetadata)) {
            return false;
        }
        if (!Objects.equals(this.dataImport, other.dataImport)) {
            return false;
        }
        return true;
    }

}
