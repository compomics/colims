/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.enums.ModificationType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "peptide_has_modification")
@Entity
public class PeptideHasModification extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;

    @Basic(optional = true)
    @Column(name = "location")
    private Integer location;
    @Basic(optional = true)
    @Column(name = "alpha_score")
    private Double alphaScore;
    @Basic(optional = true)
    @Column(name = "delta_score")
    private Double deltaScore;
    @Basic(optional = true)
    @Column(name = "modification_type", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private ModificationType modificationType;
    @JoinColumn(name = "l_peptide_id", referencedColumnName = "id")
    @ManyToOne
    private Peptide peptide;
    @JoinColumn(name = "l_modification_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL)
    private Modification modification;

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

    public Double getAlphaScore() {
        return alphaScore;
    }

    public void setAlphaScore(final Double alphaScore) {
        this.alphaScore = alphaScore;
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
}
