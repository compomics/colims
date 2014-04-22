package com.compomics.colims.distributed.model;

import com.compomics.colims.distributed.model.enums.PersistType;
import com.compomics.colims.model.Instrument;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Niels Hulstaert
 */
public class PersistMetadata implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The persist type of the task
     */
    private PersistType storageType;
    /**
     * The storage task description
     */
    private String description;
    /**
     * The start date of the run(s)
     */
    private Date startDate;
    /**
     * The instrument the run is executed on
     */
    private Instrument instrument;

    /**
     * Constructor.
     */
    public PersistMetadata() {
    }

    /**
     * Constructor.
     *
     * @param storageType
     * @param description
     * @param startDate
     * @param instrument
     */
    public PersistMetadata(PersistType storageType, String description, Date startDate, Instrument instrument) {
        this.storageType = storageType;
        this.description = description;
        this.startDate = startDate;
        this.instrument = instrument;
    }

    public PersistType getStorageType() {
        return storageType;
    }

    public void setStorageType(PersistType storageType) {
        this.storageType = storageType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.storageType);
        hash = 73 * hash + Objects.hashCode(this.description);
        hash = 73 * hash + Objects.hashCode(this.startDate);
        hash = 73 * hash + Objects.hashCode(this.instrument);
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
        final PersistMetadata other = (PersistMetadata) obj;
        if (this.storageType != other.storageType) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.startDate, other.startDate)) {
            return false;
        }
        if (!Objects.equals(this.instrument, other.instrument)) {
            return false;
        }
        return true;
    }

}
