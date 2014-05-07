package com.compomics.colims.repository;

import com.compomics.colims.model.ProteinAccession;
import java.util.List;

/**
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
}