package com.compomics.colims.model.comparator;

import com.compomics.colims.model.CvTerm;
import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class CvTermAccessionComparator implements Comparator<CvTerm>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(final CvTerm cvTerm1, final CvTerm cvTerm2) {
        return cvTerm1.getAccession().compareToIgnoreCase(cvTerm2.getAccession());
    }
}
