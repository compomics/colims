package com.compomics.colims.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a protein entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "protein")
@Entity
//@Indexed
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

    @Basic(optional = true)
    @Column(name = "description", nullable = true)
    private String description;
    /**
     * The ProteinGroupHasProtein instances from the join table between the
     * protein group and protein tables.
     */
    @OneToMany(mappedBy = "protein")
    private List<ProteinGroupHasProtein> proteinGroupHasProteins = new ArrayList<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProteinGroupHasProtein> getProteinGroupHasProteins() {
        return proteinGroupHasProteins;
    }

    public void setProteinGroupHasProteins(List<ProteinGroupHasProtein> proteinGroupHasProteins) {
        this.proteinGroupHasProteins = proteinGroupHasProteins;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Protein protein = (Protein) o;

        return sequence.equals(protein.sequence);
    }

    @Override
    public int hashCode() {
        return sequence.hashCode();
    }

    @Override
    public String toString() {
        return sequence;
    }

}
