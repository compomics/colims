package com.compomics.colims.core.mapper.impl.colimsToUtilities;

import java.util.ArrayList;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("colimsModificationMapper")
public class ColimsModificationMapper {

    private static final Logger LOGGER = Logger.getLogger(ColimsModificationMapper.class);
    /**
     * Map the colims modification matches onto the utilities peptide. The
     * utilities PTMs are matched first onto CV terms from PSI-MOD.
     *
     * @param modificationMatches the list of modification matches
     * @param ptmScores the PeptideShaker PTM scores
     * @param targetPeptide the colims target peptide
     * @throws MappingException
     */
    
    public void map(Peptide targetPeptide, ArrayList<ModificationMatch> modificationMatches) throws MappingException {
        for (PeptideHasModification pepHasMod : targetPeptide.getPeptideHasModifications()) {
            String theoreticPTM = pepHasMod.getModification().getAccession();
            boolean isVariable = pepHasMod.getModificationType().equals(ModificationType.VARIABLE);
            int modificationSite = pepHasMod.getLocation();
            ModificationMatch match = new ModificationMatch(theoreticPTM, isVariable, modificationSite);
            modificationMatches.add(match);
        }
    }
}
