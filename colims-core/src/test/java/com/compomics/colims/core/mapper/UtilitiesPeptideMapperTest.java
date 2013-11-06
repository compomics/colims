package com.compomics.colims.core.mapper;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.mapper.impl.UtilitiesPeptideMapper;
import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.preferences.ModificationProfile;
import eu.isas.peptideshaker.myparameters.PSParameter;
import java.io.FileNotFoundException;
import org.junit.Before;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class UtilitiesPeptideMapperTest {

    @Autowired
    private UtilitiesPeptideMapper utilitiesPeptideMapper;    
    
    @Test
    public void testMapPeptide() throws MappingException, IOException {
        //create new utilities Peptide
        com.compomics.util.experiment.biology.Peptide sourcePeptide = new com.compomics.util.experiment.biology.Peptide("YKENNAMRT", new ArrayList<String>(), new ArrayList<ModificationMatch>());        
        //create new utilities PeptideAssumption
        PeptideAssumption peptideAssumption = new PeptideAssumption(sourcePeptide, 1, 1, new Charge(1, 1), 100.0);        
        //create new utilities SpectrumMatch
        SpectrumMatch spectrumMatch = new SpectrumMatch("0", peptideAssumption);
        spectrumMatch.setBestAssumption(peptideAssumption);
        
        PSParameter psmProbabilities = new PSParameter();
        psmProbabilities.setSpectrumProbabilityScore(0.5);
        psmProbabilities.setPsmProbability(0.1);
        Peptide targetPeptide = new Peptide();
        utilitiesPeptideMapper.map(spectrumMatch, psmProbabilities, targetPeptide);

        Assert.assertEquals(sourcePeptide.getSequence(), targetPeptide.getSequence());
        Assert.assertEquals(sourcePeptide.getMass(), targetPeptide.getTheoreticalMass(), 0.001);        
        Assert.assertEquals(psmProbabilities.getPsmProbabilityScore(), targetPeptide.getPsmProbability(), 0.001);        
        Assert.assertEquals(psmProbabilities.getPsmProbability(), targetPeptide.getPsmPostErrorProbability(), 0.001);        
    }
}
