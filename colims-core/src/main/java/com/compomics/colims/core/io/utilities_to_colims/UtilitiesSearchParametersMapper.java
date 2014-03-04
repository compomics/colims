package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.io.Mapper;

import org.apache.log4j.Logger;

import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.SearchParameterSettings;
import com.compomics.util.experiment.identification.SearchParameters;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("utilitiesSearchParametersMapper")
public class UtilitiesSearchParametersMapper implements Mapper<SearchParameters, SearchParameterSettings> {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesSearchParametersMapper.class);
   
    /**
     * Map the SearchParameters to the Colims object.
     *
     * @param colimsSearchParametersSettings
     * @param searchParameters
      */
    @Override
    public void map(final SearchParameters searchParameters, final SearchParameterSettings colimsSearchParametersSettings) {
        FastaDb fastaDb = new FastaDb();
        fastaDb.setFileName(searchParameters.getFastaFile().getName());
        //TODO GET THE CHECKSUM
        colimsSearchParametersSettings.setEnzyme(searchParameters.getEnzyme().getName());
        colimsSearchParametersSettings.setEvalueCutoff(searchParameters.getMaxEValue());
        colimsSearchParametersSettings.setFastaDb(fastaDb);
        colimsSearchParametersSettings.setFragMassTolerance(searchParameters.getFragmentIonAccuracy());
        //TODO this might be wrong !!!!
        colimsSearchParametersSettings.setFragMassToleranceUnit(searchParameters.getPrecursorAccuracyType());
        colimsSearchParametersSettings.setFragmentIon1Type(searchParameters.getIonSearched1());
        colimsSearchParametersSettings.setFragmentIon2Type(searchParameters.getIonSearched2());
        colimsSearchParametersSettings.setHitlistLength(searchParameters.getHitListLength());
        colimsSearchParametersSettings.setMaxMissedCleavages(searchParameters.getnMissedCleavages());
        colimsSearchParametersSettings.setPrecMassTolerance(searchParameters.getPrecursorAccuracy());
        colimsSearchParametersSettings.setPrecMassToleranceUnit(searchParameters.getPrecursorAccuracyType());
        colimsSearchParametersSettings.setPrecursorUpperCharge((searchParameters.getMaxChargeSearched().value) * searchParameters.getMaxChargeSearched().sign);
        colimsSearchParametersSettings.setPrecursorLowerCharge((searchParameters.getMinChargeSearched().value) * searchParameters.getMinChargeSearched().sign);
        //TODO search engine settings?
    }

   
}
