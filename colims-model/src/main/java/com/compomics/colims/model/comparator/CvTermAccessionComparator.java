package com.compomics.colims.model.comparator;

import com.compomics.colims.model.TypedCvTerm;
import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class CvTermAccessionComparator implements Comparator<TypedCvTerm>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(final TypedCvTerm cvTerm1, final TypedCvTerm cvTerm2) {
        return cvTerm1.getAccession().compareToIgnoreCase(cvTerm2.getAccession());
    }
}
