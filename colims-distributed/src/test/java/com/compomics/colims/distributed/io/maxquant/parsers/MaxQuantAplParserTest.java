package com.compomics.colims.distributed.io.maxquant.parsers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.junit.Ignore;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantAplParserTest {

    @Autowired
    private MaxQuantAplParser maxQuantAplParser;

    @Ignore
    @Test
    public void testParse() throws Exception {
//        maxQuantAplParser.parse(MaxQuantTestSuite.maxQuantAndromedaDirectory);
//        System.out.println("");
    }
}