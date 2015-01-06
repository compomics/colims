package com.compomics.colims.core.service;

import com.compomics.colims.model.Modification;
import com.compomics.colims.model.cv.TypedCvParam;

import java.util.List;

/**
 * This interface provides methods for accessing the Ontoloy lookup service.
 *
 * @author Niels Hulstaert
 */
public interface OlsService {

    /**
     * Find a modification by exact name in the PSI-MOD ontology.
     *
     * @param name the modification name
     * @return the found modification, null if nothing was found
     */
    Modification findModificationByExactName(final String name);

    /**
     * Find modifications by name in the PSI-MOD ontology. Multiple modifications with similar names can be returned.
     *
     * @param name the modification name
     * @return the list of found modification, an empty list if nothing was found
     */
    List<Modification> findModificationByName(final String name);

    /**
     * Find a modification by accession in the ontology.
     *
     * @param accession the modification accession
     * @return the found modification, null if nothing was found
     */
    Modification findModificationByAccession(final String accession);

    /**
     * Find a modification by name and UNIMOD accession in the PSI-MOD ontology. This method tries to find the
     * modification by name and checks wether the UNIMOD accession could be found in the Xref section (by using {@link
     * java.lang.String#equalsIgnoreCase(String)}).
     *
     * @param name            the modification name
     * @param unimodAccession the modification UNIMOD accession
     * @return the found modification, null if nothing was found
     */
    Modification findModificationByNameAndUnimodAccession(final String name, final String unimodAccession);

    /**
     * Find an enzyme by name in the PSI-MOD ontology. This method uses {@link java.lang.String#equalsIgnoreCase(String)}
     * as comparison method.
     *
     * @param name the enzyme CV param name
     * @return the found enzyme as TypedCvParam, null if nothing was found
     */
    TypedCvParam findEnzymeByName(final String name);
}
