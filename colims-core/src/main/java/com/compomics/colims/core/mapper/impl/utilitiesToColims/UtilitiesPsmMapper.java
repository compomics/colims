package com.compomics.colims.core.mapper.impl.utilitiesToColims;

import java.util.ArrayList;
import java.util.List;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.mapper.MatchScore;
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
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesPsmMapper")
public class UtilitiesPsmMapper {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesPsmMapper.class);
    @Autowired
    private UtilitiesPeptideMapper utilitiesPeptideMapper;
    @Autowired
    private UtilitiesProteinMapper utilitiesProteinMapper;

    public void map(Ms2Identification ms2Identification, SpectrumMatch spectrumMatch, Spectrum targetSpectrum) throws MappingException {
        //get best assumption
        PeptideAssumption peptideAssumption = spectrumMatch.getBestAssumption();
        com.compomics.util.experiment.biology.Peptide sourcePeptide = peptideAssumption.getPeptide();

        PSParameter psmProbabilities = new PSParameter();
        PSParameter peptideProbabilities = new PSParameter();
        PSParameter proteinProbabilities = new PSParameter();
        Peptide targetPeptide = new Peptide();
        try {
            //get psm and peptide probabilities            
            psmProbabilities = (PSParameter) ms2Identification.getSpectrumMatchParameter(spectrumMatch.getKey(), psmProbabilities);
            peptideProbabilities = (PSParameter) ms2Identification.getPeptideMatchParameter(sourcePeptide.getKey(), peptideProbabilities);
        } catch (SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
        } catch (ClassNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
        }

        MatchScore psmMatchScore = new MatchScore(psmProbabilities.getPsmProbabilityScore(), psmProbabilities.getPsmProbability());
        PSPtmScores ptmScores = null;
        if (spectrumMatch.getUrParam(new PSPtmScores()) != null) {
            ptmScores = (PSPtmScores) spectrumMatch.getUrParam(new PSPtmScores());
        }
        utilitiesPeptideMapper.map(sourcePeptide, psmMatchScore, ptmScores, targetPeptide);
        //set entity relations
        targetSpectrum.getPeptides().add(targetPeptide);
        targetPeptide.setSpectrum(targetSpectrum);

        List<ProteinMatch> proteinMatches = new ArrayList<>();
        //iterate over protein keys        
        try {
            for (String proteinKey : sourcePeptide.getParentProteins()) {
                ProteinMatch proteinMatch = ms2Identification.getProteinMatch(proteinKey);
                if (proteinMatch != null) {
                    proteinMatches.add(proteinMatch);
                }
            }
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
        } catch (SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
        } catch (ClassNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
        }
        //map proteins
        MatchScore peptideMatchScore = new MatchScore(peptideProbabilities.getPeptideProbabilityScore(), peptideProbabilities.getPeptideProbability());

        utilitiesProteinMapper.map(proteinMatches, peptideMatchScore, targetPeptide);
    }
}
