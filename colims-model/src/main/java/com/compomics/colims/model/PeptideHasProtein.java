package com.compomics.colims.model;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "peptide_has_protein")
@Entity
public class PeptideHasProtein extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = true)
    @Column(name = "peptide_prob", nullable = true)
    private Double peptideProbability;
    @Basic(optional = true)
    @Column(name = "peptide_post_error_prob", nullable = true)
    private Double peptidePostErrorProbability;
    @JoinColumn(name = "l_peptide_id", referencedColumnName = "id")
    @ManyToOne
    private Peptide peptide;
    @JoinColumn(name = "l_protein_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL)
    private Protein protein;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPeptideProbability() {
        return peptideProbability;
    }

    public void setPeptideProbability(Double peptideProbability) {
        this.peptideProbability = peptideProbability;
    }

    public Double getPeptidePostErrorProbability() {
        return peptidePostErrorProbability;
    }

    public void setPeptidePostErrorProbability(Double peptidePostErrorProbability) {
        this.peptidePostErrorProbability = peptidePostErrorProbability;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public void setPeptide(Peptide peptide) {
        this.peptide = peptide;
    }

    public Protein getProtein() {
        return protein;
    }

    public void setProtein(Protein protein) {
        this.protein = protein;
    }
}
