package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.identification.protein_sequences.SequenceFactory;
import eu.isas.peptideshaker.parameters.PSParameter;
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
        File fastaFile = new ClassPathResource("data/peptideshaker/uniprot-human-reviewed-trypsin-august-2015_concatenated_target_decoy.fasta").getFile();
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
        ProteinGroup proteinGroup = new ProteinGroup();

        //create utilities protein matches
        ProteinMatch proteinMatch = new ProteinMatch("P16083");
        PSParameter psParameter = new PSParameter();
        psParameter.setProteinProbability(56.3);
        psParameter.setProteinProbabilityScore(99.4);

        //create protein group score
        PSParameter proteinGroupScore = new PSParameter();
        proteinGroupScore.setProteinProbabilityScore(0.45);
        proteinGroupScore.setProteinProbability(0.77);

        utilitiesProteinMapper.map(proteinMatch, proteinGroupScore, proteinGroup);

        Assert.assertNotNull(proteinGroup.getProteinGroupHasProteins());
        Assert.assertEquals(1, proteinGroup.getProteinGroupHasProteins().size());
        Assert.assertEquals(0.45, proteinGroup.getProteinProbability(), 0.001);
        Assert.assertEquals(0.77, proteinGroup.getProteinPostErrorProbability(), 0.01);

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
        ProteinGroup proteinGroup = new ProteinGroup();

        //create utilities protein matches
        ProteinMatch proteinMatch = new ProteinMatch("P06241");
        proteinMatch.addTheoreticProtein("P07947");
        proteinMatch.addTheoreticProtein("P12931");
        proteinMatch.setMainMatch("P06241");
        PSParameter psParameter = new PSParameter();
        psParameter.setProteinProbability(56.3);
        psParameter.setProteinProbabilityScore(99.4);

        //create protein group score
        PSParameter proteinGroupScore = new PSParameter();
        proteinGroupScore.setProteinProbabilityScore(0.45);
        proteinGroupScore.setProteinProbability(0.77);

        utilitiesProteinMapper.map(proteinMatch, proteinGroupScore, proteinGroup);

        Assert.assertNotNull(proteinGroup.getProteinGroupHasProteins());
        Assert.assertEquals(3, proteinGroup.getProteinGroupHasProteins().size());
        Assert.assertEquals(0.45, proteinGroup.getProteinProbability(), 0.001);
        Assert.assertEquals(0.77, proteinGroup.getProteinPostErrorProbability(), 0.01);

        proteinGroup.getProteinGroupHasProteins().stream().forEach(proteinGroupHasProtein -> {
            Assert.assertNotNull(proteinGroupHasProtein.getProtein());
            Assert.assertNotNull(proteinGroupHasProtein.getProteinAccession());
            Assert.assertTrue(proteinMatch.getTheoreticProteinsAccessions().contains(proteinGroupHasProtein.getProtein().getProteinAccessions().get(0).getAccession()));

            if (!"P06241".equals(proteinGroupHasProtein.getProteinAccession())) {
                Assert.assertFalse(proteinGroupHasProtein.getIsMainGroupProtein());
            } else {
                Assert.assertTrue(proteinGroupHasProtein.getIsMainGroupProtein());
                Assert.assertEquals("P06241", proteinGroupHasProtein.getProteinAccession());
            }
        });
    }
}
