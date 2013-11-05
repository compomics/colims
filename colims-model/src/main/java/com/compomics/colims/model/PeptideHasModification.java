/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.enums.ModificationScoreType;
import com.compomics.colims.model.enums.ModificationTypeEnum;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "peptide_has_modification")
@Entity
public class PeptideHasModification extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = true)
    @Column(name = "location")
    private Integer location;
    @Basic(optional = true)
    @Column(name = "score")
    private Double score;
    @Basic(optional = true)
    @Enumerated(EnumType.STRING)
    @Column(name = "score_type")
    private ModificationScoreType modificationScoreType;
    @Basic(optional = true)
    @Column(name = "modification_type", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private ModificationTypeEnum modificationType;
    @JoinColumn(name = "l_peptide_id", referencedColumnName = "id")
    @ManyToOne
    private Peptide peptide;
    @JoinColumn(name = "l_modification_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL)
    private Modification modification;

    public PeptideHasModification() {
        //default value for score type
        this.modificationScoreType = ModificationScoreType.DELTA;
    }            

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public void setPeptide(Peptide peptide) {
        this.peptide = peptide;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public ModificationScoreType getModificationScoreType() {
        return modificationScoreType;
    }

    public void setModificationScoreType(ModificationScoreType modificationScoreType) {
        this.modificationScoreType = modificationScoreType;
    }

    public ModificationTypeEnum getModificationType() {
        return modificationType;
    }

    public void setModificationType(ModificationTypeEnum modificationType) {
        this.modificationType = modificationType;
    }        

    public Modification getModification() {
        return modification;
    }

    public void setModification(Modification modification) {
        this.modification = modification;
    }        
}
