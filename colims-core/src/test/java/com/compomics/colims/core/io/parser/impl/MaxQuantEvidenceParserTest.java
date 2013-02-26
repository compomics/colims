package com.compomics.colims.core.io.parser.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.Peptide;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml" })
public class MaxQuantEvidenceParserTest {
    File getFile(final String filename) {
        return new File(getClass().getClassLoader().getResource(filename).getFile());
    }

    @Autowired
    MaxQuantEvidenceParser parser;

    @Test
    @Transactional
    public void testMaxQuantEvidenceParser() throws IOException {
        // Parse file
        parser.parse(getFile("testdata/evidence_subset_1000.tsv"));

        // Assertions
        // TODO fail("Not yet implemented");
    }

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
}
