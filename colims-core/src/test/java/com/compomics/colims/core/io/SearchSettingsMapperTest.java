package com.compomics.colims.core.io;

import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.IdentificationFile;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.massspectrometry.Charge;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class SearchSettingsMapperTest {

    private SearchParameters searchParameters;
    private FastaDb fastaDb;
    private List<File> identificationFiles;
    @Autowired
    private SearchSettingsMapper searchSettingsMapper;
    @Autowired
    private FastaDbService fastaDbService;

    @Before
    public void init() throws IOException {
        //create SearchParameters
        searchParameters = new SearchParameters();
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
        fastaDb = fastaDbService.findById(1L);

        //set stub IdentificationFile
        File identificationFile = new ClassPathResource("data/peptideshaker/colims_test_ps_file.cps").getFile();
        identificationFiles = new ArrayList<>();
        identificationFiles.add(identificationFile);
    }

    /**
     * Test the map method.
     *
     * @throws IOException thrown in case of an IO related problem.
     */
    @Test
    public void testMap() throws IOException {
        SearchAndValidationSettings searchAndValidationSettings = searchSettingsMapper.map(SearchEngineType.PEPTIDESHAKER, "0.28.0", fastaDb, searchParameters, identificationFiles, false);

        Assert.assertNotNull(searchAndValidationSettings);

        //identification files
        Assert.assertNotNull(searchAndValidationSettings.getIdentificationFiles());
        Assert.assertFalse(searchAndValidationSettings.getIdentificationFiles().isEmpty());
        Assert.assertEquals(1, searchAndValidationSettings.getIdentificationFiles().size());
        IdentificationFile mappedIdentificationFile = searchAndValidationSettings.getIdentificationFiles().get(0);
        Assert.assertEquals(identificationFiles.get(0).getName(), mappedIdentificationFile.getFileName());
        Assert.assertEquals(identificationFiles.get(0).getCanonicalPath(), mappedIdentificationFile.getFilePath());
        Assert.assertNull(mappedIdentificationFile.getBinaryFileType());
        Assert.assertNull(mappedIdentificationFile.getContent());
        Assert.assertNotNull(mappedIdentificationFile.getSearchAndValidationSettings());
        Assert.assertEquals(searchAndValidationSettings, mappedIdentificationFile.getSearchAndValidationSettings());

        //search parameters
        Assert.assertNotNull(searchAndValidationSettings.getSearchParameters());

        //search engine
        Assert.assertNotNull(searchAndValidationSettings.getSearchEngine());
        SearchEngine searchEngine = searchAndValidationSettings.getSearchEngine();
        Assert.assertEquals(SearchEngineType.PEPTIDESHAKER, searchEngine.getSearchEngineType());
        Assert.assertEquals("0.28.0", searchEngine.getVersion());

        //Fasta db
        Assert.assertNotNull(searchAndValidationSettings.getFastaDb());
    }
}
