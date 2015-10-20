package com.compomics.colims.model;

import com.compomics.colims.model.enums.ModificationType;

import javax.persistence.*;

/**
 * This class represents the join table between the peptide and modification tables.
 *
 * @author Niels Hulstaert
 */
@Table(name = "peptide_has_modification")
@Entity
public class PeptideHasModification extends DatabaseEntity {

    private static final long serialVersionUID = 3283350956279991057L;

    /**
     * The location of the modification on the peptide sequence. 1 is the first amino acid.
     */
    @Basic(optional = true)
    @Column(name = "location")
    private Integer location;
    /**
     * The probabilistic score value.
     */
    @Basic(optional = true)
    @Column(name = "prob_score")
    private Double probabilityScore;
    /**
     * The delta score value.
     */
    @Basic(optional = true)
    @Column(name = "delta_score")
    private Double deltaScore;
    /**
     * The modification type (fixed or variable).
     */
    @Basic(optional = true)
    @Column(name = "modification_type", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private ModificationType modificationType;
    /**
     * The Peptide instance where the modification has been identified on.
     */
    @JoinColumn(name = "l_peptide_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Peptide peptide;
    /**
     * The Modification instance.
     */
    @JoinColumn(name = "l_modification_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Modification modification;

    /**
     * No-arg constructor.
     */
    public PeptideHasModification() {
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(final Integer location) {
        this.location = location;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public void setPeptide(final Peptide peptide) {
        this.peptide = peptide;
    }

    public Double getProbabilityScore() {
        return probabilityScore;
    }

    public void setProbabilityScore(final Double probabilityScore) {
        this.probabilityScore = probabilityScore;
    }

    public Double getDeltaScore() {
        return deltaScore;
    }

    public void setDeltaScore(final Double deltaScore) {
        this.deltaScore = deltaScore;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(final ModificationType modificationType) {
        this.modificationType = modificationType;
    }

    public Modification getModification() {
        return modification;
    }

    public void setModification(final Modification modification) {
        this.modification = modification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeptideHasModification that = (PeptideHasModification) o;

        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (probabilityScore != null ? !probabilityScore.equals(that.probabilityScore) : that.probabilityScore != null)
            return false;
        if (deltaScore != null ? !deltaScore.equals(that.deltaScore) : that.deltaScore != null) return false;
        if (modificationType != that.modificationType) return false;
        if (!peptide.equals(that.peptide)) return false;
        return modification.equals(that.modification);

    }

    @Override
    public int hashCode() {
        int result = location != null ? location.hashCode() : 0;
        result = 31 * result + (probabilityScore != null ? probabilityScore.hashCode() : 0);
        result = 31 * result + (deltaScore != null ? deltaScore.hashCode() : 0);
        result = 31 * result + (modificationType != null ? modificationType.hashCode() : 0);
        result = 31 * result + peptide.hashCode();
        result = 31 * result + modification.hashCode();
        return result;
    }
}
