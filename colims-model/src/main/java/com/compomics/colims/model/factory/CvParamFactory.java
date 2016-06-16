package com.compomics.colims.model.factory;

import com.compomics.colims.model.SearchCvParam;
import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.InstrumentCvParam;
import com.compomics.colims.model.MaterialCvParam;
import com.compomics.colims.model.ProtocolCvParam;
import com.compomics.colims.model.cv.CvParam;
import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 * Factory class for creating CV term types.
 *
 * @author Niels Hulstaert
 */
public final class CvParamFactory {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(CvParamFactory.class);

    /**
     * Private constructor to prevent instantiation.
     */
    private CvParamFactory() {
    }

    /**
     * Create a new AuditableTypedCvParam instance.
     *
     * @param cvParamType the CV term type
     * @param ontology the ontology name
     * @param label the ontology label
     * @param accession CV term accession
     * @param name the CV term name
     * @return the AuditableTypedCvParam instance
     */
    public static AuditableTypedCvParam newAuditableTypedCvInstance(final CvParamType cvParamType, final String ontology, final String label, final String accession, final String name) {
        AuditableTypedCvParam cvParam;

        CvParamType parent = cvParamType.getParent();
        switch (parent) {
            case INSTRUMENT_CV_PARAM_TYPE:
                cvParam = new InstrumentCvParam(cvParamType, ontology, label, accession, name);
                break;
            case MATERIAL_CV_PARAM_TYPE:
                cvParam = new MaterialCvParam(cvParamType, ontology, label, accession, name);
                break;
            case PROTOCOL_CV_PARAM_TYPE:
                cvParam = new ProtocolCvParam(cvParamType, ontology, label, accession, name);
                break;
            default:
                throw new IllegalStateException("Unknown cvParamType family");
        }

        return cvParam;
    }

    /**
     * Create a new TypedCvParam instance.
     *
     * @param cvParamType the CV term type
     * @param ontology the ontology name
     * @param label the ontology label
     * @param accession CV term accession
     * @param name the CV term name
     * @return the TypedCvParam instance
     */
    public static TypedCvParam newTypedCvInstance(final CvParamType cvParamType, final String ontology, final String label, final String accession, final String name) {
        TypedCvParam cvParam;

        CvParamType parent = cvParamType.getParent();
        if (parent.equals(CvParamType.SEARCH_PARAM_CV_PARAM)) {
            cvParam = new SearchCvParam(cvParamType, ontology, label, accession, name);
        } else {
            throw new IllegalStateException("Unknown CvParamType family");
        }

        return cvParam;
    }

    /**
     * Create a new CvParam instance.
     *
     * @param <T> the CvParam subclass
     * @param clazz the CvParam subclass
     * @param ontology the ontology name
     * @param label the ontology label
     * @param accession CV term accession
     * @param name the CV term name
     * @return the TypedCvParam instance
     */
    public static <T extends CvParam> T newCvInstance(final Class<T> clazz, final String ontology, final String label, final String accession, final String name) {
        T cvParam = null;
        try {
            cvParam = clazz.newInstance();
            cvParam.setOntology(ontology);
            cvParam.setAccession(accession);
            cvParam.setLabel(label);
            cvParam.setName(name);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(CvParamFactory.class);
        }

        return cvParam;
    }

}
