package com.compomics.colims.core.mapper.impl;

import com.compomics.colims.core.mapper.Mapper;
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
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.identifications.Ms2Identification;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.pride.CvTerm;
import com.compomics.util.pride.PrideObjectsFactory;
import com.compomics.util.pride.PtmToPrideMap;
import eu.isas.peptideshaker.myparameters.PSParameter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
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
            System.out.println("test");
        } catch (SQLException | IOException | ClassNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
        }

        utilitiesPeptideMapper.map(spectrumMatch, psmProbabilities, targetPeptide);
        //set entity relations
        targetSpectrum.getPeptides().add(targetPeptide);
        targetPeptide.setSpectrum(targetSpectrum);

        List<ProteinMatch> proteinMatches = new ArrayList<>();
        //iterate over protein keys        
        for (String proteinKey : sourcePeptide.getParentProteins()) {
            try {
                ProteinMatch proteinMatch = ms2Identification.getProteinMatch(proteinKey);
                if (proteinMatch != null) {
                    proteinMatches.add(proteinMatch);
                }
            } catch (IllegalArgumentException | SQLException | IOException | ClassNotFoundException ex) {
                LOGGER.error(ex.getMessage(), ex);
                throw new MappingException(ex);
            }
        }
        //map proteins
        utilitiesProteinMapper.map(proteinMatches, peptideProbabilities, targetPeptide);
    }
}
