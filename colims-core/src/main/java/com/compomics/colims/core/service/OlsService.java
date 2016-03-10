package com.compomics.colims.core.service;

import com.compomics.colims.core.model.ols.Ontology;
import com.compomics.colims.core.model.ols.SearchResult;
import com.compomics.colims.model.AbstractModification;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.cv.TypedCvParam;
import java.io.IOException;
import java.util.EnumSet;

import java.util.List;
import java.util.Map;
import org.springframework.web.client.HttpClientErrorException;

/**
 * This interface provides methods for accessing the Ontology lookup service
 * (OLS).
 *
 * @author Niels Hulstaert
 */
public interface OlsService {

    /**
     * Retrieve all available ontologies.
     *
     * @return the available ontologies
     * @throws HttpClientErrorException in case of a HTTP 4xx error was received
     * @throws IOException in case of an I/O related problem
     */
    List<Ontology> getAllOntologies() throws HttpClientErrorException, IOException;

    /**
     * Retrieve ontologies by namespace. The namespaces are converted to
     * lowercase before querying the OLS service. Returns an empty list when
     * nothing was found.
     *
     * @param namespaces the list of ontology namespaces.
     * @return the list of found ontologies
     * @throws HttpClientErrorException in case of a HTTP 4xx error was received
     * @throws IOException in case of an I/O related problem
     */
    List<Ontology> getOntologiesByNamespace(List<String> namespaces) throws HttpClientErrorException, IOException;

    List<SearchResult> search(String query, List<String> ontologyNamespaces, EnumSet<SearchResult.SearchField> searchFields) throws HttpClientErrorException, IOException;

    /**
     * Find a modification by exact name in the PSI-MOD ontology.
     *
     * @param <T> the AbstractModification subclass instance
     * @param clazz the AbstractModification subclass (Modification or
     * SearchModification)
     * @param name the modification name
     * @return the found modification, null if nothing was found
     */
    <T extends AbstractModification> T findModificationByExactName(final Class<T> clazz, final String name);

    /**
     * Find modifications by name in the PSI-MOD ontology. Multiple
     * modifications with similar names can be returned.
     *
     * @param name the modification name
     * @return the list of found modification, an empty list if nothing was
     * found
     */
    List<Modification> findModificationByName(final String name);

    /**
     * Find a modification by accession in the ontology.
     *
     * @param <T> the AbstractModification subclass instance
     * @param clazz the AbstractModification subclass (Modification or
     * SearchModification)
     * @param accession the search modification accession
     * @return the found search modification, null if nothing was found
     */
    <T extends AbstractModification> T findModificationByAccession(final Class<T> clazz, final String accession);

    /**
     * Find a modification by name and UNIMOD accession in the PSI-MOD ontology.
     * This method tries to find the modification by name and checks whether the
     * UNIMOD accession could be found in the Xref section (by using {@link
     * java.lang.String#equalsIgnoreCase(String)}).
     *
     * @param <T> the AbstractModification subclass instance
     * @param clazz the AbstractModification subclass (Modification or
     * SearchModification)
     * @param name the modification name
     * @param unimodAccession the modification UNIMOD accession
     * @return the found modification, null if nothing was found
     */
    <T extends AbstractModification> T findModificationByNameAndUnimodAccession(final Class<T> clazz, final String name, final String unimodAccession);

    /**
     * Find an enzyme by name in the PSI-MOD ontology. This method uses
     * {@link java.lang.String#equalsIgnoreCase(String)} as comparison method.
     *
     * @param name the enzyme CV param name
     * @return the found enzyme as TypedCvParam, null if nothing was found
     */
    TypedCvParam findEnzymeByName(final String name);

    /**
     * Get the modifications cache. This cache is used to avoid redundant
     * lookups with the OLS.
     *
     * @return the map of cached modifications (key: modification accession;
     * value: modification).
     */
    Map<String, AbstractModification> getModificationsCache();
}
