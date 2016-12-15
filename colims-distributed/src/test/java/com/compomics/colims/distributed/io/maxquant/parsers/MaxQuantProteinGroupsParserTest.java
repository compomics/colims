package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.ProteinGroup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantProteinGroupsParserTest {

    private Map<FastaDb, Path> fastaDbs = new HashMap<>();
    @Autowired
    private MaxQuantProteinGroupsParser maxQuantProteinGroupsParser;

    public MaxQuantProteinGroupsParserTest() {
        fastaDbs.put(MaxQuantTestSuite.testFastaDb, MaxQuantTestSuite.testFastaDbPath);
        fastaDbs.put(MaxQuantTestSuite.contaminantsFastaDb, MaxQuantTestSuite.contaminantsFastaDbPath);
    }

    /**
     * Test of parse method of class MaxQuantProteinGroupParser.
     *
     * @throws java.lang.Exception in case of an exception
     */
    @Test
    public void testParse() throws Exception {
        maxQuantProteinGroupsParser.parse(MaxQuantTestSuite.proteinGroupsFile,
                fastaDbs, true, new ArrayList<>());

        Map<Integer, ProteinGroup> result = maxQuantProteinGroupsParser.getProteinGroups();

        //number of entries in the proteinGroups.txt file - number of reverse proteins
        Assert.assertEquals(672, result.size());
    }

    /**
     * Test of parse method of class MaxQuantProteinGroupParser.
     *
     * @throws java.lang.Exception in case of an exception
     */
    @Test
    public void testParseWithoutContaminants() throws Exception {
        maxQuantProteinGroupsParser.parse(MaxQuantTestSuite.proteinGroupsFile,
                fastaDbs, false, new ArrayList<>());

        Map<Integer, ProteinGroup> result = maxQuantProteinGroupsParser.getProteinGroups();

        //number of entries in the proteinGroups.txt file - number of reverse proteins
        Assert.assertEquals(661, result.size());
    }
}