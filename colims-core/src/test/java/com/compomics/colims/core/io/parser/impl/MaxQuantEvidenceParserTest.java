package com.compomics.colims.core.io.parser.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.compomics.colims.model.Peptide;

public class MaxQuantEvidenceParserTest {
    File getFile(final String filename) {
        return new File(getClass().getClassLoader().getResource(filename).getFile());
    }

    @Test
    public void testMaxQuantEvidenceParser() throws IOException {
        MaxQuantEvidenceParser parser = new MaxQuantEvidenceParser();

        // Parse file
        parser.parse(getFile("testdata/evidence_subset_1000.tsv"));

        // Assertions
        // TODO fail("Not yet implemented");
    }

    @Test
    public void testExtractPeptide() {
        // Expected values
        String sequence = "ABCDEFG";
        double mass = 123.45;

        // Setup argument
        Map<String, String> values = new HashMap<>();
        values.put(EvidenceHeaders.Sequence.column, sequence);
        values.put(EvidenceHeaders.Mass.column, Double.toString(mass));

        // Invoke tested method
        Peptide peptide = MaxQuantEvidenceParser.extractPeptide(values);

        // Compare values
        Assert.assertEquals(sequence, peptide.getSequence());
        Assert.assertEquals(mass, peptide.getTheoreticalMass(), 0.001);
    }

    @Test
    public void testExtractProteinAccessioncodes() {
        // Setup arguments
        Map<String, String> values = new HashMap<>();
        values.put(EvidenceHeaders.Proteins.column, "gi|150003706|ref|YP_001298450.1| rubrerythrin [Bacteroides vulgatus ATCC 8482];gi|294777471|ref|ZP_06742922.1| rubrerythrin [Bacteroides vulgatus PC510]");

        // Invoke tested method
        List<String> accessioncodes = MaxQuantEvidenceParser.extractProteinAccessioncodes(values);

        // Compare values
        Assert.assertArrayEquals(new String[] { "YP_001298450.1", "ZP_06742922.1" }, accessioncodes.toArray());
    }
}
