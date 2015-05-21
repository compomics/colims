package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.maxquant.MaxQuantTestSuite;
import com.compomics.util.experiment.identification.matches.ProteinMatch;

import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantProteinGroupParserTest {

    @Autowired
    private MaxQuantProteinGroupParser maxQuantProteinGroupParser;

    /**
     * Test of parseMaxQuantProteinGroups method, of class
     * MaxQuantProteinGroupParser.
     */
    @Ignore
    @Test
    public void testParse() throws Exception {
        // TODO: ignored due to utilities
        List<String> rawFile = Files.readAllLines(MaxQuantTestSuite.proteinGroupsFile.toPath());

        Map<Integer, ProteinMatch> result = maxQuantProteinGroupParser.parse(MaxQuantTestSuite.proteinGroupsFile);

        // minus headers
        assert(result.size() <= rawFile.size() - 1);

        ProteinMatch match = result.entrySet().iterator().next().getValue();

        assertFalse(match.getMainMatch().contains("CON"));
        assertFalse(match.getMainMatch().contains("REV"));
        // TODO: fails due to utilities issue
        assertThat(match.isDecoy(), is(false));
        assertThat(match.getPeptideMatchesKeys().size(), is(1));
    }
}