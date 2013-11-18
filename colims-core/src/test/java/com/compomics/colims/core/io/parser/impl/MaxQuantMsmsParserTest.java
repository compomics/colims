package com.compomics.colims.core.io.parser.impl;

import com.compomics.util.experiment.massspectrometry.Spectrum;
import java.io.File;
import java.util.Map;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class MaxQuantMsmsParserTest {

    File msmsFile;
    
    public MaxQuantMsmsParserTest(){
        msmsFile = new File(getClass().getClassLoader().getResource("testdata/msms_subset_1000.tsv").getPath());
        
    }

    /**
     * Test of parse method, of class MaxQuantMsmsParser.
     */
    @Test
    public void testParseWithoutPeaklist() throws Exception {
        System.out.println("parseWithoutPeaklist");
        boolean addPeakList = false;
        MaxQuantMsmsParser instance = new MaxQuantMsmsParser();
        Map<Integer,Spectrum> result = instance.parse(msmsFile, addPeakList);
        assertThat(result.keySet().size(),is(999));
    }
    
    @Test
    public void testParseWithPeaklist() throws Exception{
        System.out.println("parseWithPeaklist");
        boolean addPeakList = true;
        MaxQuantMsmsParser instance = new MaxQuantMsmsParser();
        Map<Integer,Spectrum> result = instance.parse(msmsFile, addPeakList);
        assertThat(result.keySet().size(),is(999));
        
    }
}
