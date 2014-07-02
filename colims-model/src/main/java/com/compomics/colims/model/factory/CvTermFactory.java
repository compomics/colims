package com.compomics.colims.model.factory;

import com.compomics.colims.model.TypedCvTerm;
import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.MaterialCvTerm;
import com.compomics.colims.model.ProtocolCvTerm;
import com.compomics.colims.model.enums.CvTermType;

/**
 *
 * @author Niels Hulstaert
 */
public final class CvTermFactory {

    private CvTermFactory() {
    }

    public static TypedCvTerm newInstance(final CvTermType cvTermType, final String ontology, final String label, final String accession, final String name) {
        TypedCvTerm cvTerm;

        CvTermType parent = cvTermType.getParent();
        if (parent.equals(CvTermType.INSTRUMENT_CV_TERM_TYPE)) {
            cvTerm = new InstrumentCvTerm(cvTermType, ontology, label, accession, name);
        } else if (parent.equals(CvTermType.MATERIAL_CV_TERM_TYPE)) {
            cvTerm = new MaterialCvTerm(cvTermType, ontology, label, accession, name);
        } else if (parent.equals(CvTermType.PROTOCOL_CV_TERM_TYPE)) {
            cvTerm = new ProtocolCvTerm(cvTermType, ontology, label, accession, name);
        } else {
            throw new IllegalStateException("Unknown cvTermType family");
        }

        return cvTerm;
    }
}
