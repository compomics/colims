/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    @JoinColumn(name = "l_protein_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Protein protein;

    public ProteinAccession() {
    }

    public ProteinAccession(String accession) {
        this.accession = accession;
    }
    
    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
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
        hash = 29 * hash + Objects.hashCode(this.accession);
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
        return true;
    }     

    @Override
    public String toString() {
        return accession;
    }

}
