package com.compomics.colims.core.io.parser.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.repository.ModificationRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml" })
@Transactional
public class MaxQuantEvidenceParserTest {
    File getFile(final String filename) {
        return new File(getClass().getClassLoader().getResource(filename).getFile());
    }

    @Autowired
    MaxQuantEvidenceParser parser;

    @Test
    public void testMaxQuantEvidenceParser() throws IOException {
        // Parse file
        parser.parse(getFile("testdata/evidence_subset_1000.tsv"));

        // Assertions
        // TODO fail("Not yet implemented");
    }

    @Autowired
    ModificationRepository modificationRepository;

    @Test
    public void testCreatePeptide() {
        // Expected values
        String sequence = "ABCDEFG";
        double mass = 123.45;

        // Setup argument
        Map<String, String> values = new HashMap<>();
        values.put(EvidenceHeaders.Sequence.column, sequence);
        values.put(EvidenceHeaders.Mass.column, Double.toString(mass));

        // Invoke tested method
        Peptide peptide = MaxQuantEvidenceParser.createPeptide(values);

        // Compare values
        Assert.assertEquals(sequence, peptide.getSequence());
        Assert.assertEquals(mass, peptide.getTheoreticalMass(), 0.001);
    }

    @Test
    public void testLinkPeptideToModificationsOxidization() {
        // Setup arguments
        Peptide peptide = new Peptide();
        Map<String, String> values = new HashMap<>();
        values.put(EvidenceHeaders.Oxidation_M_Probabilities.column, "AAM(1)GNFAAFSAIPGVEVR");
        parser.linkPeptideToModifications(peptide, values);

        // Check modification value matches what we expect
        List<PeptideHasModification> modifications = peptide.getPeptideHasModifications();
        Assert.assertEquals(1, modifications.size());
        PeptideHasModification pepHasMod = modifications.get(0);
        Assert.assertEquals(2, pepHasMod.getLocation().intValue());
        Modification modification = pepHasMod.getModification();
        Assert.assertEquals(EvidenceHeaders.Oxidation_M_Probabilities.column, modification.getName());

        // Assert the modification is stored in the database
        Modification storedModification = modificationRepository.findByName(EvidenceHeaders.Oxidation_M_Probabilities.column);
        Assert.assertNotNull(storedModification);
    }

    @Test
    public void testLinkPeptideToModificationsAcetyl() {
        // Setup arguments
        Peptide peptide = new Peptide();
        Map<String, String> values = new HashMap<>();
        String modificationName = EvidenceHeaders.Acetyl_Protein_N_term.column;
        values.put(modificationName, "1");
        parser.linkPeptideToModifications(peptide, values);

        // Check modification value matches what we expect
        List<PeptideHasModification> modifications = peptide.getPeptideHasModifications();
        Assert.assertEquals(1, modifications.size());
        PeptideHasModification pepHasMod = modifications.get(0);
        Assert.assertEquals(0, pepHasMod.getLocation().intValue());
        Assert.assertEquals(modificationName, pepHasMod.getModification().getName());

        // Assert the modification is stored in the database
        Modification storedModification = modificationRepository.findByName(modificationName);
        Assert.assertNotNull(storedModification);
    }
}
