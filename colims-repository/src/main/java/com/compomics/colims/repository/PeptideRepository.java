package com.compomics.colims.repository;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.repository.hibernate.PeptideDTO;
import com.compomics.colims.repository.hibernate.PeptideMzTabDTO;

import java.util.List;

/**
 * This interface provides repository methods for the Peptide class.
 *
 * @author Niels Hulstaert
 */
public interface PeptideRepository extends GenericRepository<Peptide, Long> {

    /**
     * Fetch the {@link PeptideDTO} instances associated with the given protein group and analytical runs.
     *
     * @param proteinGroupId   the protein group ID
     * @param analyticalRunIds the list of analytical run IDs
     * @return the list of PeptideDTO objects
     */
    List<PeptideDTO> getPeptideDTOs(Long proteinGroupId, List<Long> analyticalRunIds);

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
     * @param proteinGroupId   the protein group ID
     * @param analyticalRunIds the list of anayicalRun IDs
     * @return the list of Peptide sequence
     */
    List<String> getDistinctPeptideSequences(Long proteinGroupId, List<Long> analyticalRunIds);

    /**
     * Fetch the unique Peptide instances associated with the given protein group and analytical runs.
     *
     * @param proteinGroupId   the protein group ID
     * @param analyticalRunIds the list of anayical run IDs
     * @return the list of unique peptides
     */
    List<Peptide> getUniquePeptides(Long proteinGroupId, List<Long> analyticalRunIds);

    /**
     * Fetch the {@link PeptideMzTabDTO} instances associated with the given analytical runs.
     *
     * @param analyticalRunIds the list of anayical run IDs
     * @return the list of {@link PeptideMzTabDTO} instances
     */
    List<PeptideMzTabDTO> getPeptideMzTabDTOs(List<Long> analyticalRunIds);
}
