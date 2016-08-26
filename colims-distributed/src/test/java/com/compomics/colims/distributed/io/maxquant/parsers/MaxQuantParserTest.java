package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.distributed.io.maxquant.UnparseableException;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.enums.FastaDbType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantParserTest {

    @Autowired
    MaxQuantParser maxQuantParser;

    @Before
    public void setUp() throws MappingException, UnparseableException, IOException {
        EnumMap<FastaDbType, FastaDb> fastaDbs = new EnumMap<>(FastaDbType.class);
        fastaDbs.put(FastaDbType.PRIMARY, MaxQuantTestSuite.testFastaDb);

        maxQuantParser.clear();
        maxQuantParser.parse(MaxQuantTestSuite.maxQuantCombinedDirectory, fastaDbs, false, new ArrayList<>());
    }

    @Test
    public void testParseFasta() throws IOException {
        List<FastaDb> fastaDbs = new ArrayList<>();
        fastaDbs.add(MaxQuantTestSuite.testFastaDb);
        fastaDbs.add(MaxQuantTestSuite.contaminantsFastaDb);

        Map<String, String> parsedContaminantsFasta = maxQuantParser.parseFastas(fastaDbs);

        Assert.assertEquals(33322, parsedContaminantsFasta.size());
        //look for a protein
        Assert.assertTrue(parsedContaminantsFasta.containsKey("sp|Q9D103|IFM1_MOUSE"));
        //look for a contaminants protein
   //     Assert.assertTrue(parsedContaminantsFasta.containsKey("CON__P09870"));
    }

    /**
     * Test of getIdentificationForSpectrum method, of class MaxQuantParser.
     *
     * @throws java.lang.Exception in case of an exception
     */
    @Test
    public void testGetIdentificationForSpectrum() throws Exception {
        Spectrum spectrum = maxQuantParser.getSpectra().keySet().iterator().next();
        List<Peptide> peptides = maxQuantParser.getIdentificationForSpectrum(spectrum);

        Peptide peptide = peptides.get(0);
        assertThat(peptide, isA(Peptide.class));

        maxQuantParser.clear();
        peptides.clear();
        peptides = maxQuantParser.getIdentificationForSpectrum(spectrum);
        assertThat(peptides.isEmpty(), is(true));
    }

    /**
     * Test of getAplSpectra method, of class MaxQuantParser.
     *
     * @throws java.lang.Exception in case of an exception
     */
    @Test
    public void testGetSpectra() throws Exception {
        Map<Spectrum, List<Integer>> spectra = maxQuantParser.getSpectra();
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