package com.compomics.colims.model.factory;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.InstrumentCvParam;
import com.compomics.colims.model.MaterialCvParam;
import com.compomics.colims.model.ProtocolCvParam;
import com.compomics.colims.model.enums.CvParamType;

/**
 *
 * @author Niels Hulstaert
 */
public final class CvParamFactory {

    private CvParamFactory() {
    }

    public static AuditableTypedCvParam newInstance(final CvParamType cvParamType, final String ontology, final String label, final String accession, final String name) {
        AuditableTypedCvParam cvParam;

        CvParamType parent = cvParamType.getParent();
        if (parent.equals(CvParamType.INSTRUMENT_CV_PARAM_TYPE)) {
            cvParam = new InstrumentCvParam(cvParamType, ontology, label, accession, name);
        } else if (parent.equals(CvParamType.MATERIAL_CV_PARAM_TYPE)) {
            cvParam = new MaterialCvParam(cvParamType, ontology, label, accession, name);
        } else if (parent.equals(CvParamType.PROTOCOL_CV_PARAM_TYPE)) {
            cvParam = new ProtocolCvParam(cvParamType, ontology, label, accession, name);
        } else {
            throw new IllegalStateException("Unknown cvParamType family");
        }

        return cvParam;
    }
}
