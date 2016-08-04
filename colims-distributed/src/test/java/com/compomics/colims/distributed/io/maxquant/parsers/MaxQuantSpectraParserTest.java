package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantSpectraParserTest {

    @Autowired
    private MaxQuantSpectraParser maxQuantSpectraParser;

    @Test
    public void testParse() throws Exception {
        List<String> omittedProteinIds = new ArrayList<>();
        omittedProteinIds.add("0");
        omittedProteinIds.add("1");

        maxQuantSpectraParser.parse(MaxQuantTestSuite.maxQuantCombinedDirectory, false, omittedProteinIds);

        MaxQuantSpectra maxQuantSpectra = maxQuantSpectraParser.getMaxQuantSpectra();

        Assert.assertEquals(21, maxQuantSpectra.getSpectrumIDs().size());
        Assert.assertTrue(maxQuantSpectra.getUnidentifiedSpectra().isEmpty());

        // test for the unidentified spectra
        maxQuantSpectraParser.parse(MaxQuantTestSuite.maxQuantCombinedDirectory, true, omittedProteinIds);

        MaxQuantSpectra maxQuantSpectra2 = maxQuantSpectraParser.getMaxQuantSpectra();

        Assert.assertEquals(21, maxQuantSpectra2.getSpectrumIDs().size());
        Assert.assertEquals(18902, maxQuantSpectra2.getUnidentifiedSpectra().size());
    }
}
