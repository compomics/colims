package com.compomics.colims.core.io.parser.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.colims.repository.ModificationRepository;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import java.util.ArrayList;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantEvidenceParserTest {

    File getFile(final String filename) {
        return new File(getClass().getClassLoader().getResource(filename).getFile());
    }
    @Autowired
    MaxQuantEvidenceParser maxQuantEvidenceParser;
    @Autowired
    ModificationRepository modificationRepository;

    @Test
    public void testCreatePeptide() throws UnparseableException, HeaderEnumNotInitialisedException {
        // Expected values
        String sequence = "ABCDEFG";
        double mass = 123.45;
        String ProteinString = "gi|150003706|ref|YP_001298450.1| rubrerythrin [Bacteroides vulgatus ATCC 8482];gi|294777471|ref|ZP_06742922.1| rubrerythrin [Bacteroides vulgatus PC510]";
        String singleProteinID = "1000";
        // Setup argument
        Map<String, String> values = new HashMap<>();
        values.put(EvidenceHeaders.Sequence.getColumnName(), sequence);
        values.put(EvidenceHeaders.Mass.getColumnName(), Double.toString(mass));
        values.put(EvidenceHeaders.Proteins.getColumnName(), ProteinString);
        values.put(EvidenceHeaders.Protein_Group_IDs.getColumnName(), singleProteinID);
        values.put(EvidenceHeaders.Modifications.getColumnName(), "Unmodified");
        values.put(EvidenceHeaders.Score.getColumnName(), "77");
        values.put(EvidenceHeaders.MS_MS_IDs.getColumnName(), "42");
        // Invoke tested method
        PeptideAssumption peptide = maxQuantEvidenceParser.createPeptide(values);

        // Compare values
        Assert.assertEquals(sequence, peptide.getPeptide().getSequence());
        //Assert.assertEquals(mass, peptide.getMass(), 0.001);
        
    }
    
    @Test
    public void testCreatePeptideMultipleMsMsAndProteinGroupValues() throws HeaderEnumNotInitialisedException{
    String sequence = "ABCDEFG";
        double mass = 123.45;
        String ProteinString = "gi|150003706|ref|YP_001298450.1| rubrerythrin [Bacteroides vulgatus ATCC 8482];gi|294777471|ref|ZP_06742922.1| rubrerythrin [Bacteroides vulgatus PC510]";
        String multipleProteinIDS = "1000;2000;3000";
        String multipleMsMsIds = "40;41;42";
        // Setup argument
        Map<String, String> values = new HashMap<>();
        values.put(EvidenceHeaders.Sequence.getColumnName(), sequence);
        values.put(EvidenceHeaders.Mass.getColumnName(), Double.toString(mass));
        values.put(EvidenceHeaders.Proteins.getColumnName(), ProteinString);
        values.put(EvidenceHeaders.Protein_Group_IDs.getColumnName(), multipleProteinIDS);
        values.put(EvidenceHeaders.Modifications.getColumnName(), "Unmodified");
        values.put(EvidenceHeaders.Score.getColumnName(), "77");
        values.put(EvidenceHeaders.MS_MS_IDs.getColumnName(), multipleMsMsIds);
        
    }

    @Test
    public void testLinkPeptideToModificationsOxidization() throws HeaderEnumNotInitialisedException {
        // Setup arguments
        Peptide peptide = new Peptide();
        Map<String, String> values = new HashMap<>();
        values.put(EvidenceHeaders.Oxidation_M_Probabilities.getColumnName(), "AAM(1)GNFAAFSAIPGVEVR");
        values.put(EvidenceHeaders.Modifications.getColumnName(),"1");
        //maxQuantEvidenceParser.linkPeptideToModifications(peptide, values);


        peptide.addModificationMatch(maxQuantEvidenceParser.extractModifications(values).get(0));


        // Check modification value matches what we expect
        ArrayList<ModificationMatch> modifications = peptide.getModificationMatches();
        Assert.assertEquals(1, modifications.size());
        ModificationMatch pepHasMod = modifications.get(0);
        Assert.assertEquals(2, pepHasMod.getModificationSite());
        Assert.assertEquals(EvidenceHeaders.Oxidation_M_Probabilities.getColumnName(), pepHasMod.getTheoreticPtm());
    }

    @Test
    public void testLinkPeptideToModificationsAcetyl() throws HeaderEnumNotInitialisedException {
        // Setup arguments
        Peptide peptide = new Peptide();
        Map<String, String> values = new HashMap<>();
        String modificationName = EvidenceHeaders.Acetyl_Protein_N_term.getColumnName();
        values.put(modificationName, "1");
        values.put(EvidenceHeaders.Modifications.getColumnName(),"1");
        for (ModificationMatch match : maxQuantEvidenceParser.extractModifications(values)) {
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
