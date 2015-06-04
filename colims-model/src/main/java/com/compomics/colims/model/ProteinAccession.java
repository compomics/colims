package com.compomics.colims.model;

import javax.persistence.*;
import java.util.Objects;

/**
 * This class represents a protein accession entity in the database. This is a separate entity in the database because a
 * protein sequence can be linked to more than one accession.
 *
 * @author Niels Hulstaert
 */
@Table(name = "protein_accession")
@Entity
public class ProteinAccession extends DatabaseEntity {

    private static final long serialVersionUID = 9106133371576907397L;

    /**
     * The protein accession.
     */
    @Basic(optional = false)
    @Column(name = "accession", nullable = false)
    private String accession;
    /**
     * The Protein instance linked to this accession.
     */
    @JoinColumn(name = "l_protein_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Protein protein;

    /**
     * No-arg constructor.
     */
    public ProteinAccession() {
    }

    /**
     * Constructor.
     *
     * @param accession the accession string
     */
    public ProteinAccession(final String accession) {
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
        return Objects.equals(this.accession, other.accession);
    }

    @Override
    public String toString() {
        return accession;
    }

}
