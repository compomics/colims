/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.mzml;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.model.*;
import org.slf4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MzMLParserTest {

    @Autowired
    private MzMLParser mzMLParser;

    /**
     * Tests if an IllegalArgumentException is thrown if no mapping could be
     * found for the given MzML file name.
     *
     * @throws java.io.IOException in case of an I/O related problem
     * @throws uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException
     * @throws com.compomics.colims.core.io.MappingException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentException() throws IOException, MzMLUnmarshallerException, MappingException {
        //import test mzML file
        List<File> mzMLFiles = new ArrayList<>();
        File mzMLFile = new ClassPathResource("data/test_mzML_1.mzML").getFile();
        mzMLFiles.add(mzMLFile);

        mzMLParser.importMzMLFiles(mzMLFiles);

        //try to parse unknown mzML file, should throw IllegalArgumentArgumentException
        Experiment experiment = mzMLParser.parseMzMlFile("unknown_MzML_file");
    }

    @Ignore
    @Test
    public void testParseMzmlFile() throws IOException, MzMLUnmarshallerException, MappingException {
        //import test mzML file
        List<File> mzMLFiles = new ArrayList<>();
        File mzMLFile = new ClassPathResource("data/test_mzML_1.mzML").getFile();
        mzMLFiles.add(mzMLFile);

        mzMLParser.importMzMLFiles(mzMLFiles);

        //import mzML file
        Experiment experiment = mzMLParser.parseMzMlFile(mzMLFile.getName());

        //get experiment
        assertNotNull(experiment);
        assertNotNull(experiment.getSamples());
        assertEquals(1, experiment.getSamples().size());

        //get first sample
        assertNotNull(experiment.getSamples().get(0));
        Sample sample = experiment.getSamples().get(0);
        assertNotNull(sample.getAnalyticalRuns());
        assertEquals(1, sample.getAnalyticalRuns().size());

        //get first run
        assertNotNull(sample.getAnalyticalRuns().get(0));
        AnalyticalRun analyticalRun = sample.getAnalyticalRuns().get(0);
        assertEquals("Exp01", analyticalRun.getName());
        assertNotNull(analyticalRun.getStartDate());
        assertNotNull(analyticalRun.getSpectrums());
        assertEquals(1, analyticalRun.getSpectrums().size());

        //get first spectrum
        assertNotNull(analyticalRun.getSpectrums().get(0));
        Spectrum spectrum = analyticalRun.getSpectrums().get(0);
        assertEquals("test_mzML_1.mzML_cus_scan=20", spectrum.getAccession());
        assertEquals(445.34, spectrum.getMzRatio(), 0.001);
        assertEquals(Integer.valueOf(2), spectrum.getCharge());
        assertEquals(5.9905, spectrum.getScanTime(), 0.001);
        assertNotNull(spectrum.getSpectrumFiles());
        assertEquals(1, spectrum.getSpectrumFiles().size());

        //get first spectrum file
        assertNotNull(spectrum.getSpectrumFiles().get(0));
        SpectrumFile spectrumFile = spectrum.getSpectrumFiles().get(0);
        assertNotNull(spectrumFile.getContent());
        assertFalse(spectrumFile.getContent().length == 0);
    }
}
