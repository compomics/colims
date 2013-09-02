package com.compomics.colims.model;

import com.compomics.colims.model.enums.CvTermProperty;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "material_cv_term")
@Entity
public class MaterialCvTerm extends CvTerm {

    public MaterialCvTerm() {
    }    
    
    public MaterialCvTerm(CvTermProperty cvTermProperty, String ontology, String label, String accession, String name) {
        super(cvTermProperty, ontology, label, accession, name);
    }        
        
}
