package com.compomics.colims.core.service;

import com.compomics.colims.model.Peptide;

/**
 *
 * @author niels
 */
public interface PeptideService extends GenericService<Peptide, Long> {

    /**
     * Find a protein by the accession.
     *
     * @param spectrumId the spectrums id
     * @return the found peptide
     */
    Peptide findBySpectrumId(long spectrumId);
}
