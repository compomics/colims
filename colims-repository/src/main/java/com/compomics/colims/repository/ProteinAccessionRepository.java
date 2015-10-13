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
     * Get all accession strings associated with the specified group of proteins.
     *
     * @param proteinGroup the protein group
     * @return the list of protein accession strings
     */
    List<String> getAccessionsForProteinGroup(ProteinGroup proteinGroup);

    /**
     * Get all protein accession strings associated with a given peptide.
     *
     * @param peptide the peptide to find accessions for
     * @return the list of accession strings
     */
    List<String> getProteinAccessionsForPeptide(Peptide peptide);
}
