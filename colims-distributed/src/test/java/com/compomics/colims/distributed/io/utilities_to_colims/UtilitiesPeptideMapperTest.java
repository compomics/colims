package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.model.Peptide;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.massspectrometry.Charge;
import eu.isas.peptideshaker.parameters.PSParameter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class UtilitiesPeptideMapperTest {

    @Autowired
    private UtilitiesPeptideMapper utilitiesPeptideMapper;

    /**
     * Test the mapping of an utilities to a colims peptide.
     *
     * @throws MappingException
     * @throws IOException
     */
    @Test
    public void testMapPeptide() throws MappingException, IOException {
        //create new utilities SpectrumMatch
        com.compomics.util.experiment.biology.Peptide sourcePeptide = new com.compomics.util.experiment.biology.Peptide("YKENNAMRT", new ArrayList<>());
        sourcePeptide.setParentProteins(new ArrayList<>());
        PeptideAssumption peptideAssumption = new PeptideAssumption(sourcePeptide, 1, 1, new Charge(1, 2), 45.0);
        SpectrumMatch spectrumMatch = new SpectrumMatch("key", peptideAssumption);
        spectrumMatch.setBestPeptideAssumption(peptideAssumption);

        //create spectrum score
        PSParameter spectrumScore = new PSParameter();
        spectrumScore.setPsmProbability(0.99);

        Peptide targetPeptide = new Peptide();
        utilitiesPeptideMapper.map(spectrumMatch, spectrumScore, targetPeptide);

        Assert.assertEquals(sourcePeptide.getSequence(), targetPeptide.getSequence());
        Assert.assertEquals(sourcePeptide.getMass(), targetPeptide.getTheoreticalMass(), 0.001);
        Assert.assertEquals(2, targetPeptide.getCharge().intValue());
        Assert.assertEquals(spectrumScore.getPsmProbabilityScore(), targetPeptide.getPsmProbability(), 0.001);
        Assert.assertEquals(spectrumScore.getPsmProbability(), targetPeptide.getPsmPostErrorProbability(), 0.001);
    }
}
