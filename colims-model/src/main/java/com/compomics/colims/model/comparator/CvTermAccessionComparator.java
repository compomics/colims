package com.compomics.colims.model.comparator;

import com.compomics.colims.model.AuditableTypedCvTerm;
import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class CvTermAccessionComparator implements Comparator<AuditableTypedCvTerm>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(final AuditableTypedCvTerm cvTerm1, final AuditableTypedCvTerm cvTerm2) {
        return cvTerm1.getAccession().compareToIgnoreCase(cvTerm2.getAccession());
    }
}
