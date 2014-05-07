package com.compomics.colims.core.service;

import com.compomics.colims.model.Modification;

/**
 *
 * @author Niels Hulstaert
 */
public interface ModificationService extends GenericService<Modification, Long> {

    /**
     * Find a modification by the modification name. Returns the first
     * modification found, null if none were found.
     *
     * @param name the modification name
     * @return the found modification
     */
    Modification findByName(String name);

    /**
     * Find a modification by the modification accession. Returns null if
     * nothing was found.
     *
     * @param accession the modification accession
     * @return the found modification
     */
    Modification findByAccession(String accession);

    /**
     * Find a modification by the modification alternative accession. Returns
     * the first modification found, null if none were found.
     *
     * @param alternativeAccession the modification accession
     * @return the found modification
     */
    Modification findByAlternativeAccession(String alternativeAccession);

}
