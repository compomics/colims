package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantSpectraParserTest {

    @Autowired
    private MaxQuantSpectraParser maxQuantSpectraParser;

    @Test
    public void testParse() throws Exception {
        maxQuantSpectraParser.parse(MaxQuantTestSuite.maxQuantCombinedDirectory, false);

        MaxQuantSpectra maxQuantSpectra = maxQuantSpectraParser.getMaxQuantSpectra();

        Assert.assertEquals(42, maxQuantSpectra.getSpectrumIDs().size());
        Assert.assertTrue(maxQuantSpectra.getUnidentifiedSpectra().isEmpty());

        // test for the unidentified spectra
        maxQuantSpectraParser.parse(MaxQuantTestSuite.maxQuantDirectory, true);

        MaxQuantSpectra maxQuantSpectra2 = maxQuantSpectraParser.getMaxQuantSpectra();

        Assert.assertEquals(42, maxQuantSpectra2.getSpectrumIDs().size());
        Assert.assertEquals(18902, maxQuantSpectra2.getUnidentifiedSpectra().size());

    }
}