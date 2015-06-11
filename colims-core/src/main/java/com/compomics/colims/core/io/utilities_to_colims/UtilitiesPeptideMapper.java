package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.io.ModificationMappingException;
import com.compomics.colims.core.util.PeptidePosition;
import com.compomics.colims.model.Peptide;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class maps a Compomics Utilities peptide object to Colims Peptide instance.
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesPeptideMapper")
public class UtilitiesPeptideMapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(UtilitiesPeptideMapper.class);
    /**
     * The Utilities to Colims modification mapper instance.
     */
    @Autowired
    private UtilitiesModificationMapper utilitiesModificationMapper;

    /**
     * Map the utilities objects onto the Colims Peptide.
     *
     * @param sourcePeptide        the Utilities peptide
     * @param psmMatchScore        the PSM score
     * @param ptmScores            the PSPtmScores instance
     * @param identificationCharge the charge
     * @param targetPeptide        the Colims peptide
     * @throws ModificationMappingException thrown in case of a modification mapping problem
     */
    public void map(final com.compomics.util.experiment.biology.Peptide sourcePeptide, final MatchScore psmMatchScore, final PSPtmScores ptmScores, final int identificationCharge, final Peptide targetPeptide) throws ModificationMappingException {
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
     * Clear resources.
     */
    public void clear() {
        utilitiesModificationMapper.clear();
    }
}
