package com.compomics.colims.repository;

import com.compomics.colims.model.Protein;

/**
 *
 * @author niels
 */
public interface ProteinRepository extends GenericRepository<Protein, Long> {

    /**
     * Find a protein by the accession.
     *
     * @param accession the protein accession
     * @return the found protein
     */
    Protein findByAccession(String accession);
}
