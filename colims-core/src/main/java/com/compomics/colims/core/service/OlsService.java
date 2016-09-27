package com.compomics.colims.core.service;

import com.compomics.colims.core.ontology.ols.OlsSearchResult;
import com.compomics.colims.core.ontology.ols.Ontology;
import com.compomics.colims.core.ontology.ols.OntologyTerm;
import com.compomics.colims.core.ontology.ols.SearchResultMetadata;
import com.compomics.colims.model.AbstractModification;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

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
     * @throws IOException              in case of HTTP REST client error
     */
    List<Ontology> getAllOntologies() throws RestClientException, IOException;

    /**
     * Retrieve ontologies by namespace. The namespaces are converted to
     * lowercase before querying the OLS service. Returns an empty list when
     * nothing was found.
     *
     * @param namespaces the list of ontology namespaces.
     * @return the list of found ontologies
     * @throws HttpClientErrorException in case of HTTP REST client error
     * @throws IOException              in case of an I/O related problem
     */
    List<Ontology> getOntologiesByNamespace(List<String> namespaces) throws RestClientException, IOException;

    /**
     * Get the metadata before doing a paged search. If the ontology
     * namespaces/the search fields are empty, the search is performed against
     * all ontologies/with the default search fields. This method is intended to
     * be used in combination with {@link #doPagedSearch(java.lang.String, int, int)
     * }
     *
     * @param query              the search query
     * @param ontologyNamespaces the list of ontology namespaces
     * @param searchFields       the set of fields to search
     * @return the search result metadata
     * @throws HttpClientErrorException in case of HTTP REST client error
     * @throws IOException              in case of an I/O related problem
     */
    SearchResultMetadata getPagedSearchMetadata(String query, List<String> ontologyNamespaces, EnumSet<OlsSearchResult.SearchField> searchFields) throws RestClientException, IOException;

    /**
     * Do a paged search. The {@code searchUrl} comes from the {@link #getPagedSearchMetadata(java.lang.String,
     * java.util.List, java.util.EnumSet) }.
     *
     * @param searchUrl  the REST search URL
     * @param startIndex the result start index (0 based)
     * @param pageSize   the result page size
     * @return the list of search results
     * @throws HttpClientErrorException in case of HTTP REST client error
     * @throws IOException              in case of an I/O related problem
     */
    List<OlsSearchResult> doPagedSearch(String searchUrl, int startIndex, int pageSize) throws RestClientException, IOException;

    /**
     * Get the description for the ontology term with the given ontology
     * namespace and OBO ID.
     *
     * @param ontologyNamespace the ontology namespace
     * @param oboId             the ontology term OBO ID
     * @return the ontology term description
     * @throws HttpClientErrorException in case of HTTP REST client error
     * @throws IOException              in case of an I/O related problem
     */
    String getTermDescriptionByOboId(String ontologyNamespace, String oboId) throws RestClientException, IOException;

    /**
     * Find a modification by exact name in the PSI-MOD ontology.
     *
     * @param <T>   the AbstractModification subclass instance
     * @param clazz the AbstractModification subclass (Modification or SearchModification)
     * @param name  the modification name
     * @return the found modification, null if nothing was found
     * @throws HttpClientErrorException in case of HTTP REST client error
     * @throws IOException              in case of an I/O related problem
     */
    <T extends AbstractModification> T findModificationByExactName(final Class<T> clazz, final String name) throws RestClientException, IOException;

    /**
     * Find a modification by accession in the ontology.
     *
     * @param <T>       the AbstractModification subclass instance
     * @param clazz     the AbstractModification subclass (Modification or SearchModification)
     * @param accession the search modification accession
     * @return the found search modification, null if nothing was found
     * @throws HttpClientErrorException in case of HTTP REST client error
     * @throws IOException              in case of an I/O related problem
     */
    <T extends AbstractModification> T findModificationByAccession(final Class<T> clazz, final String accession) throws RestClientException, IOException;

    /**
     * Find an enzyme by name in the PSI-MOD ontology. This method uses
     * {@link java.lang.String#equalsIgnoreCase(String)} as comparison method.
     *
     * @param name the enzyme CV param name
     * @return the found enzyme as {@link OntologyTerm} object, null if nothing was found
     * @throws HttpClientErrorException in case of HTTP REST client error
     * @throws IOException              in case of an I/O related problem
     */
    OntologyTerm findEnzymeByName(final String name) throws RestClientException, IOException;

    /**
     * Get the modifications cache. This cache is used to avoid redundant
     * lookups with the OLS.
     *
     * @return the map of cached modifications (key: modification accession; value: modification).
     */
    Map<String, AbstractModification> getModificationsCache();
}
