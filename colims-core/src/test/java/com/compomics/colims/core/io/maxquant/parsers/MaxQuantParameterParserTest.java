package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.maxquant.MaxQuantTestSuite;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.preferences.ModificationProfile;

import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
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

    /**
     * Test of parse method, of class MaxQuantParameterParser.
     * @throws java.lang.Exception
     */
//    @Test
//    public void testParse() throws Exception {
//        maxQuantParameterParser.parse(MaxQuantTestSuite.maxQuantTextFolder);
//        Map<String, SearchParameters> result = maxQuantParameterParser.getRunParameters();
//
//        // insane way to get the single entry from the map
//        ModificationProfile testProfile =  result.entrySet().iterator().next().getValue().getModificationProfile();
//        assertThat(testProfile.getAllModifications(), is(not(empty())));
//        assertThat(testProfile.getFixedModifications(), is(empty()));
//        assertThat(testProfile.getVariableModifications().size(), is(2));
//    }
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