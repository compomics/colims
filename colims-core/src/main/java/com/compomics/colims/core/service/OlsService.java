package com.compomics.colims.core.service;

import com.compomics.colims.model.Modification;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface OlsService {

    /**
     * Find a modification by exact name in the PSI-MOD ontology
     *
     * @param name the modification name
     * @return the found modification, null if nothing was found
     */
    Modification findModifiationByExactName(final String name);

    /**
     * Find modifications by name in the PSI-MOD ontology. Multiple
     * modifications with similar names can be returned.
     *
     * @param name the modification name
     * @return the list of found modification, an empty list if nothing was
     * found
     */
    List<Modification> findModifiationByName(final String name);

    /**
     * Find a modification by accession in the ontology
     *
     * @param accesion the modification accession
     * @return the found modification, null if nothing was found
     */
    Modification findModifiationByAccession(final String accession);
}
