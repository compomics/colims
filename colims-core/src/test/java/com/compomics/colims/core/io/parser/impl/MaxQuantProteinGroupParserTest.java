package com.compomics.colims.core.io.parser.impl;

import com.compomics.util.experiment.biology.Protein;
import java.io.File;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Davy
 */
public class MaxQuantProteinGroupParserTest {
    
    static File shortTestFile;
    
    public MaxQuantProteinGroupParserTest() {
        ClassLoader loader = getClass().getClassLoader();    
        shortTestFile = new File(loader.getResource("testdata/proteingroups_subset_10.tsv").getFile());
    }
    
    public static void setUpClass() {
    }

    /**
     * Test of parseMaxQuantProteinGroups method, of class MaxQuantProteinGroupParser.
     */
    @Test
    public void testParseMaxQuantProteinGroups() throws Exception {
        System.out.println("parseMaxQuantProteinGroups");
        File aProteinGroupsFile = shortTestFile;
        Map<Integer,Protein> result = MaxQuantProteinGroupParser.parseMaxQuantProteinGroups(aProteinGroupsFile);
        assertThat(result.keySet().size(),is(10));
        assertThat(result.get(1722).getAccession(),is("Q9Y105"));
        assertThat(result.get(1726).isDecoy(),is(false));
        assertThat(result.get(1730).isDecoy(),is(true));
        
    }
}