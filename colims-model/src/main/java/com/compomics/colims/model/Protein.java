/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.compomics.util.protein.Header;
import com.compomics.util.protein.Header.DatabaseType;
import java.util.Objects;
import javax.persistence.Cacheable;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "protein")
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Protein extends DatabaseEntity {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Column(name = "accession", nullable = false)
    private String accession;
    @Lob
    @Basic(optional = false)
    @Column(name = "protein_sequence", nullable = false)
    private String sequence;
    /**
     * the accession and sequence MD5 digest (a 32 character hex string).
     */
    @Basic(optional = false)
    @Column(name = "acc_seq_digest", nullable = false)
    private String accessionSequenceDigest;
    @Basic(optional = true)
    @Enumerated(EnumType.STRING)
    @Column(name = "database_type", nullable = true)
    private Header.DatabaseType databaseType;
    @OneToMany(mappedBy = "protein")
    private List<PeptideHasProtein> peptideHasProteins = new ArrayList<>();
    @OneToMany(mappedBy = "mainGroupProtein")
    private List<PeptideHasProtein> peptideHasMainGroupProteins = new ArrayList<>();

    public Protein() {
    }

    public Protein(String accession, String sequence, String accessionSequenceDigest, DatabaseType databaseType) {
        this.accession = accession;
        this.sequence = sequence;
        this.accessionSequenceDigest = accessionSequenceDigest;
        this.databaseType = databaseType;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getAccessionSequenceDigest() {
        return accessionSequenceDigest;
    }

    public void setAccessionSequenceDigest(String accessionSequenceDigest) {
        this.accessionSequenceDigest = accessionSequenceDigest;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public List<PeptideHasProtein> getPeptideHasProteins() {
        return peptideHasProteins;
    }

    public void setPeptideHasProteins(List<PeptideHasProtein> peptideHasProteins) {
        this.peptideHasProteins = peptideHasProteins;
    }

    public List<PeptideHasProtein> getPeptideHasMainGroupProteins() {
        return peptideHasMainGroupProteins;
    }

    public void setPeptideHasMainGroupProteins(List<PeptideHasProtein> peptideHasMainGroupProteins) {
        this.peptideHasMainGroupProteins = peptideHasMainGroupProteins;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.accession);
        hash = 37 * hash + Objects.hashCode(this.sequence);
        hash = 37 * hash + Objects.hashCode(this.accessionSequenceDigest);
        hash = 37 * hash + Objects.hashCode(this.databaseType);
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
        final Protein other = (Protein) obj;
        if (!Objects.equals(this.accession, other.accession)) {
            return false;
        }
        if (!Objects.equals(this.sequence, other.sequence)) {
            return false;
        }
        if (!Objects.equals(this.accessionSequenceDigest, other.accessionSequenceDigest)) {
            return false;
        }
        if (this.databaseType != other.databaseType) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return accession;
    }

}
