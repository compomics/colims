package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.ProteinGroup;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantProteinGroupParserTest {

    @Autowired
    private MaxQuantProteinGroupParser maxQuantProteinGroupParser;
    @Autowired
    private MaxQuantParser maxQuantParser;

    /**
     * Test of parseMaxQuantProteinGroups method, of class MaxQuantProteinGroupParser.
     * @throws java.lang.Exception in case of an exception
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
        assertThat(result.size(), Matchers.lessThan(rawFile.size()));

        ProteinGroup proteinGroup = result.entrySet().iterator().next().getValue();

        assertFalse(proteinGroup.getMainProtein().getSequence().contains("CON"));
        assertFalse(proteinGroup.getMainProtein().getSequence().contains("REV"));
        assertThat(proteinGroup.getPeptideHasProteinGroups().size(), is(0));

        // TODO: more relevant tests
    }
}