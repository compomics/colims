package com.compomics.colims.model;

import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This class represents a search CV term entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_cv_param")
@Entity
public class SearchCvParam extends TypedCvParam {

    private static final long serialVersionUID = -2536095044338751914L;

    /**
     * No-arg constructor.
     */
    public SearchCvParam() {
    }

    /**
     * Constructor.
     *
     * @param cvParamType the CV parameter type
     * @param ontology    the ontology name
     * @param label       the ontology label
     * @param accession   the CV term accession
     * @param name        the CV term name
     */
    public SearchCvParam(final CvParamType cvParamType, final String ontology, final String label, final String accession, final String name) {
        super(cvParamType, ontology, label, accession, name);
    }

    public SearchCvParam(CvParamType cvParamType, String ontology, String label, String accession, String name, String value) {
        super(cvParamType, ontology, label, accession, name, value);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
