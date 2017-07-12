package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Iain on 19/05/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantEvidenceParserTest {

    private Path evidenceFile;

    @Autowired
    private MaxQuantEvidenceParser maxQuantEvidenceParser;

    public MaxQuantEvidenceParserTest() throws IOException {
        evidenceFile = new ClassPathResource("data/maxquant/evidence_unit_test_modifications.txt").getFile().toPath();
    }

    @Test
    public void testParse() throws Exception {
        maxQuantEvidenceParser.clear();

        Set<Integer> omittedProteinIds = new HashSet<>();
        omittedProteinIds.add(0);
        omittedProteinIds.add(1);

        maxQuantEvidenceParser.parse(evidenceFile, omittedProteinIds);

        //check the number of MBR identifications
        Assert.assertEquals(9, maxQuantEvidenceParser.getRunToMbrPeptides().size());

        //check the size of the spectrumToPeptides map
        Assert.assertEquals(14, maxQuantEvidenceParser.getSpectrumToPeptides().size());

        //check the size of the peptideToProteins map
        Assert.assertEquals(22, maxQuantEvidenceParser.getPeptideToProteinGroups().size());

        //check the parsed modifications
        //check a peptide with a terminal modification
        List<Peptide> peptides = maxQuantEvidenceParser.getPeptides().get(737);
        Peptide peptide = peptides.get(0);
        Assert.assertNull(peptide.getPsmProbability());
        Assert.assertNull(peptide.getPsmPostErrorProbability());
        Assert.assertEquals(2, peptide.getCharge().intValue());
        Assert.assertEquals(1029.52771, peptide.getTheoreticalMass(), 0.001);
        Assert.assertEquals(-1.3998, peptide.getMassError(), 0.001);
        Assert.assertEquals(1, peptide.getPeptideHasModifications().size());
        PeptideHasModification peptideHasModification = peptide.getPeptideHasModifications().get(0);
        Assert.assertEquals(0, peptideHasModification.getLocation().intValue());
        Assert.assertNull(peptideHasModification.getProbabilityScore());
        Assert.assertNull(peptideHasModification.getDeltaScore());
        Modification modification = peptide.getPeptideHasModifications().get(0).getModification();
        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:1", modification.getAccession());

        //check a peptide with a non-terminal modification
        peptides = maxQuantEvidenceParser.getPeptides().get(1154);
        //check if the this evidence ID has two associated peptides
        Assert.assertEquals(2, peptides.size());
        peptide = peptides.get(0);
        Assert.assertEquals(1, peptide.getPeptideHasModifications().size());
        peptideHasModification = peptide.getPeptideHasModifications().get(0);
        Assert.assertEquals(7, peptideHasModification.getLocation().intValue());
        Assert.assertEquals(0.999, peptideHasModification.getProbabilityScore(), 0.001);
        Assert.assertEquals(29.93, peptideHasModification.getDeltaScore(), 0.001);
        modification = peptideHasModification.getModification();
        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:35", modification.getAccession());

        //check a peptide with 2 modifications
        peptides = maxQuantEvidenceParser.getPeptides().get(6239);
        peptide = peptides.get(0);
        Assert.assertEquals(43.066, peptide.getPsmProbability(), 0.001);
        Assert.assertEquals(0.018231, peptide.getPsmPostErrorProbability(), 0.001);
        Assert.assertEquals(2, peptide.getPeptideHasModifications().size());
        peptideHasModification = peptide.getPeptideHasModifications().get(0);
        Assert.assertEquals(5, peptideHasModification.getLocation().intValue());
        Assert.assertEquals(1.0, peptideHasModification.getProbabilityScore(), 0.001);
        Assert.assertEquals(43.07, peptideHasModification.getDeltaScore(), 0.001);
        modification = peptideHasModification.getModification();
        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:35", modification.getAccession());
        peptideHasModification = peptide.getPeptideHasModifications().get(1);
        Assert.assertEquals(6, peptideHasModification.getLocation().intValue());
        Assert.assertEquals(1.0, peptideHasModification.getProbabilityScore(), 0.001);
        Assert.assertEquals(43.07, peptideHasModification.getDeltaScore(), 0.001);
        modification = peptideHasModification.getModification();
        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:21", modification.getAccession());

        //check a MBR peptide, modification scores should be empty
        peptides = maxQuantEvidenceParser.getPeptides().get(2619);
        peptide = peptides.get(0);
        Assert.assertEquals(1, peptide.getPeptideHasModifications().size());
        peptideHasModification = peptide.getPeptideHasModifications().get(0);
        Assert.assertEquals(3, peptideHasModification.getLocation().intValue());
        Assert.assertNull(peptideHasModification.getProbabilityScore());
        Assert.assertNull(peptideHasModification.getDeltaScore());
        modification = peptideHasModification.getModification();
        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:21", modification.getAccession());
    }

}
