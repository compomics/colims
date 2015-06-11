package com.compomics.colims.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * The PeptideHasProtein instances from the join table between the peptide and protein tables.
     */
    @OneToMany(mappedBy = "protein")
    private List<PeptideHasProtein> peptideHasProteins = new ArrayList<>();
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
        return Objects.equals(this.sequence, other.sequence);
    }

    @Override
    public String toString() {
        return sequence;
    }

}
