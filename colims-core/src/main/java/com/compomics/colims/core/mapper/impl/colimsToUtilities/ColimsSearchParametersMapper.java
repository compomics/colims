package com.compomics.colims.core.mapper.impl.colimsToUtilities;

import com.compomics.colims.core.mapper.Mapper;

import org.apache.log4j.Logger;

import com.compomics.colims.model.SearchParameterSettings;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.massspectrometry.Charge;
import java.io.File;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("colimsSearchParametersMapper")
@Transactional
public class ColimsSearchParametersMapper implements Mapper<SearchParameterSettings, SearchParameters> {

    private static final Logger LOGGER = Logger.getLogger(ColimsSearchParametersMapper.class);

    /**
     * Map the ColimsSearchParametersSettings to the SearchParameters object.
     *
     * @param colimsSearchParametersSettings
     * @param searchParameters
     */
    @Override
    public void map(SearchParameterSettings colimsSearchParametersSettings, SearchParameters searchParameters) {

        searchParameters.setFastaFile(new File(colimsSearchParametersSettings.getFastaDb().getName()));
        //TODO FIX THE ENZYME WITH PREDEFINED SET FOR COLIMS?
        Enzyme enzyme = new Enzyme(0, colimsSearchParametersSettings.getEnzyme(), "", "", "", "");
        searchParameters.setEnzyme(enzyme);
        searchParameters.setMaxEValue(colimsSearchParametersSettings.getEvalueCutoff());
        searchParameters.setFragmentIonAccuracy(colimsSearchParametersSettings.getFragMassTolerance());

        searchParameters.setIonSearched1(getCorrectLetter(colimsSearchParametersSettings.getFragmentIon1Type()));
        searchParameters.setIonSearched2(getCorrectLetter(colimsSearchParametersSettings.getFragmentIon2Type()));

        searchParameters.setnMissedCleavages(colimsSearchParametersSettings.getMaxMissedCleavages());
        searchParameters.setPrecursorAccuracy(colimsSearchParametersSettings.getPrecMassTolerance());
        searchParameters.setPrecursorAccuracyType(colimsSearchParametersSettings.getPrecMassToleranceUnit());
        searchParameters.setMaxChargeSearched(new Charge(1, colimsSearchParametersSettings.getPrecursorUpperCharge()));
        searchParameters.setMinChargeSearched(new Charge(1, colimsSearchParametersSettings.getPrecursorLowerCharge()));

    }

    /**
     * Converts a fragmentIonType as int to the corresponding letter (0=a 1=b
     * 2=c | 3=x 4=y 5=z)
     *
     * @param fragmentIonType
     */
    private String getCorrectLetter(int fragmentIonType) {
        String ionLetter = "a";
        switch (fragmentIonType) {
            case 0:
                ionLetter = "a";
                break;
            case 1:
                ionLetter = "b";
                break;
            case 2:
                ionLetter = "c";
                break;
            case 3:
                ionLetter = "x";
                break;
            case 4:
                ionLetter = "y";
                break;
            case 5:
                ionLetter = "z";
                break;
        }
        return ionLetter;
    }

}
