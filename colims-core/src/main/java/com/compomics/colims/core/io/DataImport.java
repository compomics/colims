package com.compomics.colims.core.io;

import com.compomics.colims.model.enums.FastaDbType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

/**
 * This abstract class is the parent class for all data import types. It has the
 * FastaDb entity as class member, which is common regardless of the import type
 * (PeptideShaker, MaxQuant).
 *
 * @author Niels Hulstaert
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PeptideShakerImport.class, name = "peptideShakerImport"),
    @JsonSubTypes.Type(value = MaxQuantImport.class, name = "maxQuantImport")})
public abstract class DataImport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The FASTA database IDs.
     */
    protected EnumMap<FastaDbType, List<Long>> fastaDbIds;

    /**
     * No-arg constructor.
     */
    public DataImport() {
    }

    /**
     * Constructor.
     *
     * @param fastaDbIds the FASTA database IDs map
     */
    public DataImport(final EnumMap<FastaDbType, List<Long>> fastaDbIds) {
        this.fastaDbIds = fastaDbIds;
    }

    public EnumMap<FastaDbType, List<Long>> getFastaDbIds() {
        return fastaDbIds;
    }

    public void setFastaDbIds(EnumMap<FastaDbType, List<Long>> fastaDbIds) {
        this.fastaDbIds = fastaDbIds;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.fastaDbIds);
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
        final DataImport other = (DataImport) obj;
        return Objects.equals(this.fastaDbIds, other.fastaDbIds);
    }

}
