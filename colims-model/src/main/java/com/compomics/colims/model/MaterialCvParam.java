package com.compomics.colims.model;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This class represents a material CV term entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "material_cv_param")
@Entity
public class MaterialCvParam extends AuditableTypedCvParam {

    private static final long serialVersionUID = -8458299630247265494L;

    /**
     * No-arg constructor.
     */
    public MaterialCvParam() {
    }

    /**
     * Constructor.
     *
     * @param cvParamType the CV parameter type
     * @param label the ontology label
     * @param accession the CV term accession
     * @param name the CV term name
     */
    public MaterialCvParam(final CvParamType cvParamType, final String label, final String accession, final String name) {
        super(cvParamType, label, accession, name);
    }

}
