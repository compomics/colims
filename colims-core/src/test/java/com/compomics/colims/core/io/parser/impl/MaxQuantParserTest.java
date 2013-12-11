package com.compomics.colims.core.io.parser.impl;

import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.hamcrest.CoreMatchers.*;
import org.junit.After;

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

    @After
    public void clearMaxQuantParser() {
        maxQuantParser.clearParsedProject();
    }

    public MaxQuantParserTest() {
        testFolder = new File(getClass().getClassLoader().getResource("testdata").getPath());
        //testFolder = new File("C:\\Users\\Davy\\Desktop\\java\\colims\\colims-core\\target\\test-classes\\testdata\\");
    }

    /**
     * Test of parseMaxQuantTextFolder method, of class MaxQuantParser.
     */
    @Test
    public void testParseMaxQuantTextFolder() throws Exception {
        System.out.println("parseMaxQuantTextFolder");
        maxQuantParser.parseMaxQuantTextFolder(testFolder);
    }

    /**
     * Test of hasParsedAFile method, of class MaxQuantParser.
     */
    @Test
    public void testHasParsedAFile() throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        System.out.println("hasParsedAFile");
        maxQuantParser.clearParsedProject();
        boolean expResult = false;
        assertThat(maxQuantParser.hasParsedAFile(), is(expResult));
        expResult = true;
        maxQuantParser.parseMaxQuantTextFolder(testFolder);
        assertThat(maxQuantParser.hasParsedAFile(), is(expResult));
    }

    /**
     * Test of getIdentificationsFromParsedFile method, of class MaxQuantParser.
     */
    @Test
    public void testGetIdentificationsFromParsedFile() throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        System.out.println("getIdentificationsFromParsedFile");
        Iterator result = maxQuantParser.getIdentificationsFromParsedFile();
        assertThat(result.hasNext(), is(false));
        maxQuantParser.parseMaxQuantTextFolder(testFolder);
        result = maxQuantParser.getIdentificationsFromParsedFile();
        assertThat(result.hasNext(), is(true));
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
        Iterator result = maxQuantParser.getSpectra();
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