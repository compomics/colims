package com.compomics.colims.distributed.model;

import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.distributed.model.enums.DbEntityType;
import java.util.Objects;

/**
 *
 * @author Niels Hulstaert
 */
public class PersistDbTask extends DbTask {

    /**
     * The persist metadata
     */
    private PersistMetadata persistMetadata;
    /**
     * The resources necessary for storing
     */
    private DataImport dataImport;

    /**
     * Constructor
     */
    public PersistDbTask() {
        super();
    }            
    
    /**
     * Constructor
     * 
     * @param dbEntityType
     * @param enitityId
     * @param userId
     * @param persistMetadata
     * @param dataImport
     */
    public PersistDbTask(DbEntityType dbEntityType, Long enitityId, Long userId, PersistMetadata persistMetadata, DataImport dataImport) {
        super(dbEntityType, enitityId, userId);
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
        if (!Objects.equals(this.dataImport, other.dataImport)) {
            return false;
        }
        return true;
    }

}
