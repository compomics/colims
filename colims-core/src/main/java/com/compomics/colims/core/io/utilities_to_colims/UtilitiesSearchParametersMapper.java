package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.io.Mapper;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.core.service.TypedCvParamService;

import com.compomics.colims.model.Modification;
import com.compomics.colims.model.SearchCvParam;
import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.model.factory.CvParamFactory;
import com.compomics.util.experiment.biology.PTM;
import org.apache.log4j.Logger;

import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.util.experiment.biology.Enzyme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class maps the Utilities search parameters to the Colims search parameters.
 *
 * @author Kenneth Verheggen
 * @author Niels Hulstaert
 */
@Component("utilitiesSearchParametersMapper")
public class UtilitiesSearchParametersMapper implements Mapper<com.compomics.util.experiment.identification.SearchParameters, SearchParameters> {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(UtilitiesSearchParametersMapper.class);
    private static final String MS_ONTOLOGY_LABEL = "MS";
    private static final String MS_ONTOLOGY = "PSI Mass Spectrometry Ontology [MS]";
    private static final String NOT_APPLICABLE = "N/A";

    /**
     * The TypedCvParam class service.
     */
    @Autowired
    private TypedCvParamService typedCvParamService;
    @Autowired
    private OlsService olsService;

    /**
     * Map the Utilities SearchParameters to the Colims SearchParameters.
     *
     * @param utilitiesSearchParameters the Utilities search parameters
     * @param searchParameters          the Colims search parameters
     */
    @Override
    public void map(final com.compomics.util.experiment.identification.SearchParameters utilitiesSearchParameters, final SearchParameters searchParameters) {
        //map Utilities enzyme to a TypedCvParam instance
        TypedCvParam enzyme = mapEnzyme(utilitiesSearchParameters.getEnzyme());
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

        }

        return enzyme;
    }

}
