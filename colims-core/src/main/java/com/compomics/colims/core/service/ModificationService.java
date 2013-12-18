package com.compomics.colims.core.service;

import com.compomics.colims.model.Modification;

/**
 *
 * @author Niels Hulstaert
 */
public interface ModificationService extends GenericService<Modification, Long> {

    /**
     * Find a modification by the modification name.
     *
     * @param name the modification name
     * @return the found modification
     */
    Modification findByName(String name);
    
    /**
     * Find a modification by the modification accession.
     *
     * @param accession the modification accession
     * @return the found modification
     */
    Modification findByAccession(String accession);
}
