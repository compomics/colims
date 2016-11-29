package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.colims.model.enums.ScoreType;
import com.compomics.colims.model.enums.SearchEngineType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

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
        EnumMap<FastaDbType, List<FastaDb>> fastaDbs = new EnumMap<>(FastaDbType.class);
        fastaDbs.put(FastaDbType.PRIMARY, new ArrayList<>(Arrays.asList(MaxQuantTestSuite.testFastaDb)));

        maxQuantSearchSettingsParser.parse(MaxQuantTestSuite.maxQuantCombinedDirectory, MaxQuantTestSuite.mqparFile, fastaDbs);
        Map<String, SearchAndValidationSettings> runSettings = maxQuantSearchSettingsParser.getRunSettings();

        SearchAndValidationSettings searchAndValidationSettings = runSettings.get("20130607_FI_Ubiquitin_9");
        Assert.assertEquals(SearchEngineType.MAXQUANT, searchAndValidationSettings.getSearchEngine().getSearchEngineType());
        SearchParameters searchParameters = searchAndValidationSettings.getSearchParameters();
        Assert.assertEquals("Trypsin/P", searchParameters.getEnzymes());
        Assert.assertEquals(Integer.valueOf(3), searchParameters.getNumberOfMissedCleavages());
        Assert.assertEquals(4.5, searchParameters.getPrecMassTolerance(), 0.001);
        Assert.assertEquals(MassAccuracyType.PPM, searchParameters.getPrecMassToleranceUnit());
        Assert.assertEquals(20.0, searchParameters.getFragMassTolerance(), 0.001);
        Assert.assertEquals(MassAccuracyType.PPM, searchParameters.getFragMassToleranceUnit());
        Assert.assertEquals(Integer.valueOf(7), searchParameters.getUpperCharge());
        Assert.assertEquals(ScoreType.FDR, searchParameters.getScoreType());
        Assert.assertEquals(0.01, searchParameters.getPsmThreshold(), 0.001);
        Assert.assertEquals(0.01, searchParameters.getProteinThreshold(), 0.001);
        Assert.assertFalse(searchParameters.getSearchParametersHasModifications().isEmpty());
        Assert.assertEquals(3, searchParameters.getSearchParametersHasModifications().size());

        Long id = searchParameters.getId();
        //check if the ID of the other search parameters, has to be the same
        runSettings.entrySet().
                stream().
                filter(entry -> !entry.getKey().equals("20130607_FI_Ubiquitin_9")).
                forEach(entry -> Assert.assertEquals(id, entry.getValue().getSearchParameters().getId()));
    }

}
