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
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesProteinMapper;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import org.junit.BeforeClass;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class UtilitiesProteinMapperTest {

    @Autowired
    private UtilitiesProteinMapper utilitiesProteinMapper;

    @BeforeClass
    public static void setupOnce() throws IOException, FileNotFoundException, ClassNotFoundException {
        //load SequenceFactory for testing
        File fastaFile = new ClassPathResource("data/peptideshaker/uniprot_sprot_101104_human_concat.fasta").getFile();
        SequenceFactory.getInstance().loadFastaFile(fastaFile, null);
    }

    /**
     * Test the protein mapping whithout protein groups
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
        Assert.assertEquals(peptideMatchScore.getProbability(), peptideHasProtein.getPeptideProbability(), 0.001);
        Assert.assertEquals(peptideMatchScore.getPostErrorProbability(), peptideHasProtein.getPeptidePostErrorProbability(), 0.001);
        Assert.assertEquals("P16083", peptideHasProtein.getProtein().getAccession());

        //main group protein should be null
        Assert.assertNull(peptideHasProtein.getMainGroupProtein());
    }

    /**
     * Test the protein mapping with protein groups
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
        //@todo can we still use this construcor if we don't want to redo the protein mapping
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
            Assert.assertEquals(peptideMatchScore.getProbability(), peptideHasProtein.getPeptideProbability(), 0.001);
            Assert.assertEquals(peptideMatchScore.getPostErrorProbability(), peptideHasProtein.getPeptidePostErrorProbability(), 0.001);
            Assert.assertTrue(proteinMatch.getTheoreticProteinsAccessions().contains(peptideHasProtein.getProtein().getAccession()));
            //main group protein should not be null
            Assert.assertNotNull(peptideHasProtein.getMainGroupProtein());
            Assert.assertEquals("P06241", peptideHasProtein.getMainGroupProtein().getAccession());
        }

    }
}
