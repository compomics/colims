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
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.pride.CvTerm;
import com.compomics.util.pride.PrideObjectsFactory;
import com.compomics.util.pride.PtmToPrideMap;
import com.google.common.collect.HashBiMap;
import eu.isas.peptideshaker.myparameters.PSParameter;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
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

    public void map(SpectrumMatch spectrumMatch, PSParameter psmProbabilities, Peptide targetPeptide) throws MappingException {
        //get best assumption
        PeptideAssumption peptideAssumption = spectrumMatch.getBestAssumption();
        //get peptide
        com.compomics.util.experiment.biology.Peptide sourcePeptide = peptideAssumption.getPeptide();        
        
        //set sequence
        targetPeptide.setSequence(sourcePeptide.getSequence());
        //set theoretical mass
        targetPeptide.setTheoreticalMass(sourcePeptide.getMass());
        //@todo how to get experimental mass
        //set psm probability
        targetPeptide.setPsmProbability(psmProbabilities.getPsmProbabilityScore());
        //set psm posterior error probability
        targetPeptide.setPsmPostErrorProbability(psmProbabilities.getPsmProbability());

        //check for modifications and modification scores
        if (!sourcePeptide.getModificationMatches().isEmpty()) {
            PSPtmScores ptmScores = null;
            if (spectrumMatch.getUrParam(new PSPtmScores()) != null) {
                ptmScores = (PSPtmScores) spectrumMatch.getUrParam(new PSPtmScores());
            }            
            utilitiesModificationMapper.map(sourcePeptide.getModificationMatches(), ptmScores, targetPeptide);
        }
    }
}
