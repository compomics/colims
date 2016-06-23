package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantSpectrumParameterHeaders;
import com.compomics.colims.model.enums.FragmentationType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantAndromedaParserTest {

    @Autowired
    private MaxQuantAndromedaParser maxQuantAndromedaParser;

    /**
     * Test the {@link MaxQuantAndromedaParser#parseParameters(Path)} method.
     *
     * @throws Exception in case something goes wrong
     */
    @Test
    public void testParseParameter() throws Exception {
        maxQuantAndromedaParser.parseParameters(MaxQuantTestSuite.maxQuantAndromedaDirectory);

        FragmentationType fragmentationType = maxQuantAndromedaParser.getFragmentationType();
        MaxQuantConstants.Analyzer massAnalyzerType = maxQuantAndromedaParser.getMassAnalyzerType();
        Map<Path, Path> aplFilePaths = maxQuantAndromedaParser.getAplFilePaths();

        //check fragmentation and mass analyzer type
        Assert.assertEquals(fragmentationType, FragmentationType.CID);
        Assert.assertEquals(massAnalyzerType, MaxQuantConstants.Analyzer.ITMS);
        //check apl files
        Assert.assertEquals(3, aplFilePaths.size());
        //check spectrum parameters
        EnumMap<MaxQuantSpectrumParameterHeaders, String> spectrumParameters = maxQuantAndromedaParser.getSpectrumParameters();
        Assert.assertNotNull(spectrumParameters);
        Assert.assertEquals(8, spectrumParameters.size());
    }

}