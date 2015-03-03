package com.compomics.colims.core.io.maxquant;

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
        shortTestFile = new ClassPathResource("data/maxquant_1512/proteinGroups.txt").getFile();
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
        assertThat(result.keySet().size(), is(131));
        assertThat(result.get(130).getMainMatch(), is("Q9JJ28"));
        assertThat(result.get(130).getNProteins(), both(is(result.get(128).getTheoreticProteinsAccessions().size())).and(is(1)));
        assertThat(result.get(130).getNProteins(), is(1));
    }
}