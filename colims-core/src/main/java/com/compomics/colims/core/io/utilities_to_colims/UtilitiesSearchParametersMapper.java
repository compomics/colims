package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.io.Mapper;

import org.apache.log4j.Logger;

import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.SearchParameterSettings;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.identification.SearchParameters;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 * @author Niels Hulstaert
 */
@Component("utilitiesSearchParametersMapper")
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
        //precursor mass tolerance
        SearchParameters.MassAccuracyType precursorAccuracyType = utilitiesSearchParameters.getPrecursorAccuracyType();        
//        searchParameterSettings.setPrecMassTolerance();
        //precursor mass tolerance unit
//        searchParameterSettings.setPrecMassToleranceUnit(utilitiesSearchParameters.getPrecMassToleranceUnit());
//        //precursor lower charge
//        searchParameterSettings.setPrecursorLowerCharge(utilitiesSearchParameters.getPrecursorLowerCharge());
//        //precursor upper charge
//        searchParameterSettings.setPrecursorUpperCharge(utilitiesSearchParameters.getPrecursorUpperCharge());
//        //fragment mass tolerance
//        searchParameterSettings.setFragMassTolerance(utilitiesSearchParameters.getFragMassTolerance());
//        //fragment mass tolerance unit
//        searchParameterSettings.setPrecMassToleranceUnit(utilitiesSearchParameters.getFragMassToleranceUnit());
//        //fragment ion type 1
//        searchParameterSettings.setFragmentIon1Type(utilitiesSearchParameters.getFragmentIon1Type());
//        //fragment ion type 2
//        searchParameterSettings.setFragmentIon2Type(utilitiesSearchParameters.getFragmentIon2Type());
//        //hitlist length
//        searchParameterSettings.setHitlistLength(utilitiesSearchParameters.getHitlistLength());
    }

}
