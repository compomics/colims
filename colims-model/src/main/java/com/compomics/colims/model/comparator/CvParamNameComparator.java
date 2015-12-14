package com.compomics.colims.model.comparator;

import com.compomics.colims.model.cv.CvParam;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This comparator compares accession fields of CvParam entity instances.
 *
 * @author Niels Hulstaert
 */
public class CvParamNameComparator implements Comparator<CvParam>, Serializable {

    @Override
    public int compare(final CvParam cvParam1, final CvParam cvParam2) {
        return cvParam1.getName().compareToIgnoreCase(cvParam2.getName());
    }
}
