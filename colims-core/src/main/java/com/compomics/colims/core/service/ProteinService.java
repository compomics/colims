package com.compomics.colims.core.service;

import com.compomics.colims.model.Protein;

/**
 * This interface provides service methods for the Protein class.
 *
 * @author Niels Hulstaert
 */
public interface ProteinService extends GenericService<Protein, Long> {

    /**
     * Find a protein by the sequence.
     *
     * @param sequence the protein sequence
     * @return the found protein
     */
    Protein findBySequence(String sequence);

    /**
     * Fetch the protein accession.
     *
     * @param protein the Protein instance
     */
    void fetchAccessions(Protein protein);

    /**
     * Get the Colims protein by accession and sequence. This method
     * looks for the protein in the cache first before querying the database.
     *
     * @param sequence the protein sequence
     * @param accession the protein accession
     * @return the found Protein instance
     */
    Protein getProtein(String sequence, String accession);

    /**
     * Clear the resources used by this resource.
     */
    void clear();

}
