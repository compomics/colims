package com.compomics.colims.core.io.parser.impl;

import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantParserTest {

    @Autowired
    MaxQuantParser maxQuantParser;
    public static File testFolder;

    public MaxQuantParserTest() {
        testFolder = new File(getClass().getClassLoader().getResource("testdata").toString());
    }

    /**
     * Test of parseMaxQuantTextFolder method, of class MaxQuantParser.
     */
    @Test
    public void testParseMaxQuantTextFolder() throws Exception {
        System.out.println("parseMaxQuantTextFolder");
        Path maxQuantTextFolder = null;
//        maxQuantParser.parseMaxQuantTextFolder(maxQuantTextFolder);
    }

    /**
     * Test of getIdentificationsFromParsedFile method, of class MaxQuantParser.
     */
    @Test
    public void testGetIdentificationsFromParsedFile() {
        System.out.println("getIdentificationsFromParsedFile");
        Iterator result = MaxQuantParser.getIdentificationsFromParsedFile();
        assertThat(result.hasNext(), is(false));
    }

    /**
     * Test of hasParsedAFile method, of class MaxQuantParser.
     */
    @Test
    public void testHasParsedAFile() {
        System.out.println("hasParsedAFile");
        boolean expResult = false;
        boolean result = MaxQuantParser.hasParsedAFile();
        assertEquals(expResult, result);
    }

    /**
     * Test of getIdentificationForSpectrum method, of class MaxQuantParser.
     */
    @Test
    public void testGetIdentificationForSpectrum() {
        System.out.println("getIdentificationForSpectrum");
        MSnSpectrum aSpectrum = null;
//        PeptideAssumption result = MaxQuantParser.getIdentificationForSpectrum(aSpectrum);
    }

    /**
     * Test of getSpectra method, of class MaxQuantParser.
     */
    @Test
    public void testGetSpectra() {
        System.out.println("getSpectra");
        Iterator result = MaxQuantParser.getSpectra();
        assertThat(result.hasNext(), is(false));
    }

    /**
     * Test of getBestProteinHitForIdentification method, of class
     * MaxQuantParser.
     */
    @Test
    public void testGetBestProteinHitForIdentification() {
        System.out.println("getBestProteinHitForIdentification");
//        ProteinMatch result = MaxQuantParser.getBestProteinHitForIdentification(new PeptideAssumption(new com.compomics.util.experiment.biology.Peptide(), 1, 99, null, 99));
    }
}