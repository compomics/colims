package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.Objects;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * This class represents a protein entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "protein")
@Entity
//@Indexed
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Protein extends DatabaseEntity {

    private static final long serialVersionUID = -8217759222711303528L;

    /**
     * The protein sequence.
     */
    @Lob
    @Basic(optional = false)
    @Column(name = "protein_sequence", nullable = false)
//    @Field(index=Index.YES, analyze=Analyze.NO, store=Store.NO)
    private String sequence;
    /**
     * The PeptideHasProtein instances from the join table between the peptide
     * and protein tables.
     */
    @OneToMany(mappedBy = "protein")
    private List<PeptideHasProtein> peptideHasProteins = new ArrayList<>();
    /**
     * The PeptideHasProtein instances from the join table between the peptide
     * and protein tables. This list contains all join table entries where this
     * protein is the main group protein.
     */
    @OneToMany(mappedBy = "mainGroupProtein")
    private List<PeptideHasProtein> peptideHasMainGroupProteins = new ArrayList<>();
    /**
     * The list of protein accessions linked to this protein.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "protein")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProteinAccession> proteinAccessions = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public Protein() {
    }

    /**
     * Constructor.
     *
     * @param sequence the peptide sequence.
     */
    public Protein(final String sequence) {
        this.sequence = sequence;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
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

    public List<ProteinAccession> getProteinAccessions() {
        return proteinAccessions;
    }

    public void setProteinAccessions(List<ProteinAccession> proteinAccessions) {
        this.proteinAccessions = proteinAccessions;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.sequence);
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
        if (!Objects.equals(this.sequence, other.sequence)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return sequence;
    }

}
