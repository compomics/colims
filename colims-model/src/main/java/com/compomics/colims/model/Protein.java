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
    @Basic(optional = false)    
    @Enumerated(EnumType.STRING)
    @Column(name = "database_type", nullable = false)
    private Header.DatabaseType databaseType;
    @OneToMany(mappedBy = "protein")
    private List<PeptideHasProtein> peptideHasProteins = new ArrayList<>();
    @OneToMany(mappedBy = "mainGroupProtein")
    private List<PeptideHasProtein> peptideHasMainGroupProteins = new ArrayList<>();

    public Protein() {
    }

    public Protein(String accession, String sequence, DatabaseType databaseType) {
        this.accession = accession;
        this.sequence = sequence;
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
        int hash = 7;
        hash = 67 * hash + (this.accession != null ? this.accession.hashCode() : 0);
        hash = 67 * hash + (this.databaseType != null ? this.databaseType.hashCode() : 0);
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
        if ((this.accession == null) ? (other.accession != null) : !this.accession.equals(other.accession)) {
            return false;
        }
        if ((this.sequence == null) ? (other.sequence != null) : !this.sequence.equals(other.sequence)) {
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
