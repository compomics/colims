package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.distributed.io.ModificationMapper;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.pride.CvTerm;
import eu.isas.peptideshaker.parameters.PSParameter;
import eu.isas.peptideshaker.parameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

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
    private final ModificationMapper modificationMapper;

    @Autowired
    public UtilitiesPeptideMapper(ModificationMapper modificationMapper) {
        this.modificationMapper = modificationMapper;
    }

    /**
     * Map the utilities objects onto the Colims Peptide.
     *
     * @param spectrumMatch the Utilities SpectrumMatch instance
     * @param spectrumScore the Utilities PSParameter instance with the spectrum scores
     * @param targetPeptide the Colims Peptide instance
     */
    public void map(final SpectrumMatch spectrumMatch, final PSParameter spectrumScore, final Peptide targetPeptide) {
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
            mapModifications(sourcePeptide.getModificationMatches(), modificationScores, targetPeptide);
        }
    }

    /**
     * Clear resources.
     */
    public void clear() {
        modificationMapper.clear();
    }

    /**
     * Map the utilities modification matches to the Colims peptide. The
     * Utilities PTMs are matched first onto CV terms from UNIMOD and PSI-MOD.
     *
     * @param modificationMatches the list of modification matches
     * @param ptmScores           the PeptideShaker PTM scores
     * @param targetPeptide       the Colims target peptide
     */
    private void mapModifications(final ArrayList<ModificationMatch> modificationMatches, final PSPtmScores ptmScores, final Peptide targetPeptide) {
        //iterate over modification matches
        for (ModificationMatch modificationMatch : modificationMatches) {
            //get the CvTerm from the PTMFactory
            PTM ptm = PTMFactory.getInstance().getPTM(modificationMatch.getTheoreticPtm());
            CvTerm cvTerm = ptm.getCvTerm();

            Modification modification;
            if (cvTerm != null) {
                modification = modificationMapper.mapByOntologyTerm(
                        cvTerm.getOntology(),
                        cvTerm.getAccession(),
                        cvTerm.getName(),
                        cvTerm.getValue(),
                        ptm.getName());
            } else {
                modification = modificationMapper.mapByName(modificationMatch.getTheoreticPtm());
            }

            //set entity associations if modification could be mapped
            if (modification != null) {
                PeptideHasModification peptideHasModification = new PeptideHasModification();

                //set the Utilities name if necessary
                if (modification.getUtilitiesName() == null && PTMFactory.getInstance().containsPTM(modificationMatch.getTheoreticPtm())) {
                    modification.setUtilitiesName(modificationMatch.getTheoreticPtm());
                }

                //set location in the PeptideHasModification join entity
                int modificationIndex = modificationMatch.getModificationSite();

                //check for N-terminal modification
                if (modificationIndex == 1) {
                    if (!ptm.equals(PTMFactory.unknownPTM)) {
                        if (ptm.isNTerm()) {
                            modificationIndex = 0;
                        }
                    }
                }
                //check for C-terminal modification
                if (modificationIndex == targetPeptide.getLength()) {
                    if (!ptm.equals(PTMFactory.unknownPTM)) {
                        if (ptm.isCTerm()) {
                            modificationIndex = targetPeptide.getLength() + 1;
                        }
                    }
                }

                peptideHasModification.setLocation(modificationIndex);

                //set modification type
                if (modificationMatch.isVariable()) {
                    if (ptmScores != null) {
                        PtmScoring ptmScoring = ptmScores.getPtmScoring(modificationMatch.getTheoreticPtm());
                        if (ptmScoring != null) {
                            if (ptmScoring.getDSites().contains(modificationIndex)) {
                                peptideHasModification.setProbabilityScore(ptmScoring.getProbabilisticScore(modificationIndex));
                            }
                            if (ptmScoring.getProbabilisticSites().contains(modificationIndex)) {
                                peptideHasModification.setDeltaScore(ptmScoring.getDeltaScore(modificationIndex));
                            }
                        }
                    }
                }

                //set entity associations
                peptideHasModification.setModification(modification);
                peptideHasModification.setPeptide(targetPeptide);

                targetPeptide.getPeptideHasModifications().add(peptideHasModification);
            }
        }
    }

}
