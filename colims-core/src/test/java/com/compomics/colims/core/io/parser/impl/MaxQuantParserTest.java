/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.parser.impl;

import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Davy
 */
public class MaxQuantParserTest {

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
        MaxQuantParser instance = new MaxQuantParser();
        instance.parseMaxQuantTextFolder(maxQuantTextFolder);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIdentificationsFromParsedFile method, of class MaxQuantParser.
     */
    @Test
    public void testGetIdentificationsFromParsedFile() {
        System.out.println("getIdentificationsFromParsedFile");
        Iterator expResult = null;
        Iterator result = MaxQuantParser.getIdentificationsFromParsedFile();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIdentificationForSpectrum method, of class MaxQuantParser.
     */
    @Test
    public void testGetIdentificationForSpectrum() {
        System.out.println("getIdentificationForSpectrum");
        MSnSpectrum aSpectrum = null;
        PeptideAssumption expResult = null;
        PeptideAssumption result = MaxQuantParser.getIdentificationForSpectrum(aSpectrum);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSpectra method, of class MaxQuantParser.
     */
    @Test
    public void testGetSpectra() {
        System.out.println("getSpectra");
        Iterator expResult = null;
        Iterator result = MaxQuantParser.getSpectra();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBestProteinHitForIdentification method, of class
     * MaxQuantParser.
     */
    @Test
    public void testGetBestProteinHitForIdentification() {
        System.out.println("getBestProteinHitForIdentification");
        PeptideAssumption aPeptideAssumption = null;
        ProteinMatch expResult = null;
        ProteinMatch result = MaxQuantParser.getBestProteinHitForIdentification(aPeptideAssumption);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}