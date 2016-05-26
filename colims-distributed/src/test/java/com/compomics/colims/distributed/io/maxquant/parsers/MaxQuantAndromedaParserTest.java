package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.model.enums.FragmentationType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantAndromedaParserTest {

    @Autowired
    private MaxQuantAndromedaParser maxQuantAndromedaParser;

    /**
     * Test the {@link MaxQuantAndromedaParser#parseParameters(File)} method.
     *
     * @throws Exception in case something goes wrong
     */
    @Test
    public void testParseParameter() throws Exception {
        maxQuantAndromedaParser.parseParameters(MaxQuantTestSuite.maxQuantAndromedaDirectory);

        //check fragmentation and mass analyzer type
        FragmentationType fragmentationType = maxQuantAndromedaParser.getFragmentationType();
        MaxQuantConstants.Analyzer massAnalyzerType = maxQuantAndromedaParser.getMassAnalyzerType();
        Map<String, String> aplFiles = maxQuantAndromedaParser.getAplFiles();

        Assert.assertEquals(fragmentationType, FragmentationType.CID);
        Assert.assertEquals(massAnalyzerType, MaxQuantConstants.Analyzer.ITMS);
        Assert.assertEquals(2, aplFiles.size());
    }

    /**
     * Test the {@link MaxQuantAndromedaParser#parseSpectra(Map, boolean)} method.
     *
     * @throws Exception in case something goes wrong
     */
    @Test
    public void testParseSpectra() throws Exception {
        maxQuantAndromedaParser.parseParameters(MaxQuantTestSuite.maxQuantAndromedaDirectory);
        //      Assert.assertEquals(1222, spectra.size());

        System.out.println("------------");
    }
}