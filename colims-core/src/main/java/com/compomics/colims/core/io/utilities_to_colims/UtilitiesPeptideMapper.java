package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.model.Peptide;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesPeptideMapper")
public class UtilitiesPeptideMapper {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesPeptideMapper.class);
    @Autowired
    private UtilitiesModificationMapper utilitiesModificationMapper;
    
    /**
     * Map the utilities objects onto the colims Peptide.
     *
     * @param sourcePeptide
     * @param psmMatchScore
     * @param ptmScores
     * @param identificationCharge
     * @param targetPeptide
     * @throws MappingException
     */
    public void map(final com.compomics.util.experiment.biology.Peptide sourcePeptide, final MatchScore psmMatchScore, final PSPtmScores ptmScores, final int identificationCharge, final Peptide targetPeptide) throws MappingException {
        //set sequence
        targetPeptide.setSequence(sourcePeptide.getSequence());
        //set theoretical mass
        targetPeptide.setTheoreticalMass(sourcePeptide.getMass());
        //set identification charge
        targetPeptide.setCharge(identificationCharge);
        //set psm probability
        targetPeptide.setPsmProbability(psmMatchScore.getProbability());
        //set psm posterior error probability
        targetPeptide.setPsmPostErrorProbability(psmMatchScore.getPostErrorProbability());

        //check for modifications and modification scores
        if (!sourcePeptide.getModificationMatches().isEmpty()) {
            utilitiesModificationMapper.map(sourcePeptide.getModificationMatches(), ptmScores, targetPeptide);
        }
    }
    
    /**
     * Clear resources after usage.
     */
    public void clear(){
        utilitiesModificationMapper.clear();
    }
}
