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
 * This class represents a quantification method cv parameters entity in the database.
 * @author demet
 */
@Table(name = "quantification_method_cv_param")
@Entity
public class QuantificationMethodCvParam extends CvParam{
    
    private static final long serialVersionUID = 8852434545673934041L;

    public QuantificationMethodCvParam() {
    }

    public QuantificationMethodCvParam(final String ontology, final String label, final String accession, final String name, final String value) {
        super(ontology, label, accession, name, value);
    }
}
