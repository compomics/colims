package com.compomics.colims.model;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This class represents an instrument CV term entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "instrument_cv_param")
@Entity
public class InstrumentCvParam extends AuditableTypedCvParam {

    private static final long serialVersionUID = 2388796313873657999L;

    /**
     * No-arg constructor.
     */
    public InstrumentCvParam() {
    }

    /**
     * Constructor.
     *
     * @param cvParamType the CV parameter type
     * @param label the ontology label
     * @param accession the CV term accession
     * @param name the CV term name
     */
    public InstrumentCvParam(final CvParamType cvParamType, final String label, final String accession, final String name) {
        super(cvParamType, label, accession, name);
    }

}
