/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.parser.impl;

import com.compomics.colims.model.Quantification;
import com.compomics.colims.model.enums.QuantificationWeight;
import java.io.File;
import java.util.List;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kenneth
 */
public class MaxQuantQuantificationParserTest {

    private final File noQuantFile;
    private final File quantFile;

    public MaxQuantQuantificationParserTest() {
        ClassLoader loader = getClass().getClassLoader();
        noQuantFile = new File(loader.getResource("data/maxquant/evidence_subset_10.tsv").getFile());
        quantFile = new File(loader.getResource("data/maxquant/evidence_subset_quant10.tsv").getFile());
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of parseMaxQuantQuantification method, of class
     * MaxQuantQuantificationParser.
     */
    @Test
    public void testParseMaxQuantQuantificationGroups() throws Exception {
        System.out.println("parseMaxQuantQuantificationGroupsWithQuant");
        File aQuantificationFile = quantFile;
        Map<Integer, List<Quantification>> result = MaxQuantQuantificationParser.parseMaxQuantQuantification(aQuantificationFile);

        assertThat(result.keySet().size(), is(15));
        assertThat(result.get(3).get(0).getIntensity(), is(427370.0));
        assertThat(result.get(3).get(1).getIntensity(), is(373000.0));
        assertThat(result.get(11).get(0).getIntensity(), is(2169200.0));
        assertThat(result.get(11).get(1).getIntensity(), is(2294200.0));
        assertThat(result.get(11).get(1).getWeight(), is(QuantificationWeight.HEAVY));

    }

    @Test
    public void testParseMaxQuantQuantificationGroupsWithoutQuant() throws Exception {
        System.out.println("parseMaxQuantQuantificationGroupsWithoutQuant");
        File aQuantificationFile = noQuantFile;
        boolean exceptionThrown = true;
        try {
            Map<Integer, List<Quantification>> result = MaxQuantQuantificationParser.parseMaxQuantQuantification(aQuantificationFile);
            exceptionThrown = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertThat(exceptionThrown, is(false));
    }

}
