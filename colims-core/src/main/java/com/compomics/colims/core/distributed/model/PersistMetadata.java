package com.compomics.colims.core.distributed.model;

import com.compomics.colims.core.distributed.model.enums.PersistType;

import java.io.Serializable;
import java.util.Date;

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
     * The ID of the instrument the run is executed on.
     */
    private Long instrumentId;

    /**
     * Constructor.
     */
    public PersistMetadata() {
    }

    /**
     * Constructor.
     *
     * @param persistType  the PersistType
     * @param description  the description string
     * @param startDate    the start date
     * @param instrumentId the instrument ID
     */
    public PersistMetadata(final PersistType persistType, final String description, final Date startDate, final Long instrumentId) {
        this.persistType = persistType;
        this.description = description;
        this.startDate = startDate != null ? new Date(startDate.getTime()) : null;
        this.instrumentId = instrumentId;
    }

    public PersistType getPersistType() {
        return persistType;
    }

    public void setPersistType(PersistType persistType) {
        this.persistType = persistType;
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

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long instrumentId) {
        this.instrumentId = instrumentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersistMetadata that = (PersistMetadata) o;

        if (persistType != that.persistType) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        return !(instrumentId != null ? !instrumentId.equals(that.instrumentId) : that.instrumentId != null);

    }

    @Override
    public int hashCode() {
        int result = persistType != null ? persistType.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (instrumentId != null ? instrumentId.hashCode() : 0);
        return result;
    }
}