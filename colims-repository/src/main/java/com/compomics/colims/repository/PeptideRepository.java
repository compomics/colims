package com.compomics.colims.repository;

import com.compomics.colims.model.Peptide;
import java.util.List;

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
    List<Peptide> findPeptideBySpectrumId(final Long spectrumID);
}
