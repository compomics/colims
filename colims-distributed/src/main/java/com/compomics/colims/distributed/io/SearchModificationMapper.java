package com.compomics.colims.distributed.io;

import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.core.service.SearchModificationService;
import com.compomics.colims.distributed.io.unimod.UnimodMarshaller;
import com.compomics.colims.model.SearchModification;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class maps modifications from input search parameters onto
 * Colims {@link SearchModification} related classes.
 *
 * @author Niels Hulstaert
 */
@SuppressWarnings("ConstantConditions")
@Component("searchModificationMapper")
public class SearchModificationMapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(SearchModificationMapper.class);

    private static final String UNIMOD_PREFIX = "UNIMOD";
    private static final String MOD_PREFIX = "MOD";
    private static final String UNKNOWN_UTILITIES_PTM = "unknown";

    /**
     * The modification service instance.
     */
    private final SearchModificationService searchModificationService;
    /**
     * The Ontology Lookup Service service.
     */
    private final OlsService olsService;
    /**
     * Contains the UNIMOD modifications.
     */
    private final UnimodMarshaller unimodMarshaller;
    /**
     * The map of cached modifications (key: modification name, value: the
     * search modification).
     */
    private final Map<String, SearchModification> cachedSearchModifications = new HashMap<>();

    @Autowired
    public SearchModificationMapper(SearchModificationService searchModificationService, OlsService olsService, UnimodMarshaller unimodMarshaller) {
        this.searchModificationService = searchModificationService;
        this.olsService = olsService;
        this.unimodMarshaller = unimodMarshaller;
    }

    /**
     * Clear resources after usage.
     */
    public void clear() {
        cachedSearchModifications.clear();
        //clear the cache from the OlsService as well
        olsService.getModificationsCache().clear();
    }

    /**
     * Map the given ontology term to a SearchModification instance.
     * Returns null if no mapping was possible.
     *
     * @param ontologyPrefix the ontology prefix
     * @param accession      the term accession
     * @param label          the term label
     * @param value          the term value
     * @param utilitiesName  the Compomics Utilities modification name
     * @return the Colims SearchModification entity
     */
    public SearchModification mapByOntologyTerm(final String ontologyPrefix, final String accession, final String label, final String value, String utilitiesName) {
        SearchModification searchModification;

        //look for the search modification in the cached search modifications map
        searchModification = cachedSearchModifications.get(accession);

        if (searchModification == null) {
            //the search modification was not found in the cachedSearchModifications map
            //look for the search modification in the database by accession
            searchModification = searchModificationService.findByAccession(accession);

            if (searchModification == null) {
                //the search modification was not found in the database
                switch (ontologyPrefix) {
                    case UNIMOD_PREFIX:
                        //look for the search modification in the UNIMOD modifications
                        searchModification = unimodMarshaller.getModificationByAccession(SearchModification.class, accession);
                        break;
                    case MOD_PREFIX: {
                        try {
                            //look for the search modification in the PSI-MOD ontology by accession
                            searchModification = olsService.findModificationByAccession(SearchModification.class, accession);
                        } catch (RestClientException | IOException ex) {
                            //log exception and continue
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }
                    break;
                    default:
                        throw new IllegalStateException("Should not be able to get here.");
                }

                if (searchModification == null) {
                    searchModification = new SearchModification(accession, label);

                    if (value != null) {
                        //@todo check if the PTM mass is the average or the monoisotopic mass shift
                        searchModification.setMonoIsotopicMassShift(Double.valueOf(value));
                    }
                }

                //if available, set the utilities name
                if (utilitiesName != null && !utilitiesName.isEmpty()) {
                    searchModification.setUtilitiesName(utilitiesName);
                }
            }
            //add to cached modifications with the accession as key
            cachedSearchModifications.put(accession, searchModification);
        }

        return searchModification;
    }

    /**
     * Map the given ontology term to a SearchModification instance.
     * Return null if no mapping was possible.
     *
     * @param ontologyPrefix the ontology prefix
     * @param accession      the term accession
     * @param label          the term label
     * @return the Colims SearchModification entity
     */
    public SearchModification mapByOntologyTerm(final String ontologyPrefix, final String accession, final String label) {
        return mapByOntologyTerm(ontologyPrefix, accession, label, null, null);
    }

    /**
     * Map the given modification name to a SearchModification instance. Return
     * null if no mapping was possible.
     *
     * @param modificationName the modification name
     * @return the Colims SearchModification instance
     */
    public SearchModification mapByName(final String modificationName) {
        SearchModification searchModification;

        //look for the search modification in the cached search modifications map
        searchModification = cachedSearchModifications.get(modificationName);

        if (searchModification == null) {
            //the search modification was not found in the cached search modifications map
            //look for the search modification in the database by name
            searchModification = searchModificationService.findByName(modificationName);

            if (searchModification == null) {
                //the search modification was not found by name in the database
                //try to find it in the UNIMOD modifications
                searchModification = unimodMarshaller.getModificationByName(SearchModification.class, modificationName);

                if (searchModification == null) {
                    //the search modification was not found in the UNIMOD ontology
                    //look for the search modification in the PSI-MOD ontology by exact name
                    try {
                        searchModification = olsService.findModificationByExactName(SearchModification.class, modificationName);
                    } catch (IOException e) {
                        //log exception and continue
                        LOGGER.error(e.getMessage(), e);
                    }

                    if (searchModification == null) {
                        //the search modification was not found in any ontology
                        //look for a matching PTM in the PTMFactory
                        PTM ptm = PTMFactory.getInstance().getPTM(modificationName);

                        searchModification = new SearchModification(modificationName);
                        if (ptm.getName().equals(UNKNOWN_UTILITIES_PTM)) {
                            LOGGER.warn("The modification " + modificationName + " could not be mapped to any resource.");
                        } else {
                            if (ptm.getCvTerm() != null) {
                                //map by the CV term if it's not null
                                searchModification = mapByOntologyTerm(
                                        ptm.getCvTerm().getOntology(),
                                        ptm.getCvTerm().getAccession(),
                                        ptm.getCvTerm().getName(),
                                        ptm.getCvTerm().getValue(),
                                        ptm.getName());
                            } else {
                                searchModification.setUtilitiesName(ptm.getName());
                                searchModification.setMonoIsotopicMassShift(ptm.getMass());
                                searchModification.setAverageMassShift(ptm.getMass());
                            }
                        }
                    }
                }
            }
            //add to cached search modifications
            cachedSearchModifications.put(searchModification.getName(), searchModification);
        }

        return searchModification;
    }

}
