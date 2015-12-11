package com.compomics.colims.model.comparator;

import com.compomics.colims.model.cv.AuditableCvParam;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This comparator compares accession fields of AuditableCvParam entity instances.
 *
 * @author Niels Hulstaert
 */
public class AuditableCvParamAccessionComparator implements Comparator<AuditableCvParam>, Serializable {

    private static final long serialVersionUID = -7238117038874630466L;

    @Override
    public int compare(final AuditableCvParam cvParam1, final AuditableCvParam cvParam2) {
        return cvParam1.getAccession().compareToIgnoreCase(cvParam2.getAccession());
    }
}
