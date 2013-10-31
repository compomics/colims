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
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.pride.CvTerm;
import com.compomics.util.pride.PrideObjectsFactory;
import com.compomics.util.pride.PtmToPrideMap;
import com.google.common.collect.HashBiMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesPeptideMapper")
public class UtilitiesPeptideMapper implements Mapper<com.compomics.util.experiment.biology.Peptide, Peptide> {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesPeptideMapper.class);

    @Autowired
    private Mapper utilitiesModificationMapper;    

    @Override
    public void map(com.compomics.util.experiment.biology.Peptide sourcePeptide, Peptide targetPeptide) throws MappingException {
        //set sequence
        targetPeptide.setSequence(sourcePeptide.getSequence());
        //set theoretical mass
        targetPeptide.setTheoreticalMass(sourcePeptide.getMass());
        //@todo how to get experimental mass

        //check for modifications
        if (!sourcePeptide.getModificationMatches().isEmpty()) {
            utilitiesModificationMapper.map(sourcePeptide.getModificationMatches(), targetPeptide);
        }
    }    
}
