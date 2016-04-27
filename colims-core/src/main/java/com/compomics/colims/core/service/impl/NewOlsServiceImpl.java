package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.model.ols.Ontology;
import com.compomics.colims.core.model.ols.OntologyTerm;
import com.compomics.colims.core.model.ols.OlsSearchResult;
import com.compomics.colims.core.model.ols.SearchResultMetadata;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.model.AbstractModification;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.model.factory.CvParamFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the OlsService interface.
 *
 * @author Niels Hulstaert
 */
@Service("newOlsService")
public class NewOlsServiceImpl implements OlsService {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(NewOlsServiceImpl.class);

    private static final String OLS_BASE_URL = "http://www.ebi.ac.uk/ols/beta/api/ontologies";
    private static final String OLS_BASE_SEARCH_URL = "http://www.ebi.ac.uk/ols/beta/api/search?q=";
    private static final String PAGE_AND_SIZE = "?page=%1$d&size=%2$d";
    private static final String START_AND_ROWS = "&start={page}&rows={pageSize}";
    private static final String EMBEDDED = "_embedded";
    private static final String PAGE = "page";
    private static final String PAGE_NUMBER = "number";
    private static final String TOTAL_PAGES = "totalPages";
    private static final String ONTOLOGIES = "ontologies";
    private static final String CONFIG = "config";
    private static final String HIGHLIGHTING = "highlighting";
    private static final String RESPONSE = "response";
    private static final String NUMBERS_FOUND = "numFound";
    private static final String DOCS = "docs";
    private static final String MOD_ONTOLOGY_NAMESPACE = "mod";
    private static final String TERMS = "terms";
    private static final String OBO_ID = "obo_id";
    private static final String LABEL = "label";
    private static final String MOD_EXACT_NAME_QUERY = OLS_BASE_SEARCH_URL + "{name}&ontology=mod&exact=true&queryFields=label";
    private static final String MOD_OBO_ID_QUERY = "/" + MOD_ONTOLOGY_NAMESPACE + "/terms?obo_id={obo_id}";
    private static final String TERMS_OBO_ID_QUERY = OLS_BASE_URL + "/" + "{ontology_namespace}/terms?obo_id={obo_id}";
    private static final String MS_LABEL_QUERY = OLS_BASE_SEARCH_URL + "{name}&ontology=ms&queryFields=label";
    private static final int PAGE_SIZE = 20;

    /**
     * The Spring RestTemplate instance for accessing the OLS rest API.
     */
    @Autowired
    private RestTemplate restTemplate;
    /**
     * The JSON mapper.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Ontologies cache to prevent unnecessary lookups. This map can contains
     * instances of {@link
     * Ontology} with the ontology namespace as key.
     */
    private final Map<String, Ontology> ontologiesCache = new HashMap<>();
    /**
     * Modifications cache to prevent unnecessary webservice lookups. This map
     * can contains instances of {@link
     * Modification} and {@link com.compomics.colims.model.SearchModification}.
     */
    private final java.util.Map<String, AbstractModification> modificationsCache = new HashMap<>();

    @Override
    public List<Ontology> getAllOntologies() throws RestClientException, IOException {
        List<Ontology> ontologies = new ArrayList<>();

        int pageIndex = 0;
        int numberOfPages = -1;
        do {
            //get the current page
            ResponseEntity<String> response = restTemplate.getForEntity(OLS_BASE_URL + String.format(PAGE_AND_SIZE, pageIndex, PAGE_SIZE), String.class);
            ObjectReader objectReader = objectMapper.reader();
            JsonNode responseBody = objectReader.readTree(response.getBody());

            //get the ontologies of the page
            JsonNode ontologiesNode = responseBody.get(EMBEDDED).get(ONTOLOGIES);
            Iterator<JsonNode> ontologyIterator = ontologiesNode.iterator();
            while (ontologyIterator.hasNext()) {
                JsonNode ontologyConfigNode = ontologyIterator.next().get(CONFIG);
                Ontology ontology = objectReader.treeToValue(ontologyConfigNode, Ontology.class);
                ontologies.add(ontology);
                //add to cache if not already present
                if (!ontologiesCache.containsKey(ontology.getNameSpace())) {
                    ontologiesCache.put(ontology.getNameSpace(), ontology);
                }
            }

            //get the current page and the last page
            pageIndex = responseBody.get(PAGE).get(PAGE_NUMBER).intValue() + 1;

            if (numberOfPages == -1) {
                numberOfPages = responseBody.get(PAGE).get(TOTAL_PAGES).intValue();
                //check for no results
                if (numberOfPages == 0) {
                    break;
                }
            }

        } while (pageIndex != numberOfPages);

        return ontologies;
    }

