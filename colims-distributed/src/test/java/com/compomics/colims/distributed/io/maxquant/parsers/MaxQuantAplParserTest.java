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
        testAplFile = new ClassPathResource("data" + File.separator + "maxquant_test1" + File.separator + "combined" + File.separator + "andromeda" + File.separator + "allSpectra.CID.ITMS.iso_0.apl").getFile().toPath();
    }

    /**
     * Test the parse method for identified spectra.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testParseAplFile() throws IOException {
        //create some dummy maxQuantSpectra object
        MaxQuantSpectra maxQuantSpectra = new MaxQuantSpectra();

        //create dummy spectrum one
        String spectrumKey1 = "RawFile: V20239_3911_Eik_green_10 Index: 4047";
        Spectrum spectrum1 = new Spectrum();
        spectrum1.setAccession("acc_1");
        spectrum1.setRetentionTime(123.45);
        maxQuantSpectra.getAplKeyToSpectrums().put(spectrumKey1, spectrum1);

        //create another dummy spectrum one
        String spectrumKey2 = "RawFile: V20239_3911_Eik_green_10 Index: 1084";
        Spectrum spectrum2 = new Spectrum();
        spectrum2.setAccession("acc_2");
        spectrum2.setRetentionTime(123.46);
        maxQuantSpectra.getAplKeyToSpectrums().put(spectrumKey2, spectrum2);

        maxQuantAplParser.parseAplFile(testAplFile, maxQuantSpectra, false);
        //check the sizes
        //2 identified spectra
        Assert.assertEquals(2, maxQuantSpectra.getAplKeyToSpectrums().size());
        //don't include unidentified ones
        Assert.assertTrue(maxQuantSpectra.getUnidentifiedSpectra().isEmpty());

        //do some additional testing
        byte[] unzippedBytes = IOUtils.unzip(maxQuantSpectra.getAplKeyToSpectrums().get(spectrumKey1).getSpectrumFiles().get(0).getContent());
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
            Assert.assertEquals("RawFile: V20239_3911_Eik_green_10 Index: 4047 Precursor: 0 _multi_", headers.get("TITLE"));
            Assert.assertEquals(spectrum1.getRetentionTime().toString(), headers.get("RTINSECONDS"));
            Assert.assertEquals("300.178852107217", headers.get("PEPMASS"));
            Assert.assertEquals("1+", headers.get("CHARGE"));
        }

    }

    /**
     * Test the parse method for unidentified spectra.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testParseAplFileUnidentified() throws IOException {

        //create some dummy maxQuantSpectra object
        MaxQuantSpectra maxQuantSpectra = new MaxQuantSpectra();

        //create dummy spectrum one
        String spectrumKey = "RawFile: V20239_3911_Eik_green_10 Index: 4047";
        Spectrum spectrum = new Spectrum();
        spectrum.setAccession("acc_1");
        spectrum.setRetentionTime(123.45);
        maxQuantSpectra.getAplKeyToSpectrums().put(spectrumKey, spectrum);
        maxQuantSpectra.getOmmittedSpectrumKeys().add("RawFile: V20263_3910_Eik_red_12 Index: 1986");
        maxQuantSpectra.getOmmittedSpectrumKeys().add("RawFile: V20263_3910_Eik_red_12 Index: 1975");

        maxQuantAplParser.parseAplFile(testAplFile, maxQuantSpectra, true);

        //check the sizes
        //one identified
        Assert.assertEquals(1, maxQuantSpectra.getAplKeyToSpectrums().size());
        //6 unidentified
        Assert.assertEquals(15957, maxQuantSpectra.getUnidentifiedSpectra().size());

        //some additional testing
        byte[] unzippedBytes = IOUtils.unzip(maxQuantSpectra.getUnidentifiedSpectra().get(0).getSpectrumFiles().get(0).getContent());
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
            Assert.assertEquals("RawFile: V20239_3911_Eik_green_10 Index: 1084 Precursor: 0 _multi_", headers.get("TITLE"));
            Assert.assertEquals("300.202816933043", headers.get("PEPMASS"));
            Assert.assertEquals("1+", headers.get("CHARGE"));
        }
    }
}