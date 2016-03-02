package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.model.ols.Ontology;
import com.compomics.colims.core.model.ols.OntologyTerm;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.model.AbstractModification;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.model.factory.CvParamFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xml.xml_soap.Map;
import org.apache.xml.xml_soap.MapItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.ontology_lookup.ontologyquery.Query;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author Niels Hulstaert
 */
@Service("newOlsService")
public class NewOlsServiceImpl implements OlsService {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(NewOlsServiceImpl.class);
    private static final String MOD_ONTOLOGY_LABEL = "MOD";
    private static final String MOD_ONTOLOGY_QUERY_LABEL = "mod";
    private static final String MOD_ONTOLOGY_IRI = "http://purl.obolibrary.org/obo/";
    private static final String MS_ONTOLOGY_LABEL = "MS";
    private static final String MS_ONTOLOGY = "PSI Mass Spectrometry Ontology [MS]";
    private static final String URL_ENCODING = "UTF-8";
    private static final String OLS_BASE_URL = "http://www.ebi.ac.uk/ols/beta/api/ontologies/";
    private static final String OLS_BASE_SEARCH_URL = "http://www.ebi.ac.uk/ols/beta/api/search?q={query}";
    private static final String PAGE_AND_SIZE = "?page=%1$d&page=%2$d";
    private static final String EMBEDDED = "_embedded";
    private static final String PAGE = "page";
    private static final String PAGE_NUMBER = "number";
    private static final String TOTAL_PAGES = "totalPages";
    private static final String TERMS = "terms";
    private static final String ONTOLOGIES = "ontologies";
    private static final String CONFIG = "config";
    private static final String TERMS_IRI_QUERY = "{ontology_namespace}/terms/{iri}";
    private static final String TERMS_CHILDREN_QUERY = "%s/terms/%s/children";
    private static final String TERMS_OBO_ID_QUERY = "{ontology_namespace}/terms?obo_id={obo_id}";
    private static final String TERMS_ROOTS_QUERY = "%s/terms/roots";
    private static final String SEARCH_ONTOLOGY_LABEL = "&ontology={ontology_namespace}&queryFields=label&exact=false";
    private static final String HIGHLIGHTING = "highlighting";
    private static final String LABEL = "label";
    private static final int PAGE_SIZE = 20;

    /**
     * The Spring RestTemplate instance for accessing the OLS rest API.
     */
    private final RestTemplate restTemplate = new RestTemplate();
    /**
     * The json mapper.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Modifications cache to prevent unnecessary webservice lookups. This map
     * can contains instances of {@link
     * Modification} and {@link com.compomics.colims.model.SearchModification}.
     */
    private final java.util.Map<String, AbstractModification> modificationsCache = new HashMap<>();
    /**
     * This interface provides access to the ontology lookup service.
     */
    @Autowired
    private Query olsClient;

    @Override
    public List<Ontology> getAllOntologies() throws HttpClientErrorException, IOException {
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
    public List<Ontology> getOntologiesByNamespace(List<String> namespaces) throws HttpClientErrorException, IOException {
        List<Ontology> ontologies = new ArrayList<>();

        for (String namespace : namespaces) {
            //get the response
            ResponseEntity<String> response = restTemplate.getForEntity(OLS_BASE_URL + "{ontology_namespace}", String.class, namespace.toLowerCase());

            ObjectReader objectReader = objectMapper.readerFor(OntologyTerm.class);
            JsonNode responseBody = objectReader.readTree(response.getBody());

            JsonNode ontologyConfigNode = responseBody.get(CONFIG);
            Ontology ontology = objectReader.treeToValue(ontologyConfigNode, Ontology.class);
            ontologies.add(ontology);
        }

        return ontologies;
    }

    @Override
    public <T extends AbstractModification> T findModificationByExactName(Class<T> clazz, final String name) {
        T modification = null;

        //find the modification by exact name
        Map modificationTerms = olsClient.getTermsByExactName(name, MOD_ONTOLOGY_LABEL);
        if (modificationTerms.getItem() != null) {
            //get the modification accession
            for (MapItem mapItem : modificationTerms.getItem()) {
                modification = findModificationByAccession(clazz, mapItem.getKey().toString());
            }
        }

        return modification;
    }

    @Override
    public List<Modification> findModificationByName(final String name) {
        List<Modification> modifications = new ArrayList<>();

        ResponseEntity<String> response = restTemplate.getForEntity("http://www.ebi.ac.uk/ols/beta/api/search?q={name}&ontology={ontology_label}&exact={exact}&queryFields=label", String.class, name, "mod", "false");
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(response.getBody());
            System.out.println("---------------");
        } catch (IOException e) {
            e.printStackTrace();
        }

//        //find the modifications by name
//        Map modificationsTerms = olsClient.getTermsByName(name, MOD_ONTOLOGY_LABEL, false);
//
//        if (modificationsTerms.getItem() != null) {
//            //get the modifications
//            modificationsTerms.getItem()
//                    .stream()
//                    .map((mapItem) -> findModificationByAccession(Modification.class, mapItem.getKey().toString()))
//                    .filter((modification) -> (modification != null))
//                    .forEach((modification) -> {
//                        modifications.add(modification);
//                    });
//        }
        return modifications;
    }