    @Override
    public List<Ontology> getOntologiesByNamespace(List<String> namespaces) throws RestClientException, IOException {
        List<Ontology> ontologies = new ArrayList<>();

        for (String namespace : namespaces) {
            if (ontologiesCache.containsKey(namespace)) {
                ontologies.add(ontologiesCache.get(namespace));
            } else {
                try {
                    //get the response
                    ResponseEntity<String> response = restTemplate.getForEntity(OLS_BASE_URL + "/{ontology_namespace}", String.class, namespace.toLowerCase());

                    ObjectReader objectReader = objectMapper.readerFor(OntologyTerm.class);
                    JsonNode responseBody = objectReader.readTree(response.getBody());

                    JsonNode ontologyConfigNode = responseBody.get(CONFIG);
                    Ontology ontology = objectReader.treeToValue(ontologyConfigNode, Ontology.class);
                    ontologies.add(ontology);

                    //add to cache
                    ontologiesCache.put(ontology.getNameSpace(), ontology);
                } catch (HttpClientErrorException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    //ignore the exception if the namespace doesn't correspond to an ontology
                    if (!ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                        throw ex;
                    }
                }
            }
        }

        return ontologies;
    }

    @Override
    public SearchResultMetadata getPagedSearchMetadata(String query, List<String> ontologyNamespaces, EnumSet<OlsSearchResult.SearchField> searchFields) throws RestClientException, IOException {
        //build the request
        StringBuilder url = new StringBuilder(OLS_BASE_SEARCH_URL);
        url.append(query);
        if (!searchFields.isEmpty() && !searchFields.equals(OlsSearchResult.DEFAULT_SEARCH_FIELDS)) {
            url.append("&queryFields=");
            url.append(searchFields.stream().map(s -> s.getQueryValue()).collect(Collectors.joining(",")));
        }
        if (!ontologyNamespaces.isEmpty()) {
            url.append("&ontology=");
            url.append(ontologyNamespaces.stream().collect(Collectors.joining(",")));
        }

        //get the response
        ResponseEntity<String> response = restTemplate.getForEntity(url.toString(), String.class);

        ObjectReader objectReader = objectMapper.reader();
        JsonNode responseBody = objectReader.readTree(response.getBody());

        return new SearchResultMetadata(responseBody.get(RESPONSE).get(NUMBERS_FOUND).asInt(), url.append(START_AND_ROWS).toString());
    }

    @Override
    public List<OlsSearchResult> doPagedSearch(String searchUrl, int startIndex, int pageSize) throws RestClientException, IOException {
        List<OlsSearchResult> searchResults = new ArrayList<>();

        //get the response
        ResponseEntity<String> response = restTemplate.getForEntity(searchUrl, String.class, startIndex, pageSize);

        ObjectReader objectReader = objectMapper.reader();
        JsonNode responseBody = objectReader.readTree(response.getBody());

        //get the docs and highlighting nodes
        JsonNode docsNode = responseBody.get(RESPONSE).get(DOCS);
        JsonNode highlightingNode = responseBody.get(HIGHLIGHTING);

        Iterator<JsonNode> docsIterator = docsNode.elements();
        Iterator<java.util.Map.Entry<String, JsonNode>> highlightingIterator = highlightingNode.fields();
        while (docsIterator.hasNext()) {
            OlsSearchResult searchResult = new OlsSearchResult();

            JsonNode ontologyTermNode = docsIterator.next();
            OntologyTerm ontologyTerm = objectReader.treeToValue(ontologyTermNode, OntologyTerm.class);
            //get the ontology title as well
            if (ontologiesCache.containsKey(ontologyTerm.getOntologyNamespace())) {
                ontologyTerm.setOntologyTitle(ontologiesCache.get(ontologyTerm.getOntologyNamespace()).getTitle());
            } else {
                List<String> namespaces = new ArrayList<>();
                namespaces.add(ontologyTerm.getOntologyNamespace());
                List<Ontology> ontologies = getOntologiesByNamespace(namespaces);
                if (!ontologies.isEmpty()) {
                    ontologyTerm.setOntologyTitle(ontologies.get(0).getTitle());
                }
            }
            searchResult.setOntologyTerm(ontologyTerm);

            java.util.Map.Entry<String, JsonNode> highLightEntry = highlightingIterator.next();
            //iterate over the fields because we need the key as well
            Iterator<java.util.Map.Entry<String, JsonNode>> highLightEntryIterator = highLightEntry.getValue().fields();
            EnumMap<OlsSearchResult.SearchField, String> matchedSearchFields = new EnumMap(OlsSearchResult.SearchField.class);
            while (highLightEntryIterator.hasNext()) {
                java.util.Map.Entry<String, JsonNode> searchHighlight = highLightEntryIterator.next();
                matchedSearchFields.put(OlsSearchResult.SearchField.findByQueryValue(searchHighlight.getKey()), searchHighlight.getValue().get(0).asText());
            }
            searchResult.setMatchedFields(matchedSearchFields);

            searchResults.add(searchResult);
        }

        return searchResults;
    }

    @Override
    public String getTermDescriptionByOboId(String ontologyNamespace, String oboId) throws RestClientException, IOException {
        String description = "";

        //get the response
        ResponseEntity<String> response = restTemplate.getForEntity(TERMS_OBO_ID_QUERY, String.class, ontologyNamespace, oboId);

        ObjectReader objectReader = objectMapper.reader();
        JsonNode responseBody = objectReader.readTree(response.getBody());

        Iterator<JsonNode> termIterator = responseBody.get(EMBEDDED).get(TERMS).iterator();
        if (termIterator.hasNext()) {
            description = termIterator.next().get("description").get(0).asText();
        }

        return description;
    }

    @Override
    public <T extends AbstractModification> T findModificationByExactName(Class<T> clazz, final String name) throws RestClientException, IOException {
        T modification = null;

        //get the response
        ResponseEntity<String> response = restTemplate.getForEntity(MOD_EXACT_NAME_QUERY, String.class, name);

        ObjectReader objectReader = objectMapper.reader();
        JsonNode responseNode = objectReader.readTree(response.getBody()).get(RESPONSE);

        //check if anything was found
        int numFound = responseNode.get(NUMBERS_FOUND).asInt();
        if (numFound > 0) {
            //get the docs node
            JsonNode docsNode = responseNode.get(DOCS);
            JsonNode modificationNode = docsNode.get(0);
            modification = findModificationByAccession(clazz, modificationNode.get(OBO_ID).textValue());
        }

        return modification;
    }

    @Override
    public <T extends AbstractModification> T findModificationByAccession(Class<T> clazz, String accession) throws RestClientException, IOException {
        T modification = null;

        //look for the modification in the cache
        if (modificationsCache.containsKey(accession)) {
            AbstractModification foundModification = modificationsCache.get(accession);
            if (clazz.isInstance(foundModification)) {
                modification = (T) foundModification;
            } else {
                modification = copyModification(clazz, foundModification);
            }
        } else {
            /**
             * Get the modification from the REST service, catch the error in
             * case nothing was found or some other problem.
             */
            modification = getByPsiModAccession(clazz, accession);

            //add modification to the cache
            modificationsCache.put(accession, modification);
        }

        return modification;
    }

    @Override
    public <T extends AbstractModification> T findModificationByNameAndUnimodAccession(final Class<T> clazz, final String name, final String unimodAccession) {
        //@// TODO: 29/03/16 implement this as soon as this is available in the new OLS service
        return null;
    }

    @Override
    public TypedCvParam findEnzymeByName(String name) throws RestClientException, IOException {
        TypedCvParam enzyme = null;

        //get the response
        ResponseEntity<String> response = restTemplate.getForEntity(MS_LABEL_QUERY, String.class, name);

        ObjectReader objectReader = objectMapper.reader();
        JsonNode responseNode = objectReader.readTree(response.getBody()).get(RESPONSE);

        //check if anything was found
        int numFound = responseNode.get(NUMBERS_FOUND).asInt();
        if (numFound > 0) {
            //get the docs node
            JsonNode docsNode = responseNode.get(DOCS);

            Iterator<JsonNode> children = docsNode.elements();
            while (children.hasNext()) {
                JsonNode child = children.next();
                if (child.has(LABEL)) {
                    String label = child.get(LABEL).asText();
                    if (label.equalsIgnoreCase(name)) {
                        OntologyTerm ontologyTerm = objectReader.treeToValue(child, OntologyTerm.class);
                        //get the ontology title
                        List<String> namespaces = new ArrayList<>();
                        namespaces.add(ontologyTerm.getOntologyNamespace());
                        List<Ontology> ontologies = getOntologiesByNamespace(namespaces);
                        ontologyTerm.setOntologyTitle(ontologies.get(0).getTitle());
                        enzyme = CvParamFactory.newTypedCvInstance(CvParamType.SEARCH_PARAM_ENZYME, ontologies.get(0).getTitle(), ontologyTerm.getLabel(), ontologyTerm.getOboId(), ontologyTerm.getLabel());
                        break;
                    }
                }
            }
        }

        return enzyme;
    }

    /**
     * Get the modification by it's PSI MOD accession.
     *
     * @param clazz the AbstractModification subclass (Modification or
     * SearchModification)
     * @param psiModAccession the PSI MOD accession of the modification
     * @param <T> the AbstractModification subclass instance
     * @return
     * @throws IOException in case of an I/O related problem
     * @throws HttpClientErrorException in case of a HTTP 4xx error was received
     */
    private <T extends AbstractModification> T getByPsiModAccession(final Class<T> clazz, final String psiModAccession) throws IOException, HttpClientErrorException {
        T modification = null;

        //get the response
        ResponseEntity<String> response = restTemplate.getForEntity(OLS_BASE_URL + MOD_OBO_ID_QUERY, String.class, psiModAccession);

        ObjectReader objectReader = objectMapper.reader();
        JsonNode responseBody = objectReader.readTree(response.getBody());

        Iterator<JsonNode> termIterator = responseBody.get(EMBEDDED).get(TERMS).iterator();
        if (termIterator.hasNext()) {
            modification = objectReader.treeToValue(termIterator.next(), clazz);
        }

        return modification;
    }

    @Override
    public java.util.Map<String, AbstractModification> getModificationsCache() {
        return modificationsCache;
    }

    /**
     * Copy (the instance fields of) the modification from one subclass of
     * AbstractModification to another.
     *
     * @param clazz the subclass of AbstractModification
     * @param modToCopy the modification to copy
     * @param <T> the AbstractModification subclass
     * @return the copied modification
     */
    private <T extends AbstractModification> T copyModification(Class<T> clazz, AbstractModification modToCopy) {
        T modification = null;

        try {
            modification = clazz.newInstance();
            modification.setAccession(modToCopy.getAccession());
            modification.setName(modToCopy.getName());
            if (modToCopy.getMonoIsotopicMassShift() != null) {
                modification.setMonoIsotopicMassShift(modToCopy.getMonoIsotopicMassShift());
            }
            if (modToCopy.getAverageMassShift() != null) {
                modification.setAverageMassShift(modToCopy.getAverageMassShift());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return modification;
    }

}
