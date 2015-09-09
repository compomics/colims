package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Modification;

import java.util.List;

/**
 * This interface provides repository methods for the Modification class.
 *
 * @author Niels Hulstaert
 */
public interface ModificationRepository extends GenericRepository<Modification, Long> {

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

    /**
     * Get the modification IDs for the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the list of protein IDs
     */
    List<Long> getModificationIdsForRun(AnalyticalRun analyticalRun);

}
