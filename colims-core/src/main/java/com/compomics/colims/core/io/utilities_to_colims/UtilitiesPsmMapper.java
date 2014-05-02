package com.compomics.colims.core.io.utilities_to_colims;

import java.util.ArrayList;
import java.util.List;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
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

    public void map(Ms2Identification ms2Identification, SpectrumMatch spectrumMatch, Spectrum targetSpectrum) throws MappingException, InterruptedException {
        //get best assumption
        PeptideAssumption peptideAssumption = spectrumMatch.getBestPeptideAssumption();
        com.compomics.util.experiment.biology.Peptide sourcePeptide = peptideAssumption.getPeptide();
        
        String key = sourcePeptide.getKey();
        System.out.println("key: " + key);

        PSParameter psmProbabilities = new PSParameter();
        PSParameter peptideProbabilities = new PSParameter();
//        PSParameter proteinProbabilities = new PSParameter();
        Peptide targetPeptide = new Peptide();
        try {
            //get psm and peptide probabilities            
            psmProbabilities = (PSParameter) ms2Identification.getSpectrumMatchParameter(spectrumMatch.getKey(), psmProbabilities);
            peptideProbabilities = (PSParameter) ms2Identification.getPeptideMatchParameter(sourcePeptide.getKey(), peptideProbabilities);
        } catch (SQLException | IOException | ClassNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
        }

        MatchScore psmMatchScore = new MatchScore(psmProbabilities.getPsmProbabilityScore(), psmProbabilities.getPsmProbability());
        PSPtmScores ptmScores = null;
        if (spectrumMatch.getUrParam(new PSPtmScores()) != null) {
            ptmScores = (PSPtmScores) spectrumMatch.getUrParam(new PSPtmScores());
        }
        utilitiesPeptideMapper.map(sourcePeptide, psmMatchScore, ptmScores, peptideAssumption.getIdentificationCharge().value, targetPeptide);
        //set entity relations
        targetSpectrum.getPeptides().add(targetPeptide);
        targetPeptide.setSpectrum(targetSpectrum);

        List<ProteinMatch> proteinMatches = new ArrayList<>();
        //iterate over protein keys        
        try {
            //get parent proteins without remapping them
            //@todo this is probably the way to go for maxquant, but what about peptideshaker?
            for (String proteinKey : sourcePeptide.getParentProteinsNoRemapping()) {
                ProteinMatch proteinMatch = ms2Identification.getProteinMatch(proteinKey);
                if (proteinMatch != null) {
                    proteinMatches.add(proteinMatch);
                }
            }
        } catch (IllegalArgumentException | SQLException | IOException | ClassNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
        }
        //map proteins
        MatchScore peptideMatchScore = null;
        if(peptideProbabilities != null){
            peptideMatchScore = new MatchScore(peptideProbabilities.getPeptideProbabilityScore(), peptideProbabilities.getPeptideProbability());
        }
        else{
            peptideMatchScore = new MatchScore(10.0, 10.0);
            System.out.println("----------------null");
        }

        utilitiesProteinMapper.map(proteinMatches, peptideMatchScore, targetPeptide);
    }
}
