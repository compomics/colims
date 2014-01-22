package com.compomics.colims.core.io.parser.impl;

import com.compomics.util.experiment.identification.matches.ProteinMatch;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantProteinGroupParserTest {

    private File shortTestFile;
    @Autowired
    private MaxQuantProteinGroupParser maxQuantProteinGroupParser;

    public MaxQuantProteinGroupParserTest() throws IOException {        
        shortTestFile = new ClassPathResource("data/maxquant/proteinGroups_subset.tsv").getFile();
    }

    /**
     * Test of parseMaxQuantProteinGroups method, of class
     * MaxQuantProteinGroupParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parseMaxQuantProteinGroups");
        File aProteinGroupsFile = shortTestFile;
        Map<Integer, ProteinMatch> result = maxQuantProteinGroupParser.parse(aProteinGroupsFile);
        assertThat(result.keySet().size(), is(1760));
        assertThat(result.get(1722).getMainMatch(), is("Q9Y105"));
        assertThat(result.get(1722).getNProteins(), both(is(result.get(1722).getTheoreticProteinsAccessions().size())).and(is(1)));
        //assertThat(result.get(1726).isDecoy(), is(false));
        assertThat(result.get(1729).getNProteins(), is(15));
        assertThat(result.get(1729).getMainMatch(), is("REV__A1Z9J3"));
        assertThat(result.get(1724).getMainMatch(), is("Q9Y112"));
        //assertThat(result.get(1730).isDecoy(), is(true));

    }
}