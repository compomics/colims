package com.compomics.colims.repository;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.repository.hibernate.model.PeptideDTO;

import java.util.List;

/**
 * This interface provides repository methods for the Peptide class.
 *
 * @author Niels Hulstaert
 */
public interface PeptideRepository extends GenericRepository<Peptide, Long> {

    /**
     * Fetch the (distinct) PeptideDTO instances associated with the given protein group.
     *
     * @param proteinGroupId the protein group ID
     * @return the list of PeptideDTO objects
     */
    List<PeptideDTO> getPeptideDTOByProteinGroupId(Long proteinGroupId);
}
