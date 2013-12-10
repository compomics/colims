package com.compomics.colims.core.mapper.impl.colimsToUtilities;

import com.compomics.colims.core.mapper.impl.utilitiesToColims.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.pride.CvTerm;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("colimsModificationMapper")
public class ColimsModificationMapper {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesModificationMapper.class);
    private static final String UNKNOWN_UTILITIES_PTM = "unknown";
    @Autowired
    private PtmCvTermMapper ptmCvTermMapper;
    @Autowired
    private ModificationService modificationService;
    @Autowired
    private OlsService olsService;

    /**
     * Map the utilities modification matches onto the colims peptide. The
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
