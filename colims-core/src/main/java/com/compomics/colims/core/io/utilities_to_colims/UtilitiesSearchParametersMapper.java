package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.io.Mapper;
import com.compomics.colims.core.service.TypedCvParamService;

import org.apache.log4j.Logger;

import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.util.experiment.biology.Enzyme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class maps the Utilities search parameters to the Colims search
 * parameters.
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

    /**
     * The TypedCvParam class service.
     */
    @Autowired
    private TypedCvParamService typedCvParamService;

    /**
     * Map the Utilities SearchParameters to the Colims SearchParameters.
     *
     * @param utilitiesSearchParameters the Utilities search parameters
     * @param searchParameters the Colims search parameters
     */
    @Override
    public void map(final com.compomics.util.experiment.identification.SearchParameters utilitiesSearchParameters, final SearchParameters searchParameters) {
        //enzyme
        Enzyme enzyme = utilitiesSearchParameters.getEnzyme();
        //searchParameterSettings.setEnzyme(enzyme.getName().toLowerCase());
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

}
