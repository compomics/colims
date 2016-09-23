package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.ModificationMappingException;
import com.compomics.colims.model.Peptide;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import eu.isas.peptideshaker.parameters.PSParameter;
import eu.isas.peptideshaker.parameters.PSPtmScores;
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
    private final UtilitiesModificationMapper utilitiesModificationMapper;

    @Autowired
    public UtilitiesPeptideMapper(UtilitiesModificationMapper utilitiesModificationMapper) {
        this.utilitiesModificationMapper = utilitiesModificationMapper;
    }

    /**
     * Map the utilities objects onto the Colims Peptide.
     *
     * @param spectrumMatch the Utilities SpectrumMatch instance
     * @param spectrumScore the Utilities PSParameter instance with the spectrum scores
     * @param targetPeptide the Colims Peptide instance
     * @throws ModificationMappingException thrown in case of a modification mapping problem
     */
    public void map(final SpectrumMatch spectrumMatch, final PSParameter spectrumScore, final Peptide targetPeptide) throws ModificationMappingException {
        PeptideAssumption peptideAssumption = spectrumMatch.getBestPeptideAssumption();
        com.compomics.util.experiment.biology.Peptide sourcePeptide = peptideAssumption.getPeptide();

        //set sequence
        targetPeptide.setSequence(sourcePeptide.getSequence());
        //set theoretical mass
        targetPeptide.setTheoreticalMass(sourcePeptide.getMass());
        //set identification charge
        targetPeptide.setCharge(peptideAssumption.getIdentificationCharge().value);
        //set psm probability
        targetPeptide.setPsmProbability(spectrumScore.getPsmProbabilityScore());
        //set psm posterior error probability
        targetPeptide.setPsmPostErrorProbability(spectrumScore.getPsmProbability());

        //check for modifications and modification scores
        if (sourcePeptide.getModificationMatches() != null && !sourcePeptide.getModificationMatches().isEmpty()) {
            PSPtmScores modificationScores = null;
            if (spectrumMatch.getUrParam(new PSPtmScores()) != null) {
                modificationScores = (PSPtmScores) spectrumMatch.getUrParam(new PSPtmScores());
            }
            utilitiesModificationMapper.map(sourcePeptide.getModificationMatches(), modificationScores, targetPeptide);
        }
    }

    /**
     * Clear resources.
     */
    public void clear() {
        utilitiesModificationMapper.clear();
    }
}
