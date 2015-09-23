package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class maps a collection of Colims PeptideHasModification instances onto a collection of Utilities
 * ModificationMatch instances.
 *
 * @author Niels Hulstaert
 */
@Component("colimsModificationMapper")
public class ColimsModificationMapper {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = Logger.getLogger(ColimsModificationMapper.class);

    /**
     * Map the Colims PeptideHasModification instances onto the Utilities ModificationMatch instances.
     *
     * @param peptideHasModifications the list of PeptideHasModification instances
     * @param modificationMatches     the list of modification matches
     */
    public void map(final List<PeptideHasModification> peptideHasModifications, final ArrayList<ModificationMatch> modificationMatches) {
        for (PeptideHasModification peptideHasModification : peptideHasModifications) {
            String theoreticPTM = peptideHasModification.getModification().getAccession();

            if (theoreticPTM != null) {
                boolean isVariable = peptideHasModification.getModificationType().equals(ModificationType.VARIABLE);
                int modificationSite = peptideHasModification.getLocation();
                ModificationMatch match = new ModificationMatch(theoreticPTM, isVariable, modificationSite);
                modificationMatches.add(match);
            }
        }
    }
}
