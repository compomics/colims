package com.compomics.colims.core.io.utilities_to_colims;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesPeptideMapper;
import com.compomics.colims.model.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;

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

        //create psm scores
        MatchScore psmMatchScore = new MatchScore(0.5, 0.1);

        Peptide targetPeptide = new Peptide();
        utilitiesPeptideMapper.map(sourcePeptide, psmMatchScore, null, targetPeptide);

        Assert.assertEquals(sourcePeptide.getSequence(), targetPeptide.getSequence());
        Assert.assertEquals(sourcePeptide.getMass(), targetPeptide.getTheoreticalMass(), 0.001);
        Assert.assertEquals(psmMatchScore.getProbability(), targetPeptide.getPsmProbability(), 0.001);
        Assert.assertEquals(psmMatchScore.getPostErrorProbability(), targetPeptide.getPsmPostErrorProbability(), 0.001);
    }
}
