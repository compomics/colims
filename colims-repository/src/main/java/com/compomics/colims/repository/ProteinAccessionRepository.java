package com.compomics.colims.repository;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.ProteinAccession;
import com.compomics.colims.model.ProteinGroup;

import java.util.List;

/**
 * This interface provides repository methods for the ProteinAccession class.
 *
 * @author Niels Hulstaert
 */
public interface ProteinAccessionRepository extends GenericRepository<ProteinAccession, Long> {

    /**
     * Find protein accessions by the accession string.
     *
     * @param accession the protein accession
     * @return the found protein accessions
     */
    List<ProteinAccession> findByAccession(String accession);

    /**
     * Get accessions for all proteins in a given protein group
     *
     * @param proteinGroup  Protein group in question
     * @return List of accessions
     */
    List<ProteinAccession> getAccessionsForProteinGroup(ProteinGroup proteinGroup);

    /**
     * Get all protein accessions associated with a peptide
     *
     * @param peptide Peptide to find accessions for
     * @return A list of accessions
     */
    List<String> getProteinAccessionsForPeptide(Peptide peptide);
}
