package com.compomics.colims.core.mapper.impl.colimsToUtilities;

import com.compomics.colims.core.mapper.impl.utilitiesToColims.*;
import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.mapper.MatchScore;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("colimsPeptideMapper")
public class ColimsPeptideMapper {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesPeptideMapper.class);
    @Autowired
    private UtilitiesModificationMapper utilitiesModificationMapper;

    public void map(Peptide sourcePeptide, com.compomics.util.experiment.biology.Peptide targetPeptide) throws MappingException {
        //set sequence
        ArrayList<String> parentProteins = new ArrayList<String>();
        for (PeptideHasProtein aParentProtein : sourcePeptide.getPeptideHasProteins()) {
            parentProteins.add(aParentProtein.getProtein().getAccession());
        }
        //TODO : REVERT THE MODIFICATIONMAPPING !!!!
        ArrayList<ModificationMatch> modifications = new ArrayList<ModificationMatch>();
        for (PeptideHasModification aModification : sourcePeptide.getPeptideHasModifications()) {
            String theoreticPTM = aModification.getModification().getAccession();
            boolean isVariable = aModification.getModificationType().equals(ModificationType.VARIABLE);
            int modificationSite = aModification.getLocation();
            ModificationMatch match = new ModificationMatch(theoreticPTM, isVariable, modificationSite);
            modifications.add(match);
        }
        targetPeptide = new com.compomics.util.experiment.biology.Peptide(sourcePeptide.getSequence(), parentProteins, modifications);

    }
}
