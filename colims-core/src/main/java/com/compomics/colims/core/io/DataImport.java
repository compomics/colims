package com.compomics.colims.core.io;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;

/**
 * This abstract class is the parent class for all data import types. It has the FastaDb entity as class member, which
 * is common regardless of the import type (PeptideShaker, MaxQuant).
 *
 * @author Niels Hulstaert
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PeptideShakerImport.class, name = "peptideShakerImport"),
        @JsonSubTypes.Type(value = MaxQuantImport.class, name = "maxQuantImport")})
public abstract class DataImport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The fasta DB ID.
     */
    protected Long fastaDbId;

    /**
     * No-arg constructor.
     */
    public DataImport() {
    }

    /**
     * Constructor.
     *
     * @param fastaDbId the FastaDb ID
     */
    public DataImport(final Long fastaDbId) {
        this.fastaDbId = fastaDbId;
    }

    public Long getFastaDbId() {
        return fastaDbId;
    }

    public void setFastaDbId(Long fastaDbId) {
        this.fastaDbId = fastaDbId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataImport that = (DataImport) o;

        return !(fastaDbId != null ? !fastaDbId.equals(that.fastaDbId) : that.fastaDbId != null);

    }

    @Override
    public int hashCode() {
        return fastaDbId != null ? fastaDbId.hashCode() : 0;
    }
}