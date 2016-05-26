package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.model.Spectrum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantAndromedaParserTest {

    @Autowired
    private MaxQuantAndromedaParser maxQuantAndromedaParser;

    @Test
    public void testParse() throws Exception {

        maxQuantAndromedaParser.parseParameters(MaxQuantTestSuite.maxQuantAndromedaDirectory);
  //      Assert.assertEquals(1222, spectra.size());

        System.out.println("------------");
    }
}