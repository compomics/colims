package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.ModificationMappingException;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.core.service.SearchModificationService;
import com.compomics.colims.model.SearchModification;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.SearchParametersHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.pride.CvTerm;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This class maps the Compomics Utilities modification related classes to Colims modification related classes.
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
    private OlsService olsService;
    /**
     * The map of cached modifications (key: modification name, value: the search modification).
     */
    private final Map<String, SearchModification> cachedSearchModifications = new HashMap<>();

    /**
     * Map the utilities modification profile to the Colims search parameters. The Utilities PTMs are matched first onto
     * CV terms from PSI-MOD.
     *
     * @param ptmSettings      the Utilities modification profile with the modifications used for the searches.
     * @param searchParameters the Colims search parameters
     * @throws ModificationMappingException thrown in case of a modification mapping problem
     */
    public void map(final PtmSettings ptmSettings, final SearchParameters searchParameters) throws ModificationMappingException {
        //iterate over fixed modifications
        for (String modificationName : ptmSettings.getAllModifications()) {
            //try to find the CV term in the PTMFactory
            CvTerm cvTerm = PTMFactory.getInstance().getPTM(modificationName).getCvTerm();

            SearchModification searchModification;
            if (cvTerm != null) {
                searchModification = mapCvTerm(cvTerm);
            } else {
                //make use of the PTM in the backed up PTMs map because it contains the mass of the modification
                searchModification = mapPtm(ptmSettings.getBackedUpPtmsMap().get(modificationName));
            }

            //set entity associations if the search modification could be mapped
            if (searchModification != null) {
                SearchParametersHasModification searchParametersHasModification = new SearchParametersHasModification();

                //set modification type
                if (ptmSettings.getAllNotFixedModifications().contains(modificationName)) {
                    searchParametersHasModification.setModificationType(ModificationType.VARIABLE);
                } else {
                    searchParametersHasModification.setModificationType(ModificationType.FIXED);
                }

                //set entity associations
                searchParametersHasModification.setSearchModification(searchModification);
                searchParametersHasModification.setSearchParameters(searchParameters);

                searchParameters.getSearchParametersHasModifications().add(searchParametersHasModification);
            } else {
                LOGGER.error("The search modification" + modificationName + " could not be mapped.");
                throw new ModificationMappingException("The search modification match " + modificationName + " could not be mapped.");
            }
        }

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
     * Map the given CvTerm utilities object to a SearchModification instance. Return null if no mapping was possible.
     *
     * @param cvTerm the utilities CvTerm
     * @return the Colims SearchModification entity
     */
    private SearchModification mapCvTerm(final CvTerm cvTerm) {
        SearchModification searchModification;

        //look for the search modification in the newModifications map
        searchModification = cachedSearchModifications.get(cvTerm.getAccession());

        if (searchModification == null) {
            //the search modification was not found in the cachedSearchModifications map
            //look for the search modification in the database by accession
            searchModification = searchModificationService.findByAccession(cvTerm.getAccession());
            //for UNIMOD mods, look for the alternative accession
            if (cvTerm.getOntology().equals("UNIMOD") && searchModification == null) {
                //look for the search modification in the database by alternative accession
                searchModification = searchModificationService.findByAlternativeAccession(cvTerm.getAccession());
            }

            if (searchModification == null) {
                //the search modification was not found in the database
                switch (cvTerm.getOntology()) {
                    case "PSI-MOD":
                        //look for the search modification in the PSI-MOD ontology by accession
                        searchModification = olsService.findModificationByAccession(SearchModification.class, cvTerm.getAccession());
                        if (searchModification != null) {
                            //add to cached search modifications with the PSI-MOD accession as key
                            cachedSearchModifications.put(searchModification.getAccession(), searchModification);
                        }
                        break;
                    case "UNIMOD":
                        //look for the search modification in the PSI-MOD ontology by name and UNIMOD accession
                        searchModification = olsService.findModificationByNameAndUnimodAccession(SearchModification.class, cvTerm.getName(), cvTerm.getAccession());
                        if (searchModification != null) {
                            //add to cached search modifications with the UNIMOD accession as key
                            cachedSearchModifications.put(cvTerm.getAccession(), searchModification);
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

        return searchModification;
    }

    /**
     * Map the given PTM to a SearchModification instance. Return null if no mapping was possible.
     *
     * @param ptm the PTM instance
     * @return the Colims SearchModification instance
     */
    private SearchModification mapPtm(final PTM ptm) {
        SearchModification searchModification;

        //look for the search modification in the newModifications map
        searchModification = cachedSearchModifications.get(ptm.getName());

        if (searchModification == null) {
            //the search modification was not found in the newModifications map
            //look for the search modification in the database by name
            searchModification = searchModificationService.findByName(ptm.getName());

            if (searchModification == null) {
                //the search modification was not found in the database
                //look for the search modification in the PSI-MOD ontology by exact name
                searchModification = olsService.findModificationByExactName(SearchModification.class, ptm.getName());

                if (searchModification == null) {
                    //the search modification was not found by name in the PSI-MOD ontology
                    //@todo maybe search by mass or not by 'exact' name?
                    //get the search modification from modification factory
                    PTM foundPtm = PTMFactory.getInstance().getPTM(ptm.getName());

                    //check if the PTM is not unknown in the PTMFactory
                    if (!foundPtm.getName().equals(UNKNOWN_UTILITIES_PTM)) {
                        LOGGER.warn("The modification match " + ptm.getName() + " could not be found in the PtmFactory.");
                    }

                    searchModification = new SearchModification(ptm.getName());
                    //@todo check if the PTM mass is the average or the monoisotopic mass shift
                    searchModification.setMonoIsotopicMassShift(ptm.getMass());
                    searchModification.setAverageMassShift(ptm.getMass());
                }
            }
            //add to cached search modifications
            cachedSearchModifications.put(searchModification.getName(), searchModification);
        }

        return searchModification;
    }

}
