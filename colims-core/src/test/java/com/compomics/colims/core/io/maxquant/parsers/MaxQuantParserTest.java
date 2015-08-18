package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.core.io.maxquant.UnparseableException;

import java.io.IOException;
import java.util.*;

import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.greaterThan;
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

    @Before
    public void setUp() throws MappingException, UnparseableException, IOException {
        FastaDb maxQuantTestFastaDb = new FastaDb();
        maxQuantTestFastaDb.setName(MaxQuantTestSuite.fastaFile.getName());
        maxQuantTestFastaDb.setFileName(MaxQuantTestSuite.fastaFile.getName());
        maxQuantTestFastaDb.setFilePath(MaxQuantTestSuite.fastaFile.getAbsolutePath());

        maxQuantParser.clear();
        maxQuantParser.parseFolder(MaxQuantTestSuite.maxQuantTextFolder, maxQuantTestFastaDb);
    }

    @Test
    public void testParseFasta() throws IOException {
        FastaDb maxQuantTestFastaDb = new FastaDb();
        maxQuantTestFastaDb.setName(MaxQuantTestSuite.fastaFile.getName());
        maxQuantTestFastaDb.setFileName(MaxQuantTestSuite.fastaFile.getName());
        maxQuantTestFastaDb.setFilePath(MaxQuantTestSuite.fastaFile.getAbsolutePath());

        Map<String, String> parsedFasta = maxQuantParser.parseFasta(maxQuantTestFastaDb);

        assertThat(parsedFasta.size(), greaterThan(0));
        // TODO: more test cases
    }

    /**
     * Test of getIdentificationForSpectrum method, of class MaxQuantParser.
     */
    @Test
    public void testGetIdentificationForSpectrum() throws Exception {
        Spectrum spectrum = maxQuantParser.getSpectra().keySet().iterator().next();
        Peptide assumption = maxQuantParser.getIdentificationForSpectrum(spectrum);

        assertThat(assumption, isA(Peptide.class));

        maxQuantParser.clear();

        assumption = maxQuantParser.getIdentificationForSpectrum(spectrum);

        assertThat(assumption, nullValue());
    }

    /**
     * Test of getSpectra method, of class MaxQuantParser.
     */
    @Test
    public void testGetSpectra() throws Exception {
        Map<Spectrum, Integer> spectra = maxQuantParser.getSpectra();
        assertThat(spectra.size(), not(0));
        assertThat(spectra.keySet().iterator().next(), isA(Spectrum.class));

        maxQuantParser.clear();
        spectra = maxQuantParser.getSpectra();
        assertThat(spectra.size(), is(0));
    }

    /**
     * Test of hasParsed method, of class MaxQuantParser
     *
     * @throws Exception
     */
    @Test
    public void testHasParsed() throws Exception {
        assertThat(maxQuantParser.hasParsed(), is(true));
        maxQuantParser.clear();
        assertThat(maxQuantParser.hasParsed(), is(false));
    }

    @Test
    public void testGetProteinHitsForIdentification() {
        // TODO: rewrite
//        maxQuantParser.
//        com.compomics.util.experiment.biology.Peptide peptide = new com.compomics.util.experiment.biology.Peptide();
//        peptide.setParentProteins(new ArrayList<String>() {
//            {
//                this.add("35");
//            }
//        });
//
//        Peptide assumption = new PeptideAssumption(peptide, 1, 1, null, -1.0);
//        List<ProteinMatch> matches = new ArrayList<>(maxQuantParser.getProteinHitsForIdentification(assumption));
//
//        assertThat(matches.size(), is(1));
//        assertThat(matches.get(0).getPeptideCount(), not(0));
//
//        maxQuantParser.clear();
//
//        matches = new ArrayList<>(maxQuantParser.getProteinHitsForIdentification(assumption));
//
//        // test when parser contains no data
//        assertThat(matches.size(), is(0));
    }
}