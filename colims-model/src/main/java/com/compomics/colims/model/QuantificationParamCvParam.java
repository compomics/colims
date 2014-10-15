package com.compomics.colims.model;

import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "quant_param_cv_param")
@Entity
public class QuantificationParamCvParam extends TypedCvParam {

    private static final long serialVersionUID = 6017965126255158010L;

    public QuantificationParamCvParam() {
    }

    public QuantificationParamCvParam(final CvParamType cvTermType, final String ontology, final String label, final String accession, final String name) {
        super(cvTermType, ontology, label, accession, name);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
