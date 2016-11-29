package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.model.enums.ScoreType;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.preferences.IdentificationParameters;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.EnumMap;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class UtilitiesSearchSettingsMapperTest {

    private IdentificationParameters identificationParameters;
    private EnumMap<FastaDbType, FastaDb> fastaDbs = new EnumMap<>(FastaDbType.class);
    @Autowired
    private UtilitiesSearchSettingsMapper utilitiesSearchSettingsMapper;
    @Autowired
    private FastaDbService fastaDbService;

    @Before
    public void init() throws IOException {
        //create SearchParameters
        SearchParameters searchParameters = new SearchParameters();
        Enzyme enzyme = new Enzyme(1, "testEnzyme", "A", "A", "A", "A");
        searchParameters.setEnzyme(enzyme);

        searchParameters.setnMissedCleavages(2);

        searchParameters.setPrecursorAccuracyType(SearchParameters.MassAccuracyType.DA);
        searchParameters.setPrecursorAccuracy(0.5);
        Charge charge = new Charge(Charge.PLUS, 1);
        searchParameters.setMinChargeSearched(charge);
        searchParameters.setMaxChargeSearched(charge);

        searchParameters.setFragmentAccuracyType(SearchParameters.MassAccuracyType.DA);
        searchParameters.setFragmentIonAccuracy(0.5);

        searchParameters.setIonSearched1("a");
        searchParameters.setIonSearched2("b");

        //look for a FastaDB instance from the db
        fastaDbs.put(FastaDbType.PRIMARY, fastaDbService.findById(1L));

        //create IdentificationParameters
        identificationParameters = IdentificationParameters.getDefaultIdentificationParameters(searchParameters);
    }

    /**
     * Test the map method.
     *
     * @throws IOException thrown in case of an IO related problem.
     */
    @Test
    public void testMap() throws IOException {
        SearchAndValidationSettings searchAndValidationSettings = utilitiesSearchSettingsMapper.map(SearchEngineType.PEPTIDESHAKER, "0.28.0", fastaDbs, identificationParameters, ScoreType.FDR, 0.001, false);

        Assert.assertNotNull(searchAndValidationSettings);

        //search parameters
        Assert.assertNotNull(searchAndValidationSettings.getSearchParameters());

        //protein score type and threshold value
        Assert.assertEquals(ScoreType.FDR, searchAndValidationSettings.getSearchParameters().getScoreType());
        Assert.assertEquals(0.001, searchAndValidationSettings.getSearchParameters().getProteinThreshold(), 0.001);

        //search engine
        Assert.assertNotNull(searchAndValidationSettings.getSearchEngine());
        SearchEngine searchEngine = searchAndValidationSettings.getSearchEngine();
        Assert.assertEquals(SearchEngineType.PEPTIDESHAKER, searchEngine.getSearchEngineType());
        Assert.assertEquals("0.28.0", searchEngine.getVersion());

        //Fasta db
        Assert.assertNotNull(searchAndValidationSettings.getSearchSettingsHasFastaDbs().get(0));
    }
}
