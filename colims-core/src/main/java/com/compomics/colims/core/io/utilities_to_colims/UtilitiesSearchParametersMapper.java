package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.io.Mapper;

import org.apache.log4j.Logger;

import com.compomics.colims.model.SearchParameterSettings;
import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.identification.SearchParameters;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 * @author Niels Hulstaert
 */
@Component("utilitiesSearchParameterSMapper")
public class UtilitiesSearchParametersMapper implements Mapper<SearchParameters, SearchParameterSettings> {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesSearchParametersMapper.class);

    /**
     * Map the Utilities SearchParameters to the colims SearchParameterSettings.
     *
     * @param utilitiesSearchParameters
     * @param searchParameterSettings
     */
    @Override
    public void map(final SearchParameters utilitiesSearchParameters, final SearchParameterSettings searchParameterSettings) {
        //enzyme
        Enzyme enzyme = utilitiesSearchParameters.getEnzyme();
        searchParameterSettings.setEnzyme(enzyme.getName().toLowerCase());
        //number of missed cleavages
        searchParameterSettings.setNumberOfMissedCleavages(utilitiesSearchParameters.getnMissedCleavages());
        //precursor mass tolerance unit
        MassAccuracyType precursorMassAccuracyType = MassAccuracyType.getByUtilitiesMassAccuracyType(utilitiesSearchParameters.getPrecursorAccuracyType());
        searchParameterSettings.setPrecMassToleranceUnit(precursorMassAccuracyType);
        //precursor mass tolerance
        searchParameterSettings.setPrecMassTolerance(utilitiesSearchParameters.getPrecursorAccuracy());
        //precursor lower charge
        searchParameterSettings.setLowerCharge(utilitiesSearchParameters.getMinChargeSearched().value);
        //precursor upper charge
        searchParameterSettings.setUpperCharge(utilitiesSearchParameters.getMaxChargeSearched().value);
        //fragment mass tolerance unit
        MassAccuracyType fragmentMassAccuracyType = MassAccuracyType.getByUtilitiesMassAccuracyType(utilitiesSearchParameters.getFragmentAccuracyType());
        searchParameterSettings.setFragMassToleranceUnit(fragmentMassAccuracyType);
        //fragment mass tolerance
        searchParameterSettings.setFragMassTolerance(utilitiesSearchParameters.getFragmentIonAccuracy());
        //fragment ion type 1
        searchParameterSettings.setFirstSearchedIonType(utilitiesSearchParameters.getIonSearched1());
        //fragment ion type 2
        searchParameterSettings.setSecondSearchedIonType(utilitiesSearchParameters.getIonSearched2());
    }

}
