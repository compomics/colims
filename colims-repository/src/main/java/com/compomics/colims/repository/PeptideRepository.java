package com.compomics.colims.repository;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.repository.hibernate.model.PeptideDTO;

import java.util.List;

/**
 * This interface provides repository methods for the Peptide class.
 *
 * @author Niels Hulstaert
 */
public interface PeptideRepository extends GenericRepository<Peptide, Long> {


    /**
     * Get all peptides associated with a spectrum
     *
     * @param spectrum Spectrum to search peptides with
     * @return List of peptides
     */
    List<Peptide> getPeptidesForSpectrum(Spectrum spectrum);

    /**
     * Fetch the PeptideDTO instances associated with the given protein group.
     *
     * @param proteinGroupId the protein group ID
     * @return the list of PeptideDTO objects
     */
    List<PeptideDTO> getPeptideDTOByProteinGroupId(Long proteinGroupId);
}
