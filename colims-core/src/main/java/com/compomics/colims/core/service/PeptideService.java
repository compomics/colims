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
     * Fetch the PeptideDTO instances associated for the given
     * protein group and runs.
     *
     * @param proteinGroupId the protein group ID
     * @param analyticalRunIds the list of analytical run IDs
     * @return the list of PeptideDTO objects
     */
    List<PeptideDTO> getPeptideDTO(Long proteinGroupId, List<Long> analyticalRunIds);
    
    /**
     * Fetch the distinct Peptide sequence instances associated with the given protein group and analyticalRun.
     * Different modifications or charge states of the same peptide are not counted.
     * 
     * @param proteinGroupId the protein group ID
     * @param analyticalRunIds the list of analytical run IDs
     * @return the list of Peptide sequences
     */
    List<String> getDistinctPeptideSequence(Long proteinGroupId, List<Long> analyticalRunIds);

    /**
     * Fetch the unique Peptides instances associated with the given protein group and analyticalRun.
     *
     * @param proteinGroupId the protein group ID
     * @param analyticalRunIds the list of anayicalRun IDs
     * @return the list of unique Peptides
     */
    List<Peptide> getUniquePeptides(Long proteinGroupId, List<Long> analyticalRunIds);
}
