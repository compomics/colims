package com.compomics.colims.model;

import com.compomics.colims.model.enums.CvTermType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "material_cv_term")
@Entity
public class MaterialCvTerm extends TypedCvTerm {

    public MaterialCvTerm() {
    }    
    
    public MaterialCvTerm(final CvTermType cvTermType, final String ontology, final String label, final String accession, final String name) {
        super(cvTermType, ontology, label, accession, name);
    }        
        
}
