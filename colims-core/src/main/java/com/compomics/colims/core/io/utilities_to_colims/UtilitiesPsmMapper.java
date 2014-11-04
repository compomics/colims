package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import java.util.ArrayList;
import java.util.List;

import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.model.IdentificationFile;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.identifications.Ms2Identification;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import eu.isas.peptideshaker.myparameters.PSParameter;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import java.io.IOException;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class maps the Utilities PSM input to a Colims spectrum entity and
 * related classes (Peptide, Protein, ...).
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesPsmMapper")
public class UtilitiesPsmMapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(UtilitiesPsmMapper.class);
    /**
     * The Compomics Utilities to Colims peptide mapper.
     */
    @Autowired
    private UtilitiesPeptideMapper utilitiesPeptideMapper;
    /**
     * The Compomics Utilities to Colims protein mapper.
     */
    @Autowired
    private UtilitiesProteinMapper utilitiesProteinMapper;

    /**
     * Map the Utilities psm input to a Colims spectrum and related classes.
     *
     * @param ms2Identification the Ms2Identification
     * @param identificationFile the identification file
     * @param spectrumMatch the SpectrumMatch
     * @param targetSpectrum the Colims spectrum entity
     * @throws SQLException thrown in case of an SQL related problem
     * @throws IOException thrown in case of an IO related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a
     * class by it's string name.
     * @throws InterruptedException thrown in case of an interrupted thread
     * problem
     * @throws MappingException thrown in case of a mapping related problem
     */
    public void map(final Ms2Identification ms2Identification, final IdentificationFile identificationFile, final SpectrumMatch spectrumMatch, final Spectrum targetSpectrum) throws SQLException, IOException, ClassNotFoundException, InterruptedException, MappingException {
        if (spectrumMatch.getBestPeptideAssumption() != null) {
            //get best assumption
            PeptideAssumption peptideAssumption = spectrumMatch.getBestPeptideAssumption();
            com.compomics.util.experiment.biology.Peptide sourcePeptide = peptideAssumption.getPeptide();
            Peptide targetPeptide = new Peptide();

            PSParameter psmProbabilities = new PSParameter();
            PSParameter peptideProbabilities = new PSParameter();
//                PSParameter proteinProbabilities = new PSParameter();
            //get psm and peptide probabilities
            psmProbabilities = (PSParameter) ms2Identification.getSpectrumMatchParameter(spectrumMatch.getKey(), psmProbabilities);
            peptideProbabilities = (PSParameter) ms2Identification.getPeptideMatchParameter(sourcePeptide.getKey(), peptideProbabilities);

            MatchScore psmMatchScore = new MatchScore(psmProbabilities.getPsmProbabilityScore(), psmProbabilities.getPsmProbability());
            PSPtmScores ptmScores = null;
            if (spectrumMatch.getUrParam(new PSPtmScores()) != null) {
                ptmScores = (PSPtmScores) spectrumMatch.getUrParam(new PSPtmScores());
            }
            utilitiesPeptideMapper.map(sourcePeptide, psmMatchScore, ptmScores, peptideAssumption.getIdentificationCharge().value, targetPeptide);
            //link the IdentificationFile to the peptide
            targetPeptide.setIdentificationFile(identificationFile);

            //set entity relations
            targetSpectrum.getPeptides().add(targetPeptide);
            targetPeptide.setSpectrum(targetSpectrum);

            List<ProteinMatch> proteinMatches = new ArrayList<>();
            //iterate over protein keys
            //get parent proteins without remapping them
            //@todo this is the way to go for maxquant, but what about peptideshaker?
            for (String proteinKey : sourcePeptide.getParentProteinsNoRemapping()) {
                ProteinMatch proteinMatch = ms2Identification.getProteinMatch(proteinKey);
                if (proteinMatch != null) {
                    proteinMatches.add(proteinMatch);
                }
            }

            //map proteins
            MatchScore peptideMatchScore = null;
            if (peptideProbabilities != null) {
                peptideMatchScore = new MatchScore(peptideProbabilities.getPeptideProbabilityScore(), peptideProbabilities.getPeptideProbability());
            } else {
                LOGGER.info("No peptide match score found for peptide: " + targetPeptide.getSequence());
                peptideMatchScore = new MatchScore(0.0, 0.0);
            }

            utilitiesProteinMapper.map(proteinMatches, peptideMatchScore, targetPeptide);
        } else {
            LOGGER.debug("No best match was found for spectrum match " + spectrumMatch.getKey());
        }
    }

    /**
     * Clear resources.
     */
    public void clear() {
        utilitiesPeptideMapper.clear();
        utilitiesProteinMapper.clear();
    }

}
