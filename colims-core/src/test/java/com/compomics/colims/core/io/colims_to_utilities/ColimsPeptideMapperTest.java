/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.core.io.colims_to_utilities.ColimsPeptideMapper;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.protein.Header.DatabaseType;
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
public class ColimsPeptideMapperTest {

    @Autowired
    private ColimsPeptideMapper colimsPeptideMapper;

    public ColimsPeptideMapperTest() {
    }

    /**
     * Test of map method, of class ColimsPeptideMapper.
     */
    @Test
    public void testMap() throws Exception {
        System.out.println("Test mapping from colims to utilities for Peptides");
        Peptide sourcePeptide = new Peptide();

        sourcePeptide.setExperimentalMass(210.574);
        sourcePeptide.setSequence("MYFHSFLDTFSKYLGSTSCPLLRLSR");
        sourcePeptide.setTheoreticalMass(210.598);

        List<PeptideHasProtein> peptideHasProtList = new ArrayList<PeptideHasProtein>();
        PeptideHasProtein parentProt = new PeptideHasProtein();
        Protein aProtein = new Protein("fakeProt1", "AKJFDAEMYFHSFLDTFSKYLGSTSCPLLRLSRRRREADSAERRUIEAL", DatabaseType.Generic_Header);
        parentProt.setMainGroupProtein(aProtein);
        parentProt.setProtein(aProtein);
        parentProt.setPeptideProbability(97.65);
        peptideHasProtList.add(parentProt);

        sourcePeptide.setPeptideHasProteins(peptideHasProtList);

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

        sourcePeptide.setPeptideHasModifications(peptideHasModList);

        PeptideMatch targetPeptideMatch = new PeptideMatch();
        colimsPeptideMapper.map(sourcePeptide, targetPeptideMatch);

        Assert.assertEquals("MYFHSFLDTFSKYLGSTSCPLLRLSR", targetPeptideMatch.getTheoreticPeptide().getSequence());
        Assert.assertEquals(3068.525, targetPeptideMatch.getTheoreticPeptide().getMass(), 0.001);
        Assert.assertEquals(3, targetPeptideMatch.getTheoreticPeptide().getModificationMatches().size());
        Assert.assertEquals(1, targetPeptideMatch.getTheoreticPeptide().getParentProteinsNoRemapping().size());
        Assert.assertEquals("fakeProt1", targetPeptideMatch.getTheoreticPeptide().getParentProteinsNoRemapping().get(0));
    }

}
