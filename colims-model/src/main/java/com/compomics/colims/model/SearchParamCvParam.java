package com.compomics.colims.model;

import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_param_cv_param")
@Entity
public class SearchParamCvParam extends TypedCvParam {

    private static final long serialVersionUID = -2536095044338751914L;

    public SearchParamCvParam() {
    }

    public SearchParamCvParam(final CvParamType cvTermType, final String ontology, final String label, final String accession, final String name) {
        super(cvTermType, ontology, label, accession, name);
    }

    public SearchParamCvParam(CvParamType cvParamType, String ontology, String label, String accession, String name, String value) {
        super(cvParamType, ontology, label, accession, name, value);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
