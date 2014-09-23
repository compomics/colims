package com.compomics.colims.model.comparator;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class CvParamAccessionComparator implements Comparator<AuditableTypedCvParam>, Serializable {

    private static final long serialVersionUID = -7238117038874630466L;

    @Override
    public int compare(final AuditableTypedCvParam cvParam1, final AuditableTypedCvParam cvParam2) {
        return cvParam1.getAccession().compareToIgnoreCase(cvParam2.getAccession());
    }
}
