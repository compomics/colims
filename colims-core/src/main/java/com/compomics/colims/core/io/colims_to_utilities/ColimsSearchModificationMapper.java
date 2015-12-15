package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.core.io.Mapper;
import com.compomics.colims.model.SearchParametersHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class maps a collection of Colims SearchParametersHasModification instances onto a Utilities PtmSettings
 * instance.
 *
 * @author Niels Hulstaert
 */
@Component("colimsSearchModificationMapper")
public class ColimsSearchModificationMapper implements Mapper<List<SearchParametersHasModification>, PtmSettings> {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = Logger.getLogger(ColimsSearchModificationMapper.class);

    /**
     * Map the Colims SearchParametersHasModification instances onto the Utilities PtmSettings instance.
     *
     * @param searchParametersHasModifications the list of SearchParametersHasModification instances
     * @param ptmSettings                      the PtmSettings instance
     */
    @Override
    public void map(final List<SearchParametersHasModification> searchParametersHasModifications, PtmSettings ptmSettings) {
        searchParametersHasModifications.stream().forEach((searchParametersHasModification) -> {
            String theoreticPTM = searchParametersHasModification.getSearchModification().getUtilitiesName();
            PTM ptm = PTMFactory.getInstance().getPTM(theoreticPTM);
            if (ptm.equals(PTMFactory.unknownPTM)) {
                boolean isVariable = searchParametersHasModification.getModificationType().equals(ModificationType.VARIABLE);
                if (isVariable) {
                    ptmSettings.addVariableModification(ptm);
                } else {
                    ptmSettings.addFixedModification(ptm);
                }
            }
        });
    }
}
