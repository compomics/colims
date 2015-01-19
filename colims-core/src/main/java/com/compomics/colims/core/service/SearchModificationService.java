package com.compomics.colims.core.service;

import com.compomics.colims.model.Modification;
import com.compomics.colims.model.SearchModification;

/**
 * This interface provides service methods for the SearchModification class.
 *
 * @author Niels Hulstaert
 */
public interface SearchModificationService extends GenericService<SearchModification, Long> {

    /**
     * Find a search modification by the modification name. Returns the first modification found, null if none were
     * found.
     *
     * @param name the search modification name
     * @return the found modification
     */
    SearchModification findByName(String name);

    /**
     * Find a search modification by the modification accession. Returns null if nothing was found.
     *
     * @param accession the search modification accession
     * @return the found modification
     */
    SearchModification findByAccession(String accession);

    /**
     * Find a search modification by the modification alternative accession. Returns the first modification found, null
     * if none were found.
     *
     * @param alternativeAccession the search modification accession
     * @return the found modification
     */
    SearchModification findByAlternativeAccession(String alternativeAccession);

}
