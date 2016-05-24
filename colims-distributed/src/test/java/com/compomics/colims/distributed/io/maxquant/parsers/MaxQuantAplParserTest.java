package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.enums.FragmentationType;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantAplParserTest {

    private static final Logger LOGGER = Logger.getLogger(MaxQuantAplParser.class);
    private static final String APL_HEADER_DELIMITER = "=";

    @Autowired
    private MaxQuantAplParser maxQuantAplParser;

    /**
     * Test the init method.
     *
     * @throws Exception in case something goes wrong
     */
    @Test
    public void testInit() throws Exception {
        maxQuantAplParser.init(MaxQuantTestSuite.maxQuantAndromedaDirectory);

        FragmentationType fragmentationType = maxQuantAplParser.getFragmentationType();
        MaxQuantConstants.Analyzer massAnalyzerType = maxQuantAplParser.getMassAnalyzerType();
        Map<String, String> aplFiles = maxQuantAplParser.getAplFiles();

        Assert.assertEquals(fragmentationType, FragmentationType.CID);
        Assert.assertEquals(massAnalyzerType, MaxQuantConstants.Analyzer.ITMS);
        Assert.assertEquals(2, aplFiles.size());
    }

    /**
     * Test the parse method.
     *
     * @throws Exception in case something goes wrong
     */
    @Test
    public void testParse() throws Exception {
        maxQuantAplParser.init(MaxQuantTestSuite.maxQuantAndromedaDirectory);

        //replace the apl files with one small one
        Map<String, String> aplFiles = maxQuantAplParser.getAplFiles();
        aplFiles.clear();
        aplFiles.put("allSpectra.CID.ITMS.iso_0_part.apl", "");

        //create some dummy spectra
        Map<String, Spectrum> spectra = new HashMap<>();

        //create dummy spectrum one
        String spectrumKey1 = "RawFile: 120329kw_JEnglish_JE10_1 Index: 496";
        Spectrum spectrum1 = new Spectrum();
        spectrum1.setAccession("acc_1");
        spectrum1.setRetentionTime(123.45);
        spectra.put(spectrumKey1, spectrum1);

        //create another dummy spectrum one
        String spectrumKey2 = "RawFile: 120329kw_JEnglish_JE10_1 Index: 7729";
        Spectrum spectrum2 = new Spectrum();
        spectrum2.setAccession("acc_2");
        spectrum2.setRetentionTime(123.46);
        spectra.put(spectrumKey2, spectrum2);

        maxQuantAplParser.parse(spectra, true);
        byte[] unzippedBytes = IOUtils.unzip(spectra.get(spectrumKey1).getSpectrumFiles().get(0).getContent());
        try (ByteArrayInputStream bais = new ByteArrayInputStream(unzippedBytes);
             InputStreamReader isr = new InputStreamReader(bais, Charset.forName("UTF-8").newDecoder());
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            Map<String, String> headers = new HashMap<>();
            //go to the next line
            br.readLine();
            //parse spectrum header part
            while ((line = br.readLine()) != null && !Character.isDigit(line.charAt(0))){
                String[] split = line.split(APL_HEADER_DELIMITER);
                headers.put(split[0], split[1]);
            }
            Assert.assertEquals("RawFile: 120329kw_JEnglish_JE10_1 Index: 496 Precursor: 0 _multi_", headers.get("TITLE"));
     //       Assert.assertEquals(spectrum1.getRetentionTime().toString(), headers.get("RTINSECONDS"));
            Assert.assertEquals("303.176402121713", headers.get("PEPMASS"));
            Assert.assertEquals("1+", headers.get("CHARGE"));
        }catch (IOException ex) {
            LOGGER.error(ex);
        }
        System.out.println("");
    }
}