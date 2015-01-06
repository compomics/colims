package com.compomics.colims.core.service;

import com.compomics.colims.model.Peptide;

/**
 * This interface provides service methods for the Peptide class.
 *
 * @author Niels Hulstaert
 */
public interface PeptideService extends GenericService<Peptide, Long> {

    /**
     * Fetch the peptide peptideHasModifications.
     *
     * @param peptide the peptide entity
     */
    void fetchPeptideHasModifications(Peptide peptide);
}
