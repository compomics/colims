package com.compomics.colims.model.comparator;

import com.compomics.colims.model.PeptideHasModification;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This comparator compares location fields of PeptideHasModification entity instances.
 *
 * @author Niels Hulstaert
 */
public class PeptideHasModificationLocationComparator implements Comparator<PeptideHasModification>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(final PeptideHasModification peptideHasModification1, final PeptideHasModification peptideHasModification2) {
        return peptideHasModification1.getLocation().compareTo(peptideHasModification2.getLocation());
    }

}
