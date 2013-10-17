package com.compomics.colims.core.io.parser.impl;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class MaxQuantMsmsParserTest {
    File getFile(final String filename) {
        return new File(getClass().getClassLoader().getResource(filename).getFile());
    }

    @Test
    public void testMaxQuantEvidenceParser() throws IOException {
        MaxQuantMsmsParser parser = new MaxQuantMsmsParser();

        // Parse file
        //parser.parse(getFile("testdata/msms_subset_1000.tsv"));

        // Assertions
        // TODO fail("Not yet implemented");
    }
}
