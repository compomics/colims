package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
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
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
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
     * Test the protein mapping without protein groups.
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
        com.compomics.util.experiment.biology.Peptide sourcePeptide = new com.compomics.util.experiment.biology.Peptide("YKENNAMRT", parentProteins, new ArrayList<ModificationMatch>());

        Peptide targetPeptide = new Peptide();

        //create utilities protein matches
        List<ProteinMatch> proteinMatches = new ArrayList();
        ProteinMatch proteinMatch = new ProteinMatch(sourcePeptide, sourcePeptide.getKey());
        proteinMatch.setMainMatch("P16083");
        proteinMatches.add(proteinMatch);

        //create peptide scores
        MatchScore peptideMatchScore = new MatchScore(0.5, 0.1);

        utilitiesProteinMapper.map(proteinMatches, peptideMatchScore, targetPeptide);

        Assert.assertNotNull(targetPeptide.getPeptideHasProteins());
        Assert.assertEquals(1, targetPeptide.getPeptideHasProteins().size());
        PeptideHasProtein peptideHasProtein = targetPeptide.getPeptideHasProteins().get(0);
        Assert.assertNotNull(peptideHasProtein.getPeptide());
        Assert.assertNotNull(peptideHasProtein.getProtein());
        Assert.assertEquals("P16083", peptideHasProtein.getProteinAccession());
        Assert.assertEquals(peptideMatchScore.getProbability(), peptideHasProtein.getPeptideProbability(), 0.001);
        Assert.assertEquals(peptideMatchScore.getPostErrorProbability(), peptideHasProtein.getPeptidePostErrorProbability(), 0.001);
        Assert.assertEquals("P16083", peptideHasProtein.getProtein().getProteinAccessions().get(0).getAccession());

        //the is main group protein should be null
        Assert.assertNull(peptideHasProtein.isMainGroupProtein());
    }

    /**
     * Test the protein mapping with protein groups.
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
        com.compomics.util.experiment.biology.Peptide sourcePeptide = new com.compomics.util.experiment.biology.Peptide("YKENNAMRT", parentProteins, new ArrayList<ModificationMatch>());

        Peptide targetPeptide = new Peptide();

        //create utilities protein matches
        List<ProteinMatch> proteinMatches = new ArrayList();
        ProteinMatch proteinMatch = new ProteinMatch(sourcePeptide, sourcePeptide.getKey());
        proteinMatch.addTheoreticProtein("P07947");
        proteinMatch.addTheoreticProtein("P12931");
        proteinMatch.setMainMatch("P06241");
        proteinMatches.add(proteinMatch);

        //create peptide scores
        MatchScore peptideMatchScore = new MatchScore(0.5, 0.1);

        utilitiesProteinMapper.map(proteinMatches, peptideMatchScore, targetPeptide);

        Assert.assertNotNull(targetPeptide.getPeptideHasProteins());
        Assert.assertEquals(3, targetPeptide.getPeptideHasProteins().size());

        for (PeptideHasProtein peptideHasProtein : targetPeptide.getPeptideHasProteins()) {
            Assert.assertNotNull(peptideHasProtein.getPeptide());
            Assert.assertNotNull(peptideHasProtein.getProtein());
            Assert.assertNotNull(peptideHasProtein.getProteinAccession());
            Assert.assertTrue(proteinMatch.getTheoreticProteinsAccessions().contains(peptideHasProtein.getProtein().getProteinAccessions().get(0).getAccession()));

            if (!"P06241".equals(peptideHasProtein.getProteinAccession())) {
                Assert.assertNull(peptideHasProtein.getPeptideProbability());
                Assert.assertNull(peptideHasProtein.getPeptidePostErrorProbability());
                Assert.assertFalse(peptideHasProtein.isMainGroupProtein());
            } else {
                Assert.assertEquals(peptideMatchScore.getProbability(), peptideHasProtein.getPeptideProbability(), 0.001);
                Assert.assertEquals(peptideMatchScore.getPostErrorProbability(), peptideHasProtein.getPeptidePostErrorProbability(), 0.001);
                Assert.assertTrue(peptideHasProtein.isMainGroupProtein());
                Assert.assertEquals("P06241", peptideHasProtein.getProteinAccession());
            }
        }
    }
}
