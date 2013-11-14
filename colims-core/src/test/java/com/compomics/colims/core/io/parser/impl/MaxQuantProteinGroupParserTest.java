package com.compomics.colims.core.io.parser.impl;

import com.compomics.util.experiment.identification.matches.ProteinMatch;
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

    File shortTestFile;

    public MaxQuantProteinGroupParserTest() {
        ClassLoader loader = getClass().getClassLoader();
        shortTestFile = new File(loader.getResource("testdata/proteingroups_subset_10.tsv").getFile());
    }

    /**
     * Test of parseMaxQuantProteinGroups method, of class
     * MaxQuantProteinGroupParser.
     */
    @Test
    public void testParseMaxQuantProteinGroups() throws Exception {
        System.out.println("parseMaxQuantProteinGroups");
        File aProteinGroupsFile = shortTestFile;
        Map<Integer, ProteinMatch> result = MaxQuantProteinGroupParser.parseMaxQuantProteinGroups(aProteinGroupsFile);
        assertThat(result.keySet().size(), is(10));
        assertThat(result.get(1722).getMainMatch(), is("Q9Y105"));
        assertThat(result.get(1722).getNProteins(), both(is(result.get(1722).getTheoreticProteinsAccessions().size())).and(is(1)));
        //assertThat(result.get(1726).isDecoy(), is(false));
        assertThat(result.get(1729).getNProteins(), is(15));
        assertThat(result.get(1729).getMainMatch(), is("REV__A1Z9J3"));
        assertThat(result.get(1724).getMainMatch(), is("Q9Y112"));
        //assertThat(result.get(1730).isDecoy(), is(true));

    }
}