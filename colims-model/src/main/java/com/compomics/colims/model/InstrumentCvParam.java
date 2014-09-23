package com.compomics.colims.model;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "instrument_cv_term")
@Entity
public class InstrumentCvParam extends AuditableTypedCvParam {

    private static final long serialVersionUID = 2388796313873657999L;

    public InstrumentCvParam() {
    }

    public InstrumentCvParam(final CvParamType cvTermType, final String ontology, final String label, final String accession, final String name) {
        super(cvTermType, ontology, label, accession, name);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
