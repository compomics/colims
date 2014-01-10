/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.mapper.colimsToUtilities;

import com.compomics.colims.core.mapper.impl.colimsToUtilities.ColimsProteinMapper;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
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
public class ColimsProteinMapperTest {

    @Autowired
    private ColimsProteinMapper colimsProteinMapper;

    public ColimsProteinMapperTest() {
    }

    /**
     * Test of map method, of class ColimsProteinMapper.
     */
    @Test
    public void testMap() throws Exception {
        System.out.println("Mapping Proteins ");
        Protein inputProtein = new Protein();
        inputProtein.setAccession("P0C9F1");
        inputProtein.setSequence("MVRLFHNPIKCLFYRGSRKTREKKLRKSLKKLNFYHPPGDCCQIYRLLENVPGGTYFITENMTNELIMIVKDSVDKKIKSVKLNFYGSYIKIHQHYYINIYMYLMRYTQIYKYPLICFNKYSYCNS");

        List<PeptideHasProtein> peptideHasProtList = new ArrayList<PeptideHasProtein>();
        PeptideHasProtein peptide = new PeptideHasProtein();
        Peptide aPeptide = new Peptide();
        aPeptide.setTheoreticalMass(33.3);
        aPeptide.setPsmProbability(96.5);
        aPeptide.setPeptideHasProteins(peptideHasProtList);
        aPeptide.setSequence("SRKIQEKKLRKSLKKLNFYHP");
        peptide.setPeptide(aPeptide);
        peptide.setProtein(inputProtein);
        peptideHasProtList.add(peptide);

        PeptideHasProtein mainPeptide = new PeptideHasProtein();
        Peptide aMainPeptide = new Peptide();
        aMainPeptide.setTheoreticalMass(33.3);
        aMainPeptide.setPsmProbability(96.5);
        aMainPeptide.setPeptideHasProteins(peptideHasProtList);
        aMainPeptide.setSequence("GGTYFITENMTNDLIMVVKDSVDKKIKS");
        mainPeptide.setPeptide(aMainPeptide);
        mainPeptide.setProtein(inputProtein);
        peptideHasProtList.add(mainPeptide);

        inputProtein.setPeptideHasProteins(peptideHasProtList);

        List<ProteinMatch> proteinMatches = new ArrayList<ProteinMatch>();
        colimsProteinMapper.map(inputProtein, proteinMatches);
        Assert.assertEquals(proteinMatches.get(0).isDecoy(), false);
        Assert.assertEquals("SRKIQEKKLRKSLKKLNFYHP", proteinMatches.get(0).getPeptideMatches().get(0));
        Assert.assertEquals("GGTYFITENMTNDLIMVVKDSVDKKIKS", proteinMatches.get(0).getPeptideMatches().get(1));
        Assert.assertEquals(2, proteinMatches.get(0).getPeptideCount());
        Assert.assertEquals("P0C9F1", proteinMatches.get(0).getMainMatch());
        Assert.assertEquals(28, aMainPeptide.getLength());
    }

}
