/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.core.io.colims_to_utilities.ColimsModificationMapper;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Kenneth Verheggen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class ColimsModificationMapperTest {

    @Autowired
    private ColimsModificationMapper colimsModificationMapper;

    public ColimsModificationMapperTest() {
    }

    /**
     * Test of map method, of class ColimsModificationMapper.
     */
    @Test
    public void testMap() throws Exception {
        System.out.println("Test mapping from colims to utilities for Modifications");
        Peptide targetPeptide = new Peptide();
//        targetPeptide.setExperimentalMass(210.574);
        targetPeptide.setSequence("MYFHSFLDTFSKYLGSTSCPLLRLSR");
        targetPeptide.setTheoreticalMass(210.598);
        List<PeptideHasModification> peptideHasModList = new ArrayList<PeptideHasModification>();
        double[] alphaScores = new double[]{0.001, 0.001, 0.003};
        double[] deltaScores = new double[]{0.003, 0.002, 0.001};
        int[] positions = new int[]{5, 7, 12};
        ModificationType[] type = new ModificationType[]{ModificationType.FIXED, ModificationType.FIXED, ModificationType.VARIABLE};

        double[] averageMasses = new double[]{133.71, 124.01, 93.09};
        double[] averageMassShift = new double[]{3.71, 4.01, 3.09};

        for (int i = 0; i < 3; i++) {
            PeptideHasModification aMod = new PeptideHasModification();
            aMod.setAlphaScore(alphaScores[i]);
            aMod.setDeltaScore(deltaScores[i]);
            aMod.setLocation(positions[i]);
            aMod.setModificationType(type[i]);
            Modification newMod = new Modification();
            newMod.setAccession("a fake modification " + i);
            newMod.setAverageMass(averageMasses[i]);
            newMod.setAverageMassShift(averageMassShift[i]);
            aMod.setModification(newMod);
            peptideHasModList.add(aMod);
        }

        targetPeptide.setPeptideHasModifications(peptideHasModList);
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<ModificationMatch>();
        colimsModificationMapper.map(targetPeptide, modificationMatches);

        Assert.assertEquals(3, modificationMatches.size());

        Assert.assertEquals(5, modificationMatches.get(0).getModificationSite());
        Assert.assertEquals(false, modificationMatches.get(0).isVariable());
        Assert.assertEquals("a fake modification 0", modificationMatches.get(0).getTheoreticPtm());

        Assert.assertEquals(12, modificationMatches.get(2).getModificationSite());
        Assert.assertEquals(true, modificationMatches.get(2).isVariable());
        Assert.assertEquals("a fake modification 2", modificationMatches.get(2).getTheoreticPtm());

    }

}
