package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.ModificationMappingException;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.core.service.SearchModificationService;
import com.compomics.colims.distributed.io.unimod.UnimodMarshaller;
import com.compomics.colims.model.SearchModification;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.SearchParametersHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.biology.AminoAcidPattern;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.pride.CvTerm;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.springframework.web.client.RestClientException;

/**
 * This class maps the Compomics Utilities modification related classes to
 * Colims modification related classes.
 *
 * @author Niels Hulstaert
 */
@SuppressWarnings("ConstantConditions")
@Component("utilitiesPtmSettingsMapper")
public class UtilitiesPtmSettingsMapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(UtilitiesPtmSettingsMapper.class);
    private static final String UNKNOWN_UTILITIES_PTM = "unknown";

    /**
     * The modification service instance.
     */
    @Autowired
    private SearchModificationService searchModificationService;
    /**
     * The Ontology Lookup Service service.
     */
    @Autowired
    private OlsService newOlsService;
    /**
     * Contains the UNIMOD modifications.
     */
    @Autowired
    private UnimodMarshaller unimodMarshaller;
    /**
     * The map of cached modifications (key: modification name, value: the
     * search modification).
     */
    private final Map<String, SearchModification> cachedSearchModifications = new HashMap<>();

    /**
     * Map the utilities modification profile to the Colims search parameters.
     * The Utilities PTMs are matched first onto CV terms from PSI-MOD.
     *
     * @param ptmSettings the Utilities modification profile with the
     * modifications used for the searches.
     * @param searchParameters the Colims search parameters
     * @throws ModificationMappingException thrown in case of a modification
     * mapping problem
     */
    public void map(final PtmSettings ptmSettings, final SearchParameters searchParameters) throws ModificationMappingException {
        //iterate over all modifications
        for (String modificationName : ptmSettings.getAllModifications()) {
            //try to find the PTM and the associated CvTerm in the backed up PTMs
            PTM ptm = ptmSettings.getBackedUpPtmsMap().get(modificationName);
            CvTerm cvTerm = ptm.getCvTerm();

            SearchModification searchModification;
            if (cvTerm != null) {
                searchModification = mapCvTerm(cvTerm);
            } else {
                searchModification = mapByName(modificationName);
            }

            //set entity associations if the search modification could be mapped
            if (searchModification != null) {
                SearchParametersHasModification searchParametersHasModification = new SearchParametersHasModification();

                //set the Utilities name if necessary
                if (searchModification.getUtilitiesName() == null && PTMFactory.getInstance().containsPTM(modificationName)) {
                    searchModification.setUtilitiesName(modificationName);
                }

                //set modification type
                if (ptmSettings.getAllNotFixedModifications().contains(modificationName)) {
                    searchParametersHasModification.setModificationType(ModificationType.VARIABLE);
                } else {
                    searchParametersHasModification.setModificationType(ModificationType.FIXED);
                }

                //set residues
                AminoAcidPattern aminoAcidPattern = ptm.getPattern();
                if (aminoAcidPattern != null) {
                    searchParametersHasModification.setResidues(aminoAcidPattern.asSequence());
                }

                //set entity associations
                searchParametersHasModification.setSearchModification(searchModification);
                searchParametersHasModification.setSearchParameters(searchParameters);

                searchParameters.getSearchParametersHasModifications().add(searchParametersHasModification);
            }
        }

    }

    /**
     * Clear resources after usage.
     */
    public void clear() {
        cachedSearchModifications.clear();
        //clear the cache from the OlsService as well
        newOlsService.getModificationsCache().clear();
    }

    /**
     * Map the given CvTerm utilities object to a SearchModification instance.
     * Return null if no mapping was possible.
     *
     * @param cvTerm the utilities CvTerm
     * @return the Colims SearchModification entity
     */
    private SearchModification mapCvTerm(final CvTerm cvTerm) throws ModificationMappingException {
        SearchModification searchModification;

        //look for the search modification in the cached search modifications map
        searchModification = cachedSearchModifications.get(cvTerm.getAccession());

        if (searchModification == null) {
            //the search modification was not found in the cachedSearchModifications map
            //look for the search modification in the database by accession
            searchModification = searchModificationService.findByAccession(cvTerm.getAccession());

            if (searchModification == null) {
                //the search modification was not found in the database
                switch (cvTerm.getOntology()) {
                    case "UNIMOD":
                        //look for the search modification in the UNIMOD modifications
                        searchModification = unimodMarshaller.getModificationByName(SearchModification.class, cvTerm.getName());
                        //@todo uncomment this as soon the new OLS service support this again
//                        if (searchModification == null) {
//                            //look for the search modification in the PSI-MOD ontology by name and UNIMOD accession
//                            searchModification = olsService.findModificationByNameAndUnimodAccession(SearchModification.class, cvTerm.getName(), cvTerm.getAccession());
//                        }
                        if (searchModification != null) {
                            //add to cached modifications with the UNIMOD accession as key
                            cachedSearchModifications.put(cvTerm.getAccession(), searchModification);
                        }
                        break;
                    case "PSI-MOD": {
                        try {
                            //look for the search modification in the PSI-MOD ontology by accession
                            searchModification = newOlsService.findModificationByAccession(SearchModification.class, cvTerm.getAccession());
                        } catch (RestClientException | IOException ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }
                    if (searchModification != null) {
                        //add to cached search modifications with the PSI-MOD accession as key
                        cachedSearchModifications.put(searchModification.getAccession(), searchModification);
                    }
                    break;
                    default:
                        throw new IllegalStateException("Should not be able to get here.");
                }

                if (searchModification == null) {
                    searchModification = new SearchModification(cvTerm.getAccession(), cvTerm.getName());

                    //@todo check if the PTM mass is the average or the monoisotopic mass shift
                    searchModification.setMonoIsotopicMassShift(Double.valueOf(cvTerm.getValue()));

                    //add to cached search modifications
                    cachedSearchModifications.put(searchModification.getAccession(), searchModification);
                }
            } else {
                cachedSearchModifications.put(cvTerm.getAccession(), searchModification);
            }
        }

        if (searchModification == null) {
            LOGGER.error("The Utilities CvTerm " + cvTerm.getAccession() + " could not be mapped.");
            throw new ModificationMappingException("The Utilities CvTerm " + cvTerm.getAccession() + " could not be mapped.");
        }

        return searchModification;
    }

    /**
     * Map the given modification name to a SearchModification instance. Return
     * null if no mapping was possible.
     *
     * @param modificationName the modification name
     * @return the Colims SearchModification instance
     * @throws com.compomics.colims.core.io.ModificationMappingException in case
     * of a modification mapping problem
     */
    public SearchModification mapByName(final String modificationName) throws ModificationMappingException {
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
                        searchModification = newOlsService.findModificationByExactName(SearchModification.class, modificationName);
                    } catch (IOException e) {
                        //@// TODO: 23/03/16 handle this
                        e.printStackTrace();
                    }

                    if (searchModification == null) {
                        //the search modification was not found in the PSI-MOD ontology
                        //look for a matching PTM in the PTMFactory
                        PTM ptm = PTMFactory.getInstance().getPTM(modificationName);

                        searchModification = new SearchModification(modificationName);
                        if (ptm.getName().equals(UNKNOWN_UTILITIES_PTM)) {
                            LOGGER.warn("The modification match " + modificationName + " could not be found in the PTMFactory.");
                        } else {
                            searchModification.setMonoIsotopicMassShift(ptm.getMass());
                            searchModification.setAverageMassShift(ptm.getMass());
                        }
                    }
                }
            }
            //add to cached search modifications
            cachedSearchModifications.put(searchModification.getName(), searchModification);
        }

        if (searchModification == null) {
            LOGGER.error("The modification name " + modificationName + " could not be mapped.");
            throw new ModificationMappingException("The modification name " + modificationName + " could not be mapped.");
        }

        return searchModification;
    }
}
