package com.compomics.colims.core.distributed.model;

import com.compomics.colims.core.distributed.model.enums.PersistType;
import com.compomics.colims.model.Instrument;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * This class represents the persist meta data of a PersistDbTask.
 *
 * @author Niels Hulstaert
 */
public class PersistMetadata implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The persist type of the task.
     */
    private PersistType persistType;
    /**
     * The storage task description.
     */
    private String description;
    /**
     * The start date of the run(s).
     */
    private Date startDate;
    /**
     * The instrument the run is executed on.
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
     * @param persistType the PersistType
     * @param description the description string
     * @param startDate the start date
     * @param instrument the instrument
     */
    public PersistMetadata(final PersistType persistType, final String description, final Date startDate, final Instrument instrument) {
        this.persistType = persistType;
        this.description = description;
        this.startDate = startDate != null ? new Date(startDate.getTime()) : null;
        this.instrument = instrument;
    }

    public PersistType getStorageType() {
        return persistType;
    }

    public void setStorageType(PersistType storageType) {
        this.persistType = storageType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return new Date(startDate.getTime());
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate != null ? new Date(startDate.getTime()) : null;
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
        hash = 73 * hash + Objects.hashCode(this.persistType);
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
        if (this.persistType != other.persistType) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.startDate, other.startDate)) {
            return false;
        }
        return Objects.equals(this.instrument, other.instrument);
    }

}
