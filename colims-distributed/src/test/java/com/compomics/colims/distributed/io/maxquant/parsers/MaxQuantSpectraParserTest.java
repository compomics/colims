package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantSpectraParserTest {

    @Autowired
    private MaxQuantSpectraParser maxQuantSpectraParser;

    @Test
    public void testParse() throws Exception {
        Set<Integer> omittedProteinIds = new HashSet<>();
        omittedProteinIds.add(0);
        omittedProteinIds.add(1);

        maxQuantSpectraParser.clear();
        maxQuantSpectraParser.parse(MaxQuantTestSuite.maxQuantCombinedDirectory, false, omittedProteinIds);

        MaxQuantSpectra maxQuantSpectra = maxQuantSpectraParser.getMaxQuantSpectra();

        Assert.assertEquals(2864, maxQuantSpectra.getSpectrumToPsms().size());
        Assert.assertTrue(maxQuantSpectra.getUnidentifiedSpectra().isEmpty());
    }

    @Test
    public void testParseIncludeUnIdentified() throws Exception {
        Set<Integer> omittedProteinIds = new HashSet<>();
        omittedProteinIds.add(0);
        omittedProteinIds.add(1);

        // test for the unidentified spectra
        maxQuantSpectraParser.clear();
        maxQuantSpectraParser.parse(MaxQuantTestSuite.maxQuantCombinedDirectory, true, omittedProteinIds);

        MaxQuantSpectra maxQuantSpectra = maxQuantSpectraParser.getMaxQuantSpectra();

        Assert.assertEquals(2864, maxQuantSpectra.getSpectrumToPsms().size());
        int numberOfUnidentifiedSpectra = 0;
        numberOfUnidentifiedSpectra = maxQuantSpectra.getUnidentifiedSpectra().values().stream().map((unidentifiedSpectra) -> unidentifiedSpectra.size()).reduce(numberOfUnidentifiedSpectra, Integer::sum);
        Assert.assertEquals(11306, numberOfUnidentifiedSpectra);
    }

}
