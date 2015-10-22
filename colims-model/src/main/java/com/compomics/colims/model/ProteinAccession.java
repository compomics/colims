package com.compomics.colims.model;

import javax.persistence.*;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.PERSIST)
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
    public String toString() {
        return accession;
    }

}
