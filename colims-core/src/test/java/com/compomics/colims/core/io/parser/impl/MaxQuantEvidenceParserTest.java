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

import com.compomics.colims.repository.ModificationRepository;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import java.util.ArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml" })
@Transactional
public class MaxQuantEvidenceParserTest {
    File getFile(final String filename) {
        return new File(getClass().getClassLoader().getResource(filename).getFile());
    }

    @Autowired
    MaxQuantEvidenceParser maxQuantEvidenceParser;

    @Test
    public void testMaxQuantEvidenceParser() throws IOException {
        // Parse file
        //parser.parse(getFile("testdata/evidence_subset_1000.tsv"));

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
        String ProteinString = "gi|150003706|ref|YP_001298450.1| rubrerythrin [Bacteroides vulgatus ATCC 8482];gi|294777471|ref|ZP_06742922.1| rubrerythrin [Bacteroides vulgatus PC510]";
        
        // Setup argument
        Map<String, String> values = new HashMap<>();
        values.put(EvidenceHeaders.Sequence.column, sequence);
        values.put(EvidenceHeaders.Mass.column, Double.toString(mass));
        values.put(EvidenceHeaders.Proteins.column, ProteinString);
        values.put(EvidenceHeaders.Modifications.column, "Unmodified");

        // Invoke tested method
        Peptide peptide = MaxQuantEvidenceParser.createPeptide(values);

        // Compare values
        Assert.assertEquals(sequence, peptide.getSequence());
        //Assert.assertEquals(mass, peptide.getMass(), 0.001);
    }

    @Test
    public void testLinkPeptideToModificationsOxidization() {
        // Setup arguments
        Peptide peptide = new Peptide();
        Map<String, String> values = new HashMap<>();
        values.put(EvidenceHeaders.Oxidation_M_Probabilities.column, "AAM(1)GNFAAFSAIPGVEVR");
        //maxQuantEvidenceParser.linkPeptideToModifications(peptide, values);

        
        peptide.addModificationMatch(maxQuantEvidenceParser.extractModifications(values).get(0));
        
        
        // Check modification value matches what we expect
        ArrayList<ModificationMatch> modifications = peptide.getModificationMatches();
        Assert.assertEquals(1, modifications.size());
        ModificationMatch pepHasMod = modifications.get(0);
        Assert.assertEquals(2, pepHasMod.getModificationSite());
        Assert.assertEquals(EvidenceHeaders.Oxidation_M_Probabilities.column, pepHasMod.getTheoreticPtm());
    }

    @Test
    public void testLinkPeptideToModificationsAcetyl() {
        // Setup arguments
        Peptide peptide = new Peptide();
        Map<String, String> values = new HashMap<>();
        String modificationName = EvidenceHeaders.Acetyl_Protein_N_term.column;
        values.put(modificationName, "1");
        for(ModificationMatch match : maxQuantEvidenceParser.extractModifications(values)){
        peptide.addModificationMatch(match);
        }
        // Check modification value matches what we expect
        List<ModificationMatch> modifications = peptide.getModificationMatches();
        Assert.assertEquals(1, modifications.size());
        ModificationMatch pepHasMod = modifications.get(0);
        Assert.assertEquals(0, pepHasMod.getModificationSite());
        Assert.assertEquals(modificationName, pepHasMod.getTheoreticPtm());
    }
}
