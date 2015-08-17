package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.maxquant.MaxQuantTestSuite;

import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.SearchParameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Davy & Iain
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantParameterParserTest {

    @Autowired
    MaxQuantParameterParser maxQuantParameterParser;

    @Test
    public void testParse() throws Exception {
        FastaDb fastaDb = new FastaDb();
        fastaDb.setFilePath(MaxQuantTestSuite.fastaFile.getCanonicalPath());
        fastaDb.setName("test fasta");

        maxQuantParameterParser.parse(MaxQuantTestSuite.maxQuantTextFolder, fastaDb, false);
        Map<String, SearchAndValidationSettings> result = maxQuantParameterParser.getRunSettings();

        // insane way to get the single entry from the map
        SearchParameters testProfile =  result.entrySet().iterator().next().getValue().getSearchParameters();
        assertThat(testProfile.getEnzyme().getName(), is("Trypsin/P"));
        //assertThat(testProfile.getFixedModifications(), is(empty()));
        //assertThat(testProfile.getVariableModifications().size(), is(2));
    }

    @Test
    public void testParseParameters() throws Exception {
        Set<String> uniqueLines = new HashSet<>();

        uniqueLines.addAll(Files.readAllLines(MaxQuantTestSuite.parameterFile.toPath()));

        Map<String, String> parameters = maxQuantParameterParser.parseParameters(MaxQuantTestSuite.parameterFile);

        assertThat(parameters.size(), is(uniqueLines.size()));
        assertThat(parameters.get("user name"), is("compomics"));
    }

//    @Test
//    public void testParseExperiment() throws Exception {
//        SearchParameters searchParameters = maxQuantParameterParser.parseExperiment(MaxQuantTestSuite.maxQuantTextFolder);
//        Map<String, String> fileParameters = maxQuantParameterParser.parseParameters(MaxQuantTestSuite.parameterFile);
//
//        assertThat(fileParameters.get("ms/ms tol. (ftms)"), containsString(searchParameters.getPrecursorAccuracyType().toString().toLowerCase()));
//        assertThat(FilenameUtils.separatorsToSystem(searchParameters.getFastaFile().getPath()), is(FilenameUtils.separatorsToSystem(fileParameters.get("fasta file"))));
//    }
}