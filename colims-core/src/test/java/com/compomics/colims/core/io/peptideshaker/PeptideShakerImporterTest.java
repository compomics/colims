package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.AuthenticationBean;
import com.compomics.util.experiment.biology.PTMFactory;
import org.apache.commons.compress.archivers.ArchiveException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class PeptideShakerImporterTest {

    @Autowired
    private PeptideShakerImporter peptideShakerImporter;
    @Autowired
    private PeptideShakerIO peptideShakerIO;
    @Autowired
    private SampleService sampleService;
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private AuthenticationBean authenticationBean;
    @Autowired
    private UserService userService;
    @Autowired
    private InstrumentService instrumentService;

    @Before
    public void setup() throws IOException, XmlPullParserException {
        //load mods from test resources instead of user folder
        Resource utilitiesMods = new ClassPathResource("data/peptideshaker/searchGUI_mods.xml");
        PTMFactory.getInstance().clearFactory();
        PTMFactory.getInstance().importModifications(utilitiesMods.getFile(), false);

        //set admin user in authentication bean
        User adminUser = userService.findByName("admin1");
        authenticationBean.setCurrentUser(adminUser);
    }

    @Test
    public void testMap() throws IOException, ArchiveException, ClassNotFoundException, MappingException, SQLException, InterruptedException {
        //import PeptideShaker .cps file
        UnpackedPeptideShakerImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("data/peptideshaker/colims_test_ps_file.cps").getFile());
        //set mgf files and fasta file
        List<File> mgfFiles = new ArrayList<>();
        mgfFiles.add(new ClassPathResource("data/peptideshaker/qExactive01819.mgf").getFile());
        unpackedPsDataImport.setMgfFiles(mgfFiles);

        File fastaFile = new ClassPathResource("data/peptideshaker/uniprot-human-reviewed-trypsin-october-2014_concatenated_target_decoy.fasta").getFile();
        FastaDb fastaDb = new FastaDb();
        fastaDb.setName(fastaFile.getName());
        fastaDb.setFileName(fastaFile.getName());
        fastaDb.setFilePath(fastaFile.getAbsolutePath());
        unpackedPsDataImport.setFastaDb(fastaDb);

        //clear resources
        peptideShakerImporter.clear();

        List<AnalyticalRun> analyticalRuns = peptideShakerImporter.importData(unpackedPsDataImport);

        //analytical run
        AnalyticalRun testAnalyticalRun = analyticalRuns.get(0);
        Assert.assertNotNull(testAnalyticalRun);
        Assert.assertNull(testAnalyticalRun.getSample());
        Assert.assertNotNull(testAnalyticalRun.getSpectrums());
        Assert.assertEquals(6561, testAnalyticalRun.getSpectrums().size());

        //search and validation settings
        Assert.assertNotNull(testAnalyticalRun.getSearchAndValidationSettings());

        //spectra
        for (Spectrum spectrum : testAnalyticalRun.getSpectrums()) {
            Assert.assertNotNull(spectrum.getAnalyticalRun());
            if (!spectrum.getPeptides().isEmpty()) {
                for (Peptide peptide : spectrum.getPeptides()) {
                    Assert.assertNotNull(peptide.getSpectrum());
                    Assert.assertFalse(peptide.getSequence().isEmpty());
                    if (!peptide.getPeptideHasProteins().isEmpty()) {
                        for (PeptideHasProtein peptideHasProtein : peptide.getPeptideHasProteins()) {
                            Assert.assertNotNull(peptideHasProtein.getPeptide());
                            Protein protein = peptideHasProtein.getProtein();
                            Assert.assertNotNull(protein);
                            Assert.assertFalse(protein.getProteinAccessions().isEmpty());
                            Assert.assertFalse(protein.getProteinAccessions().get(0).getAccession().isEmpty());
                            Assert.assertFalse(protein.getSequence().isEmpty());
                        }
                    }
                    if (!peptide.getPeptideHasModifications().isEmpty()) {
                        for (PeptideHasModification peptideHasModification : peptide.getPeptideHasModifications()) {
                            Assert.assertNotNull(peptideHasModification.getPeptide());
                            Modification modification = peptideHasModification.getModification();
                            Assert.assertNotNull(modification);
                            Assert.assertNotNull(modification.getPeptideHasModifications());
                            Assert.assertFalse(modification.getName().isEmpty());
                        }
                    }
                }
            }
        }

        //get sample from db
        Sample sample = sampleService.findAll().get(0);

        for (AnalyticalRun analyticalRun : analyticalRuns) {
            Date auditDate = new Date();

            SearchAndValidationSettings searchAndValidationSettings = analyticalRun.getSearchAndValidationSettings();
            searchAndValidationSettings.setCreationDate(auditDate);
            searchAndValidationSettings.setModificationDate(auditDate);
            searchAndValidationSettings.setUserName("test");

            analyticalRun.setCreationDate(auditDate);
            analyticalRun.setModificationDate(auditDate);
            analyticalRun.setUserName("testing");
            analyticalRun.setStartDate(auditDate);
            analyticalRun.setSample(sample);
            analyticalRun.setInstrument(instrumentService.findAll().get(0));

            analyticalRunService.saveOrUpdate(analyticalRun);
        }

//        //do it again
//        analyticalRuns = peptideShakerImporter.importInputAndResults(searchAndValidationSettings, null);
//
//        for (AnalyticalRun analyticalRun : analyticalRuns) {
//            analyticalRun.setCreationDate(new Date());
//            analyticalRun.setModificationDate(new Date());
//            analyticalRun.setUserName("testing");
//            analyticalRun.setStartDate(new Date());
//            analyticalRun.setSample(sample);
//            analyticalRun.setInstrument(instrumentService.findAll().get(0));
//            analyticalRunService.saveOrUpdate(analyticalRun);
//        }
    }
}
