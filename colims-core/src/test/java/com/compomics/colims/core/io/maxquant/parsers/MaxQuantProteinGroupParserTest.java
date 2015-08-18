package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.maxquant.MaxQuantTestSuite;

import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.ProteinGroup;
import org.junit.Test;

import static org.hamcrest.Matchers.lessThan;
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
    @Autowired
    private MaxQuantParser maxQuantParser;

    /**
     * Test of parseMaxQuantProteinGroups method, of class
     * MaxQuantProteinGroupParser.
     */
    @Test
    public void testParse() throws Exception {
        FastaDb maxQuantTestFastaDb = new FastaDb();
        maxQuantTestFastaDb.setName(MaxQuantTestSuite.fastaFile.getName());
        maxQuantTestFastaDb.setFileName(MaxQuantTestSuite.fastaFile.getName());
        maxQuantTestFastaDb.setFilePath(MaxQuantTestSuite.fastaFile.getAbsolutePath());

        List<String> rawFile = Files.readAllLines(MaxQuantTestSuite.proteinGroupsFile.toPath());

        Map<Integer, ProteinGroup> result = maxQuantProteinGroupParser.parse(MaxQuantTestSuite.proteinGroupsFile, maxQuantParser.parseFasta(maxQuantTestFastaDb));

        // minus headers
        assertThat(result.size(), lessThan(rawFile.size()));

        ProteinGroup proteinGroup = result.entrySet().iterator().next().getValue();

        assertFalse(proteinGroup.getMainProtein().getSequence().contains("CON"));
        assertFalse(proteinGroup.getMainProtein().getSequence().contains("REV"));
        assertThat(proteinGroup.getPeptideHasProteinGroups().size(), is(0));

        // TODO: more relevant tests
    }
}