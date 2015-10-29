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
 * @author Niels Hulstaert
 */
@Table(name = "quantification_group")
@Entity
public class QuantificationGroup extends DatabaseEntity {

    private static final long serialVersionUID = 6780345493346945685L;

    @JoinColumn(name = "l_peptide_id", referencedColumnName = "id")
    @ManyToOne
    private Peptide peptide;
    @JoinColumn(name = "l_quantification_id", referencedColumnName = "id")
    @ManyToOne
    private Quantification quantification;

    public Peptide getPeptide() {
        return peptide;
    }

    public void setPeptide(Peptide peptide) {
        this.peptide = peptide;
    }

    public Quantification getQuantification() {
        return quantification;
    }

    public void setQuantification(Quantification quantification) {
        this.quantification = quantification;
    }

}
