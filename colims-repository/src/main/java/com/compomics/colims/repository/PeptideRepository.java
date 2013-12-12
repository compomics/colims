package com.compomics.colims.repository;

import com.compomics.colims.model.Peptide;

/**
 *
 * @author Kenneth Verheggen
 */
public interface PeptideRepository extends GenericRepository<Peptide, Long> {

    /**
     * Find the spectra by analytical run id
     *
     * @param spectrumID the spectrum id
     * @return list of spectra for analyticalRun, that may be empty
     */
    Peptide findPeptideBySpectrumId(final Long spectrumID);
}
