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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
        FastaDb maxQuantTestFastaDb = new FastaDb();
        maxQuantTestFastaDb.setName(MaxQuantTestSuite.fastaFile.getName());
        maxQuantTestFastaDb.setFileName(MaxQuantTestSuite.fastaFile.getName());
        maxQuantTestFastaDb.setFilePath(MaxQuantTestSuite.fastaFile.getAbsolutePath());

        EnumMap<FastaDbType, FastaDb> fastaDbs = new EnumMap<>(FastaDbType.class);
        fastaDbs.put(FastaDbType.PRIMARY, maxQuantTestFastaDb);

        maxQuantParser.clear();
        maxQuantParser.parseFolder(MaxQuantTestSuite.maxQuantTextFolder, fastaDbs);
    }

    @Test
    public void testParseFasta() throws IOException {
        Resource contaminantsFasta = new ClassPathResource("config/contaminants.fasta");
        FastaDb contaminantsFastaDb = new FastaDb();
        contaminantsFastaDb.setName("contaminants");
        contaminantsFastaDb.setFileName(contaminantsFasta.getFilename());
        contaminantsFastaDb.setFilePath(contaminantsFasta.getURI().getPath());
        List<FastaDb> fastaDbs = new ArrayList<>();
        fastaDbs.add(contaminantsFastaDb);

        Map<String, String> parsedContaminantsFasta = maxQuantParser.parseFastas(fastaDbs);

        Assert.assertEquals(246, parsedContaminantsFasta.size());
    }

    /**
     * Test of getIdentificationForSpectrum method, of class MaxQuantParser.
     *
     * @throws java.lang.Exception in case of an exception
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
     *
     * @throws java.lang.Exception in case of an exception
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