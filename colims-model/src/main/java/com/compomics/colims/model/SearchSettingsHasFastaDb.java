package com.compomics.colims.model;

import com.compomics.colims.model.enums.FastaDbType;
import java.util.Objects;

import javax.persistence.*;

/**
 * @author Niels Hulstaert
 */
@Table(name = "search_settings_has_fasta_db")
@Entity
public class SearchSettingsHasFastaDb extends DatabaseEntity {

    private static final long serialVersionUID = 659064989295992898L;

    /**
     * The fasta database type.
     */
    @Basic(optional = false)
    @Column(name = "fasta_db_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private FastaDbType fastaDbType;
    /**
     * The SearchParameters instance of this join entity.
     */
    @JoinColumn(name = "l_search_and_val_settings_id", referencedColumnName = "id")
    @ManyToOne
    private SearchAndValidationSettings searchAndValidationSettings;
    /**
     * The FastaDb instance of this join entity.
     */
    @JoinColumn(name = "l_fasta_db_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private FastaDb fastaDb;

    /**
     * No-arg constructor.
     */
    public SearchSettingsHasFastaDb() {
    }

    /**
     * Constructor
     *
     * @param fastaDbType the FASTA database type
     * @param searchAndValidationSettings the SeaSearchAndValidationSettings instance
     * @param fastaDb the FASTA database
     */
    public SearchSettingsHasFastaDb(FastaDbType fastaDbType, SearchAndValidationSettings searchAndValidationSettings, FastaDb fastaDb) {
        this.fastaDbType = fastaDbType;
        this.searchAndValidationSettings = searchAndValidationSettings;
        this.fastaDb = fastaDb;
    }

    public FastaDbType getFastaDbType() {
        return fastaDbType;
    }

    public void setFastaDbType(FastaDbType fastaDbType) {
        this.fastaDbType = fastaDbType;
    }

    public SearchAndValidationSettings getSearchAndValidationSettings() {
        return searchAndValidationSettings;
    }

    public void setSearchAndValidationSettings(SearchAndValidationSettings searchAndValidationSettings) {
        this.searchAndValidationSettings = searchAndValidationSettings;
    }

    public FastaDb getFastaDb() {
        return fastaDb;
    }

    public void setFastaDb(FastaDb fastaDb) {
        this.fastaDb = fastaDb;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.fastaDbType);
        hash = 37 * hash + Objects.hashCode(this.searchAndValidationSettings);
        hash = 37 * hash + Objects.hashCode(this.fastaDb);
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
        final SearchSettingsHasFastaDb other = (SearchSettingsHasFastaDb) obj;
        if (this.fastaDbType != other.fastaDbType) {
            return false;
        }
        if (!Objects.equals(this.searchAndValidationSettings, other.searchAndValidationSettings)) {
            return false;
        }
        if (!Objects.equals(this.fastaDb, other.fastaDb)) {
            return false;
        }
        return true;
    }

}
