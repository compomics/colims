package com.compomics.colims.core.service;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.repository.hibernate.model.PeptideDTO;

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
     * @return the peptide with the fetched modifications
     */
    Peptide fetchPeptideHasModifications(Peptide peptide);

    /**
     * Fetch the (distinct) PeptideDTO instances associated with the given protein group.
     *
     * @param proteinGroupId the protein group ID
     * @return the list of PeptideDTO objects
     */
    List<PeptideDTO> getPeptideDTOByProteinGroupId(Long proteinGroupId);

}
