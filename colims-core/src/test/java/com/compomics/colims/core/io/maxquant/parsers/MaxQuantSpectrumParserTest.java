package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.core.io.maxquant.UnparseableException;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.enums.FragmentationType;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantSpectrumParserTest {

    @Autowired
    private MaxQuantSpectrumParser maxQuantSpectrumParser;

    @Test
    public void testParse() throws Exception {
        // excluding peaklist as is tested separately
        Map<Spectrum, Integer> result = maxQuantSpectrumParser.parse(MaxQuantTestSuite.msmsFile);
        List<String> rawFile = Files.readAllLines(MaxQuantTestSuite.msmsFile.toPath());

        Spectrum spectrum = result.keySet().iterator().next();

        // TODO: better test cases

        assertThat(result.size(), lessThan(rawFile.size() - 1));
        assertThat(rawFile.get(1), containsString(spectrum.getTitle().split("-")[0]));
        //assertThat(result.get(0).getPeakList().size(), is(19));
        assertThat(spectrum.getRetentionTime(), not(0.0));
        //assertThat(result.get(0).asMgf(), containsString("TITLE=" + result.get(0).getSpectrumTitle()));
    }

    @Ignore
    @Test
    public void testParseFragmentations() throws IOException, UnparseableException {
//        Map<Integer, FragmentationType> fragmentations = maxQuantSpectrumParser.parseFragmentations(MaxQuantTestSuite.msmsFile);
//        List<String> rawFile = Files.readAllLines(MaxQuantTestSuite.msmsFile.toPath());
//
//        assertThat(fragmentations.size(), is(rawFile.size() - 1));
//        assertThat(rawFile.get(1), containsString(fragmentations.get(0).name()));
    }

    @Test
    public void testParsePeakList() {
        String peaks = "y1;y2;y3";
        String intensities = "100.0;200.2;300.3";
        String masses = "500.5;600.6;700.7";

        Map<Double, Double> peakMap = maxQuantSpectrumParser.parsePeakList(peaks, intensities, masses);

        assertThat(peakMap.size(), is(3));
        assertTrue(peakMap.keySet().contains(500.5));
        assertThat(peakMap.get(500.5), is(100.0));
    }
}