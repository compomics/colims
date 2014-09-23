package com.compomics.colims.model;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "protocol_cv_term")
@Entity
public class ProtocolCvParam extends AuditableTypedCvParam {

    private static final long serialVersionUID = -3197049656512425516L;
    
    public ProtocolCvParam() {
    }

    public ProtocolCvParam(final CvParamType cvTermType, final String ontology, final String label, final String accession, final String name) {
        super(cvTermType, ontology, label, accession, name);
    }    

}
