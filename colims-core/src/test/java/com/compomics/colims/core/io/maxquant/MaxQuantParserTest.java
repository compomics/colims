package com.compomics.colims.core.io.maxquant;

import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.IsCloseTo.closeTo;
import org.junit.After;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantParserTest {
    
    @Autowired
    MaxQuantParser maxQuantParser;
    private static File testFolder;
    
    @After
    public void clearMaxQuantParser() {
        maxQuantParser.clearParsedProject();
    }
    
    public MaxQuantParserTest() throws IOException {
        testFolder = new ClassPathResource("data/maxquant").getFile();
    }

    /**
     * Test of parseMaxQuantTextFolder method, of class MaxQuantParser.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testParseMaxQuantTextFolder() throws Exception {
        System.out.println("parseMaxQuantTextFolder");
        maxQuantParser.parseMaxQuantTextFolder(testFolder);
        assertThat(maxQuantParser.hasParsedAFile(), is(true));
    }

    /**
     * Test of hasParsedAFile method, of class MaxQuantParser.
     * 
     * @throws java.io.IOException
     * @throws com.compomics.colims.core.io.parser.impl.HeaderEnumNotInitialisedException
     * @throws com.compomics.colims.core.io.parser.impl.UnparseableException
     */
    @Test
    public void testHasParsedAFile() throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        System.out.println("hasParsedAFile");
        maxQuantParser.clearParsedProject();
        assertThat(maxQuantParser.hasParsedAFile(), is(false));
        maxQuantParser.parseMaxQuantTextFolder(testFolder);
        assertThat(maxQuantParser.hasParsedAFile(), is(true));
        maxQuantParser.clearParsedProject();
        assertThat(maxQuantParser.hasParsedAFile(), is(false));
    }

    /**
     * Test of getIdentificationsFromParsedFile method, of class MaxQuantParser.
     */
    @Test
    public void testGetIdentificationsFromParsedFile() throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        System.out.println("getIdentificationsFromParsedFile");
        Collection result = maxQuantParser.getIdentificationsFromParsedFile();
        assertThat(result.iterator().hasNext(), is(false));
        maxQuantParser.parseMaxQuantTextFolder(testFolder);
        result = maxQuantParser.getIdentificationsFromParsedFile();
        assertThat(result.iterator().hasNext(), is(true));
        assertThat(result.size(), both(is(774)).and(is(maxQuantParser.getSpectraFromParsedFile().size())));
    }

    /**
     * Test of getIdentificationForSpectrum method, of class MaxQuantParser.
     */
    @Test
    public void testGetIdentificationForSpectrum() throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        System.out.println("getIdentificationForSpectrum");
        maxQuantParser.parseMaxQuantTextFolder(testFolder);
        assertThat(maxQuantParser.getIdentificationsFromParsedFile().size(), is(774));
        ArrayList<MSnSpectrum> spectra = new ArrayList<>(maxQuantParser.getSpectraFromParsedFile());
        PeptideAssumption testAssumption = maxQuantParser.getIdentificationForSpectrum(spectra.get(4));
        assertThat(testAssumption.getPeptide().getSequence(), is(not(nullValue())));
        assertThat(testAssumption.getPeptide().getSequence(), is("AADIIDGLRK"));
        assertThat(testAssumption.getPeptide().getParentProteinsNoRemapping().size(), is(1));
        assertThat(testAssumption.getPeptide().getMass(), closeTo(1070.6084, 0.0001));

        //is unmodified
        assertThat(testAssumption.getPeptide().getModificationMatches().isEmpty(), is(true));

        //test modifications
        //acetyl only
        MSnSpectrum togetSpectrum = new MSnSpectrum();
        SpectrumIntUrParameterShizzleStuff testId = new SpectrumIntUrParameterShizzleStuff();
        testId.setSpectrumid(46);
        togetSpectrum.addUrParam(testId);
        testAssumption = maxQuantParser.getIdentificationForSpectrum(togetSpectrum);
        assertThat(testAssumption, is(not(nullValue())));
        assertThat(testAssumption.getPeptide().getModificationMatches().size(), is(1));
        assertThat(testAssumption.getPeptide().getModificationMatches().get(0).getTheoreticPtm(), is("acetyl (protein n-term)"));
        //is N-term
        assertThat(testAssumption.getPeptide().getModificationMatches().get(0).getModificationSite(), is(0));

        //oxidation only
        ((SpectrumIntUrParameterShizzleStuff) togetSpectrum.getUrParam(testId)).setSpectrumid(36);
        testAssumption = maxQuantParser.getIdentificationForSpectrum(togetSpectrum);
        assertThat(testAssumption.getPeptide().getModificationMatches().size(), is(1));
        assertThat(testAssumption.getPeptide().getModificationMatches().get(0).getTheoreticPtm(), is("oxidation (m)"));
        assertThat(testAssumption.getPeptide().getModificationMatches().get(0).getModificationSite(), is(2));

        //multiple oxidations spectra id 37


        //both (don't have an entry for this yet) 442


        //and test if the assumptions were parsed correctly
        ((SpectrumIntUrParameterShizzleStuff) togetSpectrum.getUrParam(testId)).setSpectrumid(487);
        assertThat(maxQuantParser.getIdentificationForSpectrum(togetSpectrum).getScore(), is(58.676));
        ((SpectrumIntUrParameterShizzleStuff) togetSpectrum.getUrParam(testId)).setSpectrumid(605);
        assertThat(maxQuantParser.getIdentificationForSpectrum(togetSpectrum).getScore(), is(101.64));

        //test link between protein groups and peptides
        ((SpectrumIntUrParameterShizzleStuff) togetSpectrum.getUrParam(testId)).setSpectrumid(275);
        Iterator<ProteinMatch> proteinIter = maxQuantParser.getProteinHitsForIdentification(maxQuantParser.getIdentificationForSpectrum(togetSpectrum)).iterator();
        ProteinMatch testProtein = proteinIter.next();
        assertThat(testProtein.getMainMatch(), is("P62917"));
        //assertThat(maxQuantParser.getIdentificationForSpectrum(spectra.get(2246)).getPeptide().getParentProteins().size(), is(2));
        //assertThat(Integer.parseInt(maxQuantParser.getIdentificationForSpectrum(spectra.get(2246)).getPeptide().getParentProteins().get(1)), is(1100));

        //Map<Integer, List<Quantification>> quantificationMap = MaxQuantQuantificationParser.parseMaxQuantQuantification(quantFile);

        //first test if the quantifications are parsed correctly
        //assertThat(quantificationMap.keySet().size(), is(15));

        // assertThat(quantificationMap.get(11).get(0).getIntensity(), is(2169200.0));
        // assertThat(quantificationMap.get(11).get(1).getIntensity(), is(2294200.0));

        // assertThat(quantificationMap.get(11).get(1).getWeight(), is(QuantificationWeight.HEAVY));
        // assertThat(quantificationMap.get(11).get(0).getWeight(), is(QuantificationWeight.LIGHT));

        //PeptideAssumption result = MaxQuantParser.getIdentificationForSpectrum(aSpectrum);
    }

    /**
     * Test of getSpectraFromParsedFile method, of class MaxQuantParser.
     */
    @Test
    public void testGetSpectra() throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        System.out.println("getSpectra");
        maxQuantParser.clearParsedProject();
        Collection result = maxQuantParser.getSpectraFromParsedFile();
        assertThat(result.iterator().hasNext(), is(false));
        maxQuantParser.parseMaxQuantTextFolder(testFolder);
        result = maxQuantParser.getSpectraFromParsedFile();
        assertThat(result.iterator().hasNext(), is(true));
        assertThat(result.size(), is(774));
    }

    /**
     * Test of getBestProteinHitForIdentification method, of class
     * MaxQuantParser.
     */
    @Test
    public void testGetBestProteinHitForIdentification() throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        System.out.println("getBestProteinHitForIdentification");
        maxQuantParser.parseMaxQuantTextFolder(testFolder);
        com.compomics.util.experiment.biology.Peptide testPeptide = new com.compomics.util.experiment.biology.Peptide();
        testPeptide.setParentProteins(new ArrayList<String>() {
            {
                this.add("354");
            }
        });
        ProteinMatch result = maxQuantParser.getBestProteinHitForIdentification(new PeptideAssumption(testPeptide, 1, 1, null, -1.0));
        assertThat(result, is(notNullValue()));
    }
}