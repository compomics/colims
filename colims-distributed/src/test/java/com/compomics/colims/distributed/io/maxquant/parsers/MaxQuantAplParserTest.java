package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.enums.FragmentationType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantAplParserTest {

    @Autowired
    private MaxQuantAplParser maxQuantAplParser;

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

    @Test
    public void testParse() throws Exception {
        maxQuantAplParser.init(MaxQuantTestSuite.maxQuantAndromedaDirectory);
        Map<SpectrumKey, Spectrum> spectra = new HashMap<>();
        maxQuantAplParser.parse(spectra, false);
        System.out.println("");
    }
}