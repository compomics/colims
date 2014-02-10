package com.compomics.colims.core.io.maxquant;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ProteinAccessioncodeParserTest {
    @Test
    public void testGetProteinAccessioncodeMatchingRegexes() {
        List<String> regexes = ProteinAccessioncodeParser.getProteinAccessioncodeMatchingRegexes();
        Assert.assertFalse(regexes.isEmpty());
    }

    @Test
    public void testExtractProteinAccessioncodes() {
        // Setup arguments
        String proteinsLine = "gi|150003706|ref|YP_001298450.1| rubrerythrin [Bacteroides vulgatus ATCC 8482];gi|294777471|ref|ZP_06742922.1| rubrerythrin [Bacteroides vulgatus PC510]";

        // Invoke tested method
        List<String> accessioncodes = ProteinAccessioncodeParser.extractProteinAccessioncodes(proteinsLine);

        // Compare values
        Assert.assertArrayEquals(new String[] { "YP_001298450.1", "ZP_06742922.1" }, accessioncodes.toArray());
    }
}
