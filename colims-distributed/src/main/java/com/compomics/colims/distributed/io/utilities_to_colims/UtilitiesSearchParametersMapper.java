package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.Mapper;
import com.compomics.colims.core.io.ModificationMappingException;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.core.service.TypedCvParamService;
import com.compomics.colims.model.SearchCvParam;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.colims.model.factory.CvParamFactory;
import com.compomics.util.experiment.biology.Enzyme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * This class maps the Utilities search parameters to the Colims search parameters.
 *
 * @author Kenneth Verheggen
 * @author Niels Hulstaert
 */
@Component("utilitiesSearchParametersMapper")
public class UtilitiesSearchParametersMapper implements Mapper<com.compomics.util.experiment.identification.identification_parameters.SearchParameters, SearchParameters> {

    private static final String MS_ONTOLOGY_LABEL = "MS";
    private static final String MS_ONTOLOGY = "PSI Mass Spectrometry Ontology [MS]";
    private static final String NOT_APPLICABLE = "N/A";
    private static final String DEFAULT_SEARCH_TYPE_ACCESSION = "MS:1001083";

    /**
     * The default search type, for the moment this is fixed to "ms-ms search".
     */
    private SearchCvParam defaultSearchType = null;
    /**
     * The Utilities PTM settings mapper.
     */
    @Autowired
    private UtilitiesPtmSettingsMapper utilitiesPtmSettingsMapper;
    /**
     * The TypedCvParam class service.
     */
    @Autowired
    private TypedCvParamService typedCvParamService;
    /**
     * The Ontology Lookup Service service.
     */
    @Autowired
    private OlsService olsService;

    /**
     * Map the Utilities SearchParameters to the Colims SearchParameters.
     *
     * @param utilitiesSearchParameters the Utilities search parameters
     * @param searchParameters          the Colims search parameters
     * @throws com.compomics.colims.core.io.ModificationMappingException in case of a modification mapping problem
     */
    @Override
    public void map(com.compomics.util.experiment.identification.identification_parameters.SearchParameters utilitiesSearchParameters, final SearchParameters searchParameters) throws ModificationMappingException {
        //set the default search type
        searchParameters.setSearchType(defaultSearchType);
        //map Utilities enzyme to a TypedCvParam instance
        TypedCvParam enzyme = null;
        if (utilitiesSearchParameters.getEnzyme() != null) {
            enzyme = mapEnzyme(utilitiesSearchParameters.getEnzyme());
        }
        searchParameters.setEnzyme((SearchCvParam) enzyme);
        //number of missed cleavages
        searchParameters.setNumberOfMissedCleavages(utilitiesSearchParameters.getnMissedCleavages());
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
        //fragment ion type 1
        searchParameters.setFirstSearchedIonType(utilitiesSearchParameters.getIonSearched1());
        //fragment ion type 2
        searchParameters.setSecondSearchedIonType(utilitiesSearchParameters.getIonSearched2());

        //map the PTM settings
        utilitiesPtmSettingsMapper.map(utilitiesSearchParameters.getPtmSettings(), searchParameters);
    }

    /**
     * Map the given Utilities Enzyme instance to a TypedCvParam instance. Return null if no mapping was possible.
     *
     * @param utilitiesEnzyme the Utilities Enzyme instance
     * @return the TypedCvParam instance
     */
    private TypedCvParam mapEnzyme(final Enzyme utilitiesEnzyme) {
        TypedCvParam enzyme;

        //look for the enzyme in the database
        enzyme = typedCvParamService.findByName(utilitiesEnzyme.getName(), CvParamType.SEARCH_PARAM_ENZYME, true);

        if (enzyme == null) {
            //the enzyme was not found by name in the database
            //look for the enzyme in the MS ontology by name
            enzyme = olsService.findEnzymeByName(utilitiesEnzyme.getName());

            if (enzyme == null) {
                //the enzyme was not found by name in the MS ontology
                enzyme = CvParamFactory.newTypedCvInstance(CvParamType.SEARCH_PARAM_ENZYME, MS_ONTOLOGY, MS_ONTOLOGY_LABEL, NOT_APPLICABLE, utilitiesEnzyme.getName());
            }

            //persist the newly created enzyme
            typedCvParamService.persist(enzyme);
        }

        return enzyme;
    }

    /**
     * Get the default search type from the database and assign it to the class field.
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

}
