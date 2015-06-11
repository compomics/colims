package com.compomics.colims.model;

import javax.persistence.*;

/**
 * This class represents the join table between the peptide and protein tables.
 *
 * @author Niels Hulstaert
 */
@Table(name = "peptide_has_protein")
@Entity
public class PeptideHasProtein extends DatabaseEntity {

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
     * <pre>
     * This Boolean field can have 3 values;
     *  1. null: the Protein instance is not a part of a protein group.
     *  2. false: the Protein instance is part of a protein group but is not the main protein.
     *  3. true: the Protein instance is the main protein of a protein group.
     * </pre>
     */
    @Basic(optional = true)
    @Column(name = "main_group_protein", nullable = true)
    private Boolean isMainGroupProtein;
    /**
     * The protein accession. It's important to note that the accession is also added (if not already present) to the
     * {@link ProteinAccession} table. It's stored as well here for auditing purposes.
     */
    @Basic(optional = true)
    @Column(name = "protein_accession", nullable = true)
    private String proteinAccession;
    /**
     * The Peptide instance of this join entity.
     */
    @JoinColumn(name = "l_peptide_id", referencedColumnName = "id")
    @ManyToOne
    private Peptide peptide;
    /**
     * The Protein instance of this join entity.
     */
    @JoinColumn(name = "l_protein_id", referencedColumnName = "id")
    @ManyToOne
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Protein protein;

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

    public Boolean isMainGroupProtein() {
        return isMainGroupProtein;
    }

    public void setMainGroupProtein(Boolean isMainGroupProtein) {
        this.isMainGroupProtein = isMainGroupProtein;
    }

    public String getProteinAccession() {
        return proteinAccession;
    }

    public void setProteinAccession(String proteinAccession) {
        this.proteinAccession = proteinAccession;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public void setPeptide(final Peptide peptide) {
        this.peptide = peptide;
    }

    public Protein getProtein() {
        return protein;
    }

    public void setProtein(final Protein protein) {
        this.protein = protein;
    }

}
