package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantSpectrumParameterHeaders;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.enums.FastaDbType;
import org.jdom2.JDOMException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Davy & Iain
 * @author niels
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantSearchSettingsParserTest {

    @Autowired
    MaxQuantSearchSettingsParser maxQuantSearchSettingsParser;

    @Test
    public void testParse() throws Exception {
        EnumMap<FastaDbType, FastaDb> fastaDbs = new EnumMap<>(FastaDbType.class);
        fastaDbs.put(FastaDbType.PRIMARY, MaxQuantTestSuite.testFastaDb);

        maxQuantSearchSettingsParser.parse(MaxQuantTestSuite.maxQuantDirectory, fastaDbs, false);
        Map<String, SearchAndValidationSettings> result = maxQuantSearchSettingsParser.getRunSettings();

        // insane way to get the single entry from the map
        SearchParameters testProfile = result.entrySet().iterator().next().getValue().getSearchParameters();
        assertThat(testProfile.getEnzyme().getName(), is("Trypsin/P"));
        assertThat(testProfile.getSearchParametersHasModifications().size(), is(3));
    }

    @Ignore
    @Test
    public void testParseParameters() throws Exception {
//        Set<String> uniqueLines = new HashSet<>();
//
//        uniqueLines.addAll(Files.readAllLines(MaxQuantTestSuite.parameterFile.toPath()));
//
//        Map<String, String> parameters = maxQuantParameterParser.parseParameters(MaxQuantTestSuite.parameterFile);
//
//        assertThat(parameters.size(), is(uniqueLines.size()));
//        assertThat(parameters.get("user name"), is("compomics"));
    }
    @Test
    public void testparseSpectrumParameters() throws JDOMException {

        maxQuantSearchSettingsParser.parseSpectrumParameters(MaxQuantTestSuite.parameterDirectory);

        assertThat(maxQuantSearchSettingsParser.getSpectrumParamsWithRawFile().get("V20239_3911_Eik_green_10").
                get(MaxQuantSpectrumParameterHeaders.VARIABLE_MODIFICATIONS), is("Acetyl (Protein N-term),Oxidation (M)"));
        assertThat(maxQuantSearchSettingsParser.getSpectrumParamsWithRawFile().get("V20239_3911_Eik_green_10").
                get(MaxQuantSpectrumParameterHeaders.MAX_CHARGE), is("7"));

    }

}
