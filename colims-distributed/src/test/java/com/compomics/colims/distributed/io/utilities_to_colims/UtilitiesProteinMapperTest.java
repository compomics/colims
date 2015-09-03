package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProteinGroup;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import eu.isas.peptideshaker.myparameters.PSParameter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class UtilitiesProteinMapperTest {

    @Autowired
    private UtilitiesProteinMapper utilitiesProteinMapper;

    @BeforeClass
    public static void setupOnce() throws IOException, ClassNotFoundException {
        //load SequenceFactory for testing
        File fastaFile = new ClassPathResource("data/peptideshaker/uniprot-human-reviewed-trypsin-january-2015_concatenated_target_decoy.fasta").getFile();
        SequenceFactory.getInstance().loadFastaFile(fastaFile, null);
    }

    /**
     * Test the protein mapping with one protein match.
     *
     * @throws MappingException
     * @throws IOException
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testMapProtein_1() throws MappingException, IOException, SQLException, ClassNotFoundException, InterruptedException {
        //create new utilities peptide
        ArrayList<String> parentProteins = new ArrayList<>();
        parentProteins.add("P16083");
        com.compomics.util.experiment.biology.Peptide sourcePeptide = new com.compomics.util.experiment.biology.Peptide("YKENNAMRT", new ArrayList<>());
        sourcePeptide.setParentProteins(parentProteins);

        Peptide targetPeptide = new Peptide();

        //create utilities protein matches
        ProteinMatch proteinMatch = new ProteinMatch(sourcePeptide, sourcePeptide.getKey());
        proteinMatch.setMainMatch("P16083");
        PSParameter psParameter = new PSParameter();
        psParameter.setProteinProbability(56.3);
        psParameter.setProteinProbabilityScore(99.4);

        //create peptide scores
        MatchScore peptideMatchScore = new MatchScore(0.5, 0.1);

        utilitiesProteinMapper.map(proteinMatch, psParameter, peptideMatchScore, targetPeptide);

        Assert.assertNotNull(targetPeptide.getPeptideHasProteinGroups());
        Assert.assertEquals(1, targetPeptide.getPeptideHasProteinGroups().size());

        PeptideHasProteinGroup peptideHasProteinGroup = targetPeptide.getPeptideHasProteinGroups().get(0);
        Assert.assertNotNull(peptideHasProteinGroup.getPeptide());
        Assert.assertNotNull(peptideHasProteinGroup.getProteinGroup());
        Assert.assertNotNull(peptideHasProteinGroup.getPeptideProbability());
        Assert.assertEquals(peptideMatchScore.getProbability(), peptideHasProteinGroup.getPeptideProbability(), 0.001);
        Assert.assertNotNull(peptideHasProteinGroup.getPeptidePostErrorProbability());
        Assert.assertEquals(peptideMatchScore.getPostErrorProbability(), peptideHasProteinGroup.getPeptidePostErrorProbability(), 0.001);

        ProteinGroup proteinGroup = peptideHasProteinGroup.getProteinGroup();
        Assert.assertEquals(99.4, proteinGroup.getProteinProbability(), 0.001);
        Assert.assertEquals(56.3, proteinGroup.getProteinPostErrorProbability(), 0.01);
        Assert.assertNotNull(proteinGroup.getPeptideHasProteinGroups());
        Assert.assertEquals(1, proteinGroup.getProteinGroupHasProteins().size());

        ProteinGroupHasProtein proteinGroupHasProtein = proteinGroup.getProteinGroupHasProteins().get(0);
        Assert.assertNotNull(proteinGroupHasProtein.getProteinGroup());
        Assert.assertNotNull(proteinGroupHasProtein.getProtein());
        Assert.assertNotNull(proteinGroupHasProtein.getProteinAccession());
        Assert.assertTrue(proteinGroupHasProtein.getIsMainGroupProtein());
        Assert.assertEquals("P16083", proteinGroupHasProtein.getProteinAccession());
    }

    /**
     * Test the protein mapping with more than one protein match.
     *
     * @throws MappingException
     * @throws IOException
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testMapProtein_2() throws MappingException, IOException, SQLException, ClassNotFoundException, InterruptedException {
        //create new utilities peptide
        ArrayList<String> parentProteins = new ArrayList<>();
        parentProteins.add("P06241");
        //@todo can we still use this constructor if we don't want to redo the protein mapping
        com.compomics.util.experiment.biology.Peptide sourcePeptide = new com.compomics.util.experiment.biology.Peptide("YKENNAMRT", new ArrayList<>());
        sourcePeptide.setParentProteins(parentProteins);

        Peptide targetPeptide = new Peptide();

        //create utilities protein matches
        ProteinMatch proteinMatch = new ProteinMatch(sourcePeptide, sourcePeptide.getKey());
        proteinMatch.addTheoreticProtein("P07947");
        proteinMatch.addTheoreticProtein("P12931");
        proteinMatch.setMainMatch("P06241");
        PSParameter psParameter = new PSParameter();
        psParameter.setProteinProbability(56.3);
        psParameter.setProteinProbabilityScore(99.4);

        //create peptide scores
        MatchScore peptideMatchScore = new MatchScore(0.5, 0.1);

        utilitiesProteinMapper.map(proteinMatch, psParameter, peptideMatchScore, targetPeptide);

        Assert.assertNotNull(targetPeptide.getPeptideHasProteinGroups());
        Assert.assertEquals(1, targetPeptide.getPeptideHasProteinGroups().size());

        PeptideHasProteinGroup peptideHasProteinGroup = targetPeptide.getPeptideHasProteinGroups().get(0);
        Assert.assertNotNull(peptideHasProteinGroup.getPeptide());
        Assert.assertNotNull(peptideHasProteinGroup.getProteinGroup());
        Assert.assertNotNull(peptideHasProteinGroup.getPeptideProbability());
        Assert.assertEquals(peptideMatchScore.getProbability(), peptideHasProteinGroup.getPeptideProbability(), 0.001);
        Assert.assertNotNull(peptideHasProteinGroup.getPeptidePostErrorProbability());
        Assert.assertEquals(peptideMatchScore.getPostErrorProbability(), peptideHasProteinGroup.getPeptidePostErrorProbability(), 0.001);

        ProteinGroup proteinGroup = peptideHasProteinGroup.getProteinGroup();
        Assert.assertEquals(99.4, proteinGroup.getProteinProbability(), 0.001);
        Assert.assertEquals(56.3, proteinGroup.getProteinPostErrorProbability(), 0.01);
        Assert.assertNotNull(proteinGroup.getPeptideHasProteinGroups());
        Assert.assertEquals(3, proteinGroup.getProteinGroupHasProteins().size());

        for (ProteinGroupHasProtein proteinGroupHasProtein : peptideHasProteinGroup.getProteinGroup().getProteinGroupHasProteins()) {
            Assert.assertNotNull(proteinGroupHasProtein.getProtein());
            Assert.assertNotNull(proteinGroupHasProtein.getProteinAccession());
            Assert.assertTrue(proteinMatch.getTheoreticProteinsAccessions().contains(proteinGroupHasProtein.getProtein().getProteinAccessions().get(0).getAccession()));

            if (!"P06241".equals(proteinGroupHasProtein.getProteinAccession())) {
                Assert.assertFalse(proteinGroupHasProtein.getIsMainGroupProtein());
            } else {
                Assert.assertTrue(proteinGroupHasProtein.getIsMainGroupProtein());
                Assert.assertEquals("P06241", proteinGroupHasProtein.getProteinAccession());
            }
        }
    }
}
