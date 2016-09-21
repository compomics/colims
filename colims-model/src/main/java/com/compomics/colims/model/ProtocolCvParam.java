package com.compomics.colims.model;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This class represents an protocol CV term entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "protocol_cv_param")
@Entity
public class ProtocolCvParam extends AuditableTypedCvParam {

    private static final long serialVersionUID = -3197049656512425516L;

    /**
     * No-arg constructor.
     */
    public ProtocolCvParam() {
    }

    /**
     * Constructor.
     *
     * @param cvParamType the CV parameter type
     * @param label the ontology label
     * @param accession the CV term accession
     * @param name the CV term name
     */
    public ProtocolCvParam(final CvParamType cvParamType, final String label, final String accession, final String name) {
        super(cvParamType, label, accession, name);
    }

}
