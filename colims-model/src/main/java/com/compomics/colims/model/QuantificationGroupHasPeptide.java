/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "quantification_group_has_peptide")
@Entity
public class QuantificationGroupHasPeptide extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "l_quantification_group_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationGroup quantificationGroup;
    @JoinColumn(name = "l_peptide_id", referencedColumnName = "id")
    @ManyToOne
    private Peptide peptide;

    public QuantificationGroup getQuantificationGroup() {
        return quantificationGroup;
    }

    public void setQuantificationGroup(QuantificationGroup quantificationGroup) {
        this.quantificationGroup = quantificationGroup;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public void setPeptide(Peptide peptide) {
        this.peptide = peptide;
    }
}
