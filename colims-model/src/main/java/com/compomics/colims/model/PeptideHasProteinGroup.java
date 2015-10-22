package com.compomics.colims.model;

import javax.persistence.*;

/**
 * This class represents the join table between the peptide and protein group tables.
 *
 * @author Niels Hulstaert
 */
@Table(name = "peptide_has_protein_group")
@Entity
public class PeptideHasProteinGroup extends DatabaseEntity {

    private static final long serialVersionUID = -7522445376198555037L;

    /**
     * The peptide probability score.
     */
    @Basic(optional = true)
    @Column(name = "peptide_prob", nullable = true)
    private Double peptideProbability;
    /**
     * The peptide posterior error probability score.
     */
    @Basic(optional = true)
    @Column(name = "peptide_post_error_prob", nullable = true)
    private Double peptidePostErrorProbability;
    /**
     * The Peptide instance of this join entity.
     */
    @JoinColumn(name = "l_peptide_id", referencedColumnName = "id")
    @ManyToOne
    private Peptide peptide;
    /**
     * The ProteinGroup instance of this join entity.
     */
    @JoinColumn(name = "l_protein_group_id", referencedColumnName = "id")
    @ManyToOne
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    private ProteinGroup proteinGroup;

    public Double getPeptideProbability() {
        return peptideProbability;
    }

    public void setPeptideProbability(final Double peptideProbability) {
        this.peptideProbability = peptideProbability;
    }

    public Double getPeptidePostErrorProbability() {
        return peptidePostErrorProbability;
    }

    public void setPeptidePostErrorProbability(final Double peptidePostErrorProbability) {
        this.peptidePostErrorProbability = peptidePostErrorProbability;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public void setPeptide(final Peptide peptide) {
        this.peptide = peptide;
    }

    public ProteinGroup getProteinGroup() {
        return proteinGroup;
    }

    public void setProteinGroup(ProteinGroup proteinGroup) {
        this.proteinGroup = proteinGroup;
    }
}
