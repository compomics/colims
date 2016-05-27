package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.model.Spectrum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantAplParserTest {

    private static final String APL_HEADER_DELIMITER = "=";
    private Path testAplFile;

    @Autowired
    private MaxQuantAplParser maxQuantAplParser;

    /**
     * No-arg constructor.
     *
     * @throws IOException in case of an I/O related problem
     */
    public MaxQuantAplParserTest() throws IOException {
        testAplFile = new ClassPathResource("data" + File.separator + "maxquant_1.5.2.8" + File.separator + "andromeda" + File.separator + "allSpectra.CID.ITMS.iso_0_part.apl").getFile().toPath();
    }

    /**
     * Test the parse method for identified spectra.
     *
     * @throws Exception in case something goes wrong
     */
    @Test
    public void testParseAplFile() throws Exception {
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

        maxQuantAplParser.parseAplFile(testAplFile, spectra, false);
        Assert.assertEquals(2, spectra.size());
        byte[] unzippedBytes = IOUtils.unzip(spectra.get(spectrumKey1).getSpectrumFiles().get(0).getContent());
        try (ByteArrayInputStream bais = new ByteArrayInputStream(unzippedBytes);
             InputStreamReader isr = new InputStreamReader(bais, Charset.forName("UTF-8").newDecoder());
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            Map<String, String> headers = new HashMap<>();
            //go to the next line
            br.readLine();
            //parse spectrum header part
            while ((line = br.readLine()) != null && !Character.isDigit(line.charAt(0))) {
                String[] split = line.split(APL_HEADER_DELIMITER);
                headers.put(split[0], split[1]);
            }
            Assert.assertEquals("RawFile: 120329kw_JEnglish_JE10_1 Index: 496 Precursor: 0 _multi_", headers.get("TITLE"));
            Assert.assertEquals(spectrum1.getRetentionTime().toString(), headers.get("RTINSECONDS"));
            Assert.assertEquals("303.176402121713", headers.get("PEPMASS"));
            Assert.assertEquals("1+", headers.get("CHARGE"));
        }

    }

    /**
     * Test the parse method for unidentified spectra.
     *
     * @throws Exception in case something goes wrong
     */
    @Test
    public void testParseAplFileUnidentified() throws Exception {
        String spectrumKey = "RawFile: 120329kw_JEnglish_JE10_1 Index: 496";

        //create some dummy spectra for unidentified spectra
        Map<String, Spectrum> spectra = new HashMap<>();
        maxQuantAplParser.parseAplFile(testAplFile, spectra, true);

        //check the size
        Assert.assertEquals(7, spectra.size());
        byte[] unzippedBytes = IOUtils.unzip(spectra.get(spectrumKey).getSpectrumFiles().get(0).getContent());
        try (ByteArrayInputStream bais = new ByteArrayInputStream(unzippedBytes);
             InputStreamReader isr = new InputStreamReader(bais, Charset.forName("UTF-8").newDecoder());
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            Map<String, String> headers = new HashMap<>();
            //go to the next line
            br.readLine();
            //parse spectrum header part
            while ((line = br.readLine()) != null && !Character.isDigit(line.charAt(0))) {
                String[] split = line.split(APL_HEADER_DELIMITER);
                headers.put(split[0], split[1]);
            }
            Assert.assertEquals("RawFile: 120329kw_JEnglish_JE10_1 Index: 496 Precursor: 0 _multi_", headers.get("TITLE"));
            Assert.assertEquals("303.176402121713", headers.get("PEPMASS"));
            Assert.assertEquals("1+", headers.get("CHARGE"));
        }
    }
}