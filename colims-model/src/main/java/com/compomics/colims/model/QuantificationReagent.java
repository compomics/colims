/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.cv.CvParam;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This class represents a quantification reagent entity in the database.
 * @author demet
 */
@Table(name = "quantification_reagent")
@Entity
public class QuantificationReagent extends CvParam{
    
    private static final long serialVersionUID = -2868659987541597218L;

    public QuantificationReagent() {
    }

    public QuantificationReagent(String label, String accession, String name, String value) {
        super(label, accession, name, value);
    }
}
