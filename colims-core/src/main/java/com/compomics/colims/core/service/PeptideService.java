package com.compomics.colims.core.service;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.repository.hibernate.PeptideDTO;

import java.util.List;

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

    /**
     * Fetch the (distinct) PeptideDTO instances associated with the given protein group.
     *
     * @param proteinGroupId the protein group ID
     * @param analyticalRunIds the list of analytical Run IDs
     * @return the list of PeptideDTO objects
     */
    List<PeptideDTO> getPeptideDTOByProteinGroupIdAnalyticalRunId(Long proteinGroupId, List<Long> analyticalRunIds);

}
