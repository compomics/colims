package com.compomics.colims.core.service;

import com.compomics.colims.model.Protein;

/**
 *
 * @author Niels Hulstaert
 */
public interface ProteinService extends GenericService<Protein, Long> {
    
    /**
     * Find a protein by the accession.
     *
     * @param accession the protein accession
     * @return the found protein
     */
    Protein findByAccession(String accession);   
}
