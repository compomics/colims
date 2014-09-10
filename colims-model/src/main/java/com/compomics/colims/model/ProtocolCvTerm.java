package com.compomics.colims.model;

import com.compomics.colims.model.enums.CvTermType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "protocol_cv_term")
@Entity
public class ProtocolCvTerm extends TypedCvTerm {

    private static final long serialVersionUID = -3197049656512425516L;
    
    public ProtocolCvTerm() {
    }

    public ProtocolCvTerm(final CvTermType cvTermType, final String ontology, final String label, final String accession, final String name) {
        super(cvTermType, ontology, label, accession, name);
    }    

}
