package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.Mapper;
import com.compomics.colims.core.service.TypedCvParamService;
import com.compomics.colims.distributed.io.SearchModificationMapper;
import com.compomics.colims.model.SearchCvParam;
import com.compomics.colims.model.SearchModification;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.SearchParametersHasModification;
import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.preferences.IdentificationParameters;
import com.compomics.util.pride.CvTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class maps the Utilities search parameters onto the Colims search
 * parameters.
 *
 * @author Kenneth Verheggen
 * @author Niels Hulstaert
 */
@Component("utilitiesSearchParametersMapper")
public class UtilitiesSearchParametersMapper implements Mapper<IdentificationParameters, SearchParameters> {

    private static final String DEFAULT_SEARCH_TYPE_ACCESSION = "MS:1001083";

    /**
     * The default search type, for the moment this is fixed to "ms-ms search".
     */
    private SearchCvParam defaultSearchType = null;
    /**
     * The Utilities PTM settings mapper.
     */
    private final SearchModificationMapper searchModificationMapper;
    /**
     * The TypedCvParam class service.
     */
    private final TypedCvParamService typedCvParamService;

    @Autowired
    public UtilitiesSearchParametersMapper(SearchModificationMapper searchModificationMapper, TypedCvParamService typedCvParamService) {
        this.searchModificationMapper = searchModificationMapper;
        this.typedCvParamService = typedCvParamService;
    }

    /**
     * Map the Utilities SearchParameters to the Colims SearchParameters.
     *
     * @param identificationParameters the Utilities identification parameters
     * @param searchParameters         the Colims search parameters
     */
    @Override
    public void map(final IdentificationParameters identificationParameters, final SearchParameters searchParameters) {
        //set the default search type
        searchParameters.setSearchType(defaultSearchType);
        //get the FDR values
        searchParameters.setPsmThreshold(identificationParameters.getIdValidationPreferences().getDefaultPsmFDR());
        searchParameters.setPeptideThreshold(identificationParameters.getIdValidationPreferences().getDefaultPeptideFDR());
        searchParameters.setProteinThreshold(identificationParameters.getIdValidationPreferences().getDefaultProteinFDR());
        //get the Utilities search parameters
        com.compomics.util.experiment.identification.identification_parameters.SearchParameters utilitiesSearchParameters = identificationParameters.getSearchParameters();
        //map Utilities enzymes and associated number of missed cleavages
        if (utilitiesSearchParameters.getDigestionPreferences().hasEnzymes()) {
            List<String> enzymeList = utilitiesSearchParameters.getDigestionPreferences().getEnzymes()
                    .stream()
                    .map(Enzyme::getName)
                    .collect(Collectors.toList());
            List<Integer> missedCleavages = new ArrayList<>();
            enzymeList.stream()
                    .forEach(enzyme -> missedCleavages.add(utilitiesSearchParameters.getDigestionPreferences().getnMissedCleavages(enzyme)));

            searchParameters.setEnzymes(enzymeList.stream().collect(Collectors.joining(SearchParameters.DELIMITER)));
            searchParameters.setNumberOfMissedCleavages(missedCleavages.stream().map(Object::toString).collect(Collectors.joining(SearchParameters.DELIMITER)));
        }
        //precursor mass tolerance unit
        MassAccuracyType precursorMassAccuracyType = MassAccuracyType.getByUtilitiesMassAccuracyType(utilitiesSearchParameters.getPrecursorAccuracyType());
        searchParameters.setPrecMassToleranceUnit(precursorMassAccuracyType);
        //precursor mass tolerance
        searchParameters.setPrecMassTolerance(utilitiesSearchParameters.getPrecursorAccuracy());
        //precursor lower charge
        searchParameters.setLowerCharge(utilitiesSearchParameters.getMinChargeSearched().value);
        //precursor upper charge
        searchParameters.setUpperCharge(utilitiesSearchParameters.getMaxChargeSearched().value);
        //fragment mass tolerance unit
        MassAccuracyType fragmentMassAccuracyType = MassAccuracyType.getByUtilitiesMassAccuracyType(utilitiesSearchParameters.getFragmentAccuracyType());
        searchParameters.setFragMassToleranceUnit(fragmentMassAccuracyType);
        //fragment mass tolerance
        searchParameters.setFragMassTolerance(utilitiesSearchParameters.getFragmentIonAccuracy());
        //searched fragment ions
        List<String> forwardIons = utilitiesSearchParameters.getForwardIons()
                .stream()
                .map(index -> Arrays.asList(com.compomics.util.experiment.identification.identification_parameters.SearchParameters.implementedForwardIons).get(index))
                .collect(Collectors.toList());
        List<String> rewindIons = utilitiesSearchParameters.getRewindIons()
                .stream()
                .map(index -> Arrays.asList(com.compomics.util.experiment.identification.identification_parameters.SearchParameters.implementedRewindIons).get(index - com.compomics.util.experiment.identification.identification_parameters.SearchParameters.implementedForwardIons.length))
                .collect(Collectors.toList());
        List<String> searchedIons = new ArrayList<>();
        searchedIons.addAll(forwardIons);
        searchedIons.addAll(rewindIons);
        searchParameters.setSearchedIons(searchedIons.stream().collect(Collectors.joining(SearchParameters.DELIMITER)));

        //map the search modifications
        mapSearchModifications(utilitiesSearchParameters.getPtmSettings(), searchParameters);
    }

    /**
     * Get the default search type from the database and assign it to the class
     * field.
     */
    @PostConstruct
    private void getDefaultSearchType() {
        //look for the default search type in the database
        TypedCvParam searchType = typedCvParamService.findByAccession(DEFAULT_SEARCH_TYPE_ACCESSION, CvParamType.SEARCH_TYPE);
        if (searchType != null) {
            defaultSearchType = (SearchCvParam) searchType;
        } else {
            throw new IllegalStateException("The default search type CV term was not found in the database.");
        }
    }

    /**
     * Map the utilities modification profile to the Colims search parameters.
     * The Utilities PTMs are matched first onto CV terms from PSI-MOD.
     *
     * @param ptmSettings      the Utilities modification profile with the modifications used for the searches.
     * @param searchParameters the Colims search parameters
     */
    private void mapSearchModifications(final PtmSettings ptmSettings, final SearchParameters searchParameters) {
        //iterate over all modifications
        for (String modificationName : ptmSettings.getAllModifications()) {
            //try to find the PTM and the associated CvTerm in the backed up PTMs
            PTM ptm = ptmSettings.getBackedUpPtmsMap().get(modificationName);
            CvTerm cvTerm = ptm.getCvTerm();

            SearchModification searchModification;
            if (cvTerm != null) {
                searchModification = searchModificationMapper.mapByOntologyTerm(cvTerm.getOntology(), cvTerm.getAccession(), cvTerm.getName(), cvTerm.getValue(), modificationName);
            } else {
                searchModification = searchModificationMapper.mapByName(modificationName);
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

                //set entity associations
                searchParametersHasModification.setSearchModification(searchModification);
                searchParametersHasModification.setSearchParameters(searchParameters);

                searchParameters.getSearchParametersHasModifications().add(searchParametersHasModification);
            }
        }

    }

}
