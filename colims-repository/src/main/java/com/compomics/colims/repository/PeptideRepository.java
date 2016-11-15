package com.compomics.colims.repository;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.PeptideHasProteinGroup;
import com.compomics.colims.repository.hibernate.PeptideDTO;

import java.util.List;

/**
 * This interface provides repository methods for the Peptide class.
 *
 * @author Niels Hulstaert
 */
public interface PeptideRepository extends GenericRepository<Peptide, Long> {

    /**
     * Fetch the PeptideDTO instances associated with the given protein group and analyticalRun.
     *
     * @param proteinGroupId the protein group ID
     * @param analyticalRunIds the list of anayicalRun IDs
     * @return the list of PeptideDTO objects
     */
    List<PeptideDTO> getPeptideDTOByProteinGroupIdAnalyticalRunId(Long proteinGroupId, List<Long> analyticalRunIds);


    /**
     * Fetch the PeptideHasModification join entities.
     *
     * @param peptideId the peptide ID
     * @return the list of PeptideHasModification instances
     */
    List<PeptideHasModification> fetchPeptideHasModifications(Long peptideId);
    
    /**
     * Fetch the distinct Peptide sequence instances associated with the given protein group and analyticalRun.
     * Different modifications or charge states of the same peptide are not counted.
     * 
     * @param proteinGroupId the protein group ID
     * @param analyticalRunIds the list of anayicalRun IDs
     * @return the list of Peptide sequence
     */
    List<String> getDistinctPeptideSequenceByProteinGroupIdAnalyticalRunId(Long proteinGroupId, List<Long> analyticalRunIds);
    
    /**
     * Fetch the unique Peptide instances associated with the given protein group and analyticalRun.
     *
     * @param proteinGroupId the protein group ID
     * @param analyticalRunIds the list of anayicalRun IDs
     * @return the list of unique Peptides
     */
    List<Peptide> getUniquePeptideByProteinGroupIdAnalyticalRunId(Long proteinGroupId, List<Long> analyticalRunIds);
}
