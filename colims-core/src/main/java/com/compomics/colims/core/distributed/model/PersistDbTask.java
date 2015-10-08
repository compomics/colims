package com.compomics.colims.core.distributed.model;

import com.compomics.colims.core.io.DataImport;

import java.util.Objects;

/**
 * This class holds the information necessary for the distributed module for storing data.
 *
 * @author Niels Hulstaert
 */
public class PersistDbTask extends DbTask {

    private static final long serialVersionUID = -3660025176317700432L;

    /**
     * The persist meta data.
     */
    private PersistMetadata persistMetadata;
    /**
     * The resources necessary for storing.
     */
    private DataImport dataImport;

    /**
     * no-arg Constructor.
     */
    public PersistDbTask() {
    }

    /**
     * Constructor.
     *
     * @param dbEntityClass   the database entity class
     * @param enitityId       the ID of the entity to persist
     * @param userId          the ID of the user
     * @param persistMetadata the persist
     * @param dataImport      the DataImport
     */
    public PersistDbTask(Class dbEntityClass, Long enitityId, Long userId, PersistMetadata persistMetadata, DataImport dataImport) {
        super(dbEntityClass, enitityId, userId);
        this.persistMetadata = persistMetadata;
        this.dataImport = dataImport;
    }

    public PersistMetadata getPersistMetadata() {
        return persistMetadata;
    }

    public void setPersistMetadata(PersistMetadata persistMetadata) {
        this.persistMetadata = persistMetadata;
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
        hash = 79 * hash + Objects.hashCode(this.persistMetadata);
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
        final PersistDbTask other = (PersistDbTask) obj;
        if (!Objects.equals(this.persistMetadata, other.persistMetadata)) {
            return false;
        }
        return Objects.equals(this.dataImport, other.dataImport);
    }

}
