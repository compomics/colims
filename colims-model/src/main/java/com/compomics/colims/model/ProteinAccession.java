/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.compomics.util.protein.Header;
import com.compomics.util.protein.Header.DatabaseType;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "protein_accession")
@Entity
public class ProteinAccession extends DatabaseEntity {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Column(name = "accession", nullable = false)
    private String accession;
    @Column(name = "database_type", nullable = true)
    private Header.DatabaseType databaseType;
    @JoinColumn(name = "l_protein_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Protein protein;

    public ProteinAccession() {
    }

    public ProteinAccession(String accession) {
        this.accession = accession;
    }

    public ProteinAccession(String accession, DatabaseType databaseType) {
        this.accession = accession;
        this.databaseType = databaseType;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public Protein getProtein() {
        return protein;
    }

    public void setProtein(Protein protein) {
        this.protein = protein;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.accession);
        hash = 17 * hash + Objects.hashCode(this.databaseType);
        hash = 17 * hash + Objects.hashCode(this.protein);
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
        final ProteinAccession other = (ProteinAccession) obj;
        if (!Objects.equals(this.accession, other.accession)) {
            return false;
        }
        if (this.databaseType != other.databaseType) {
            return false;
        }
        if (!Objects.equals(this.protein, other.protein)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return accession;
    }

}
