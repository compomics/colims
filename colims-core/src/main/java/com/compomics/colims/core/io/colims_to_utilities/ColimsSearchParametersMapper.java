package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.util.experiment.massspectrometry.Charge;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class maps a Colims SearchParameters instance onto a Utilities SearchParameters instance.
 *
 * @author Niels Hulstaert
 */
@Component("colimsSearchParametersMapper")
@Transactional
public class ColimsSearchParametersMapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(ColimsSearchParametersMapper.class);

    /**
     * Map the Colims SearchParameters onto the Utilties SearchParameters. This method only maps the fields necessary to
     * annotate the spectrum panel.
     *
     * @param colimsSearchParameters the Colims SearchParameters instance
     * @return the Utilities SearchParameters instance
     */
    public com.compomics.util.experiment.identification.identification_parameters.SearchParameters mapForSpectrumPanel(final SearchParameters colimsSearchParameters) {
        com.compomics.util.experiment.identification.identification_parameters.SearchParameters utilitiesSearchParameters = new com.compomics.util.experiment.identification.identification_parameters.SearchParameters();

        //fragment ion accuracy
        utilitiesSearchParameters.setFragmentAccuracyType(MassAccuracyType.getByColimsMassAccuracyType(colimsSearchParameters.getFragMassToleranceUnit()));
        utilitiesSearchParameters.setFragmentIonAccuracy(colimsSearchParameters.getFragMassTolerance());

        //fragment ions searched
        utilitiesSearchParameters.setIonSearched1(getFragmentIonTypeToString(colimsSearchParameters.getFirstSearchedIonType()));
        utilitiesSearchParameters.setIonSearched2(getFragmentIonTypeToString(colimsSearchParameters.getSecondSearchedIonType()));

        //precursor accuracy
        utilitiesSearchParameters.setPrecursorAccuracyType(MassAccuracyType.getByColimsMassAccuracyType(colimsSearchParameters.getPrecMassToleranceUnit()));
        utilitiesSearchParameters.setPrecursorAccuracy(colimsSearchParameters.getPrecMassTolerance());

        utilitiesSearchParameters.setMinChargeSearched(new Charge(1, colimsSearchParameters.getLowerCharge()));
        utilitiesSearchParameters.setMaxChargeSearched(new Charge(1, colimsSearchParameters.getUpperCharge()));

        return utilitiesSearchParameters;
    }

    /**
     * Converts a fragmentIonType as int to the corresponding letter (0=a 1=b 2=c | 3=x 4=y 5=z)
     *
     * @param fragmentIonType the ion type String represantation
     */
    private String getFragmentIonTypeToString(final int fragmentIonType) {
        String ionLetter;
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
            default:
                throw new IllegalStateException("Should be unreachable.");
        }
        return ionLetter;
    }

}
