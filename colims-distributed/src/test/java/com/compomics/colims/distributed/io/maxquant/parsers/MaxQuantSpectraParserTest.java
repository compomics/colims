package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.model.Spectrum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.List;
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

        maxQuantSpectraParser.parse(MaxQuantTestSuite.maxQuantCombinedDirectory, false, omittedProteinIds);

        MaxQuantSpectra maxQuantSpectra = maxQuantSpectraParser.getMaxQuantSpectra();

        Assert.assertEquals(21, maxQuantSpectra.getSpectrumToPsms().size());
        Assert.assertTrue(maxQuantSpectra.getUnidentifiedSpectra().isEmpty());

        // test for the unidentified spectra
        maxQuantSpectraParser.parse(MaxQuantTestSuite.maxQuantCombinedDirectory, true, omittedProteinIds);

        MaxQuantSpectra maxQuantSpectra2 = maxQuantSpectraParser.getMaxQuantSpectra();

        Assert.assertEquals(21, maxQuantSpectra2.getSpectrumToPsms().size());
        int numberOfUnidentifiedSpectra = 0;
        for (List<Spectrum> unidentifiedSpectra : maxQuantSpectra.getUnidentifiedSpectra().values()) {
            numberOfUnidentifiedSpectra += unidentifiedSpectra.size();
        }
        Assert.assertEquals(18902, numberOfUnidentifiedSpectra);
    }
}