    @Override
    public <T extends AbstractModification> T findModificationByAccession(Class<T> clazz, String accession) {
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
            //get the modification name
            String searchModificationName = olsClient.getTermById(accession, MOD_ONTOLOGY_LABEL);

//            ResponseEntity<String> response = restTemplate.getForEntity("http://www.ebi.ac.uk/ols/beta/api/search?q={accession}&ontology={ontology_label}&exact={exact}&queryFields=obo_id", String.class, accession, "mod", "true");
            try {
                String iriAccession = accession.replace(':', '_');
                String test = "http://www.ebi.ac.uk/ols/beta/api/ontologies/mod/terms/" + URLEncoder.encode(MOD_ONTOLOGY_IRI + iriAccession, URL_ENCODING);
                ResponseEntity<String> response = restTemplate.getForEntity("http://www.ebi.ac.uk/ols/beta/api/ontologies/mod/terms/" + URLEncoder.encode(MOD_ONTOLOGY_IRI + iriAccession, URL_ENCODING), String.class);
                ObjectMapper mapper = new ObjectMapper();

                JsonNode responseBody = mapper.readTree(response.getBody());

                System.out.println("---------------");
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }

            //check if a term was found
            if (!accession.equals(searchModificationName)) {
                //get the term metadata by accession
                Map modificationMetaData = olsClient.getTermMetadata(accession, MOD_ONTOLOGY_LABEL);
                if (modificationMetaData.getItem() != null) {
                    try {
                        modification = clazz.newInstance();
                        modification.setAccession(accession);
                        modification.setName(searchModificationName);

                        //get the modification properties
                        for (MapItem mapItem : modificationMetaData.getItem()) {
                            if (mapItem.getKey() != null && mapItem.getValue() != null) {
                                if (mapItem.getKey().equals("DiffMono")) {
                                    try {
                                        Double monoIsotopicsMassShift = Double.parseDouble(mapItem.getValue().toString());
                                        modification.setMonoIsotopicMassShift(monoIsotopicsMassShift);
                                    } catch (NumberFormatException nfe) {
                                        LOGGER.error(nfe, nfe.getCause());
                                    }
                                } else if (mapItem.getKey().equals("DiffAvg")) {
                                    try {
                                        Double averageMassShift = Double.parseDouble(mapItem.getValue().toString());
                                        modification.setAverageMassShift(averageMassShift);
                                    } catch (NumberFormatException nfe) {
                                        LOGGER.error(nfe.getMessage(), nfe);
                                    }
                                }
                            }
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        LOGGER.error(e.getMessage(), e);
                    }

                    //add modification to the cache
                    modificationsCache.put(accession, modification);
                }
            }
        }

        return modification;
    }

    @Override
    public <T extends AbstractModification> T findModificationByNameAndUnimodAccession(final Class<T> clazz, final String name, final String unimodAccession) {
        T modification = null;

        //look for the modification in the cache
        if (modificationsCache.containsKey(unimodAccession)) {
            AbstractModification foundModification = modificationsCache.get(unimodAccession);
            if (clazz.isInstance(foundModification)) {
                modification = (T) foundModification;
            } else {
                modification = copyModification(clazz, foundModification);
            }
        } else {
            //first, find the modifications by name
            Map modificationsTerms = olsClient.getTermsByName(name, MOD_ONTOLOGY_LABEL, false);
            if (modificationsTerms.getItem() != null) {
                String tempAccession = null;
                //iterate over the modifications
                outerloop:
                for (MapItem mapItem : modificationsTerms.getItem()) {
                    String accession = mapItem.getKey().toString();
                    //get the Xrefs
                    Map termXrefs = olsClient.getTermXrefs(accession, MOD_ONTOLOGY_LABEL);
                    for (MapItem xref : termXrefs.getItem()) {
                        if (StringUtils.containsIgnoreCase(xref.getValue().toString(), unimodAccession)) {
                            if (xref.getValue().toString().equalsIgnoreCase(unimodAccession)) {
                                T foundModification = findModificationByAccession(clazz, accession);
                                if (foundModification != null) {
                                    modification = foundModification;
                                    break outerloop;
                                }
                            } else {
                                //keep track of the next best thing
                                tempAccession = accession;
                            }
                        }
                    }
                }
                if (modification == null && tempAccession != null) {
                    T foundModification = findModificationByAccession(clazz, tempAccession);
                    if (foundModification != null) {
                        modification = foundModification;

                        //add modification to the cache
                        modificationsCache.put(unimodAccession, modification);
                    }
                }
            }
        }

        return modification;
    }

    @Override
    public TypedCvParam findEnzymeByName(String name) {
        TypedCvParam enzyme = null;

        //find the enzyme by name
        Map enzymeTerms = olsClient.getTermsByName(name, MS_ONTOLOGY_LABEL, false);
        if (enzymeTerms.getItem() != null) {
            for (MapItem mapItem : enzymeTerms.getItem()) {
                String enzymeName = mapItem.getValue().toString();
                if (enzymeName.equalsIgnoreCase(name)) {
                    enzyme = CvParamFactory.newTypedCvInstance(CvParamType.SEARCH_PARAM_ENZYME, MS_ONTOLOGY, MS_ONTOLOGY_LABEL, mapItem.getKey().toString(), enzymeName);
                    break;
                }
            }
        }
        return enzyme;
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
