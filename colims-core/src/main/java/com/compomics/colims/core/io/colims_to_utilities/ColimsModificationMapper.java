package com.compomics.colims.core.io.colims_to_utilities;

import java.util.ArrayList;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author Kenneth Verheggen
 */
@Component("colimsModificationMapper")
public class ColimsModificationMapper {

    private static final Logger LOGGER = Logger.getLogger(ColimsModificationMapper.class);

    /**
     * Map the Colims modification matches onto the Utilities peptide. The Utilities PTMs are matched first onto CV
     * params from PSI-MOD.
     *
     * @param targetPeptide       the Colims target peptide
     * @param modificationMatches the list of modification matches
     */
    public void map(final Peptide targetPeptide, final ArrayList<ModificationMatch> modificationMatches) {
        LOGGER.debug("Mapping modifications from " + targetPeptide.getSequence() + " to new modificationMatches");
        for (PeptideHasModification peptideHasModification : targetPeptide.getPeptideHasModifications()) {
            String theoreticPTM = peptideHasModification.getModification().getAccession();
            boolean isVariable = peptideHasModification.getModificationType().equals(ModificationType.VARIABLE);
            int modificationSite = peptideHasModification.getLocation();
            ModificationMatch match = new ModificationMatch(theoreticPTM, isVariable, modificationSite);
            modificationMatches.add(match);
        }
    }
}
