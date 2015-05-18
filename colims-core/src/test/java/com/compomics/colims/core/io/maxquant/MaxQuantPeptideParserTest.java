package com.compomics.colims.core.io.maxquant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantEvidenceHeaders;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.colims.repository.ModificationRepository;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantPeptideParserTest {

    @Autowired
    MaxQuantEvidenceParser maxQuantEvidenceParser;
    @Autowired
    ModificationRepository modificationRepository;

    @Before
    public void clearModifications() {
        //reset the modifications in the parser, this happens automatically in the parse method
        maxQuantEvidenceParser.clear();
    }

    @Test
    public void testCreatePeptide() throws UnparseableException, HeaderEnumNotInitialisedException {
        // Expected values
        String sequence = "ABCDEFG";
        double mass = 123.45;
        String ProteinString = "gi|150003706|ref|YP_001298450.1| rubrerythrin [Bacteroides vulgatus ATCC 8482];gi|294777471|ref|ZP_06742922.1| rubrerythrin [Bacteroides vulgatus PC510]";
        String singleProteinID = "1000";
        // Setup argument
        Map<String, String> values = new HashMap<>();
        values.put(MaxQuantEvidenceHeaders.SEQUENCE.getColumnName(), sequence);
        values.put(MaxQuantEvidenceHeaders.MASS.getColumnName(), Double.toString(mass));
        values.put(MaxQuantEvidenceHeaders.PROTEINS.getColumnName(), ProteinString);
        values.put(MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS.getColumnName(), singleProteinID);
        values.put(MaxQuantEvidenceHeaders.MODIFICATIONS.getColumnName(), "Unmodified");
        values.put(MaxQuantEvidenceHeaders.SCORE.getColumnName(), "77");
        values.put(MaxQuantEvidenceHeaders.MS_MS_IDS.getColumnName(), "42");
        // Invoke tested method
        PeptideAssumption peptide = maxQuantEvidenceParser.createPeptideAssumption(values);

        // Compare values
        Assert.assertEquals(sequence, peptide.getPeptide().getSequence());
        //Assert.assertEquals(mass, peptide.getMass(), 0.001);

    }

    @Test
    public void testCreatePeptideMultipleMsMsAndProteinGroupValues() throws HeaderEnumNotInitialisedException {
        String sequence = "ABCDEFG";
        double mass = 123.45;
        String ProteinString = "gi|150003706|ref|YP_001298450.1| rubrerythrin [Bacteroides vulgatus ATCC 8482];gi|294777471|ref|ZP_06742922.1| rubrerythrin [Bacteroides vulgatus PC510]";
        String multipleProteinIDS = "1000;2000;3000";
        String multipleMsMsIds = "40;41;42";
        // Setup argument
        Map<String, String> values = new HashMap<>();
        values.put(MaxQuantEvidenceHeaders.SEQUENCE.getColumnName(), sequence);
        values.put(MaxQuantEvidenceHeaders.MASS.getColumnName(), Double.toString(mass));
        values.put(MaxQuantEvidenceHeaders.PROTEINS.getColumnName(), ProteinString);
        values.put(MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS.getColumnName(), multipleProteinIDS);
        values.put(MaxQuantEvidenceHeaders.MODIFICATIONS.getColumnName(), "Unmodified");
        values.put(MaxQuantEvidenceHeaders.SCORE.getColumnName(), "77");
        values.put(MaxQuantEvidenceHeaders.MS_MS_IDS.getColumnName(), multipleMsMsIds);

    }
}
