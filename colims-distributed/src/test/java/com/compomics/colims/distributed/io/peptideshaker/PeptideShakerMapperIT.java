package com.compomics.colims.distributed.io.peptideshaker;

import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.*;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.FastaDbType;
import org.apache.commons.compress.archivers.ArchiveException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
@Transactional
@Rollback
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PeptideShakerMapperIT {

    @Autowired
    private PeptideShakerMapper peptideShakerMapper;
    @Autowired
    private PeptideShakerIO peptideShakerIO;
    @Autowired
    private SampleService sampleService;
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private UserBean userBean;
    @Autowired
    private UserService userService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private FastaDbService fastaDbService;

    @Before
    public void setup() throws IOException {
        //set admin user in authentication bean
        User adminUser = userService.findByName("admin");
        userService.fetchAuthenticationRelations(adminUser);
        userBean.setCurrentUser(adminUser);
    }

    /**
     * Test the importing of PeptideShaker data.
     *
     * @throws IOException
     * @throws ArchiveException
     * @throws ClassNotFoundException
     * @throws MappingException
     * @throws SQLException
     * @throws InterruptedException
     */
    @Test
    public void testImportData() throws IOException, ArchiveException, ClassNotFoundException, MappingException, SQLException, InterruptedException {
        //import PeptideShaker .cps file
        UnpackedPeptideShakerImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsxArchive(new ClassPathResource("data/peptideshaker/colims_test_ps_file.cpsx").getFile());
        //set mgf files and fasta file
        List<Path> mgfFiles = new ArrayList<>();
        mgfFiles.add(Paths.get("qExactive01819.mgf"));
        unpackedPsDataImport.setMgfFiles(mgfFiles);

        Path fastaFile = Paths.get("uniprot-human-reviewed-trypsin-august-2015_concatenated_target_decoy.fasta");
        FastaDb fastaDb = new FastaDb();
        fastaDb.setName(fastaFile.getFileName().toString());
        fastaDb.setFileName(fastaFile.getFileName().toString());
        fastaDb.setFilePath(fastaFile.toString());
        fastaDb.setVersion("test_version");
        fastaDb.setDatabaseName("UNIPROT");

        //save the fasta db. We don't have it as an insert statement in the import.sql file
        //as the file path might be different depending on the OS
        fastaDbService.persist(fastaDb);

        EnumMap<FastaDbType, List<Long>> fastaDbIds = new EnumMap<>(FastaDbType.class);
        fastaDbIds.put(FastaDbType.PRIMARY, new ArrayList<>(Arrays.asList(fastaDb.getId())));

        unpackedPsDataImport.setFastaDbIds(fastaDbIds);

        //clear resources
        peptideShakerMapper.clear();

        Path experimentsDirectory = new ClassPathResource("data/peptideshaker").getFile().toPath();
        Path fastasDirectory = new ClassPathResource("data/peptideshaker").getFile().toPath();
        MappedData mappedData = peptideShakerMapper.mapData(unpackedPsDataImport, experimentsDirectory.toAbsolutePath(), fastasDirectory.toAbsolutePath());

        //analytical run
        AnalyticalRun testAnalyticalRun = mappedData.getAnalyticalRuns().get(0);
        Assert.assertTrue(testAnalyticalRun.getStorageLocation().contains("colims_test_ps_file.cpsx"));
        Assert.assertNotNull(testAnalyticalRun);
        Assert.assertNull(testAnalyticalRun.getSample());
        Assert.assertNotNull(testAnalyticalRun.getSpectrums());
        Assert.assertEquals(5329, testAnalyticalRun.getSpectrums().size());

        //search and validation settings
        Assert.assertNotNull(testAnalyticalRun.getSearchAndValidationSettings());

        //spectra
        testAnalyticalRun.getSpectrums().stream().forEach(spectrum -> {
            Assert.assertNotNull(spectrum.getAnalyticalRun());
            if (!spectrum.getPeptides().isEmpty()) {
                spectrum.getPeptides().stream().forEach(peptide -> {
                    Assert.assertNotNull(peptide.getSpectrum());
                    Assert.assertFalse(peptide.getSequence().isEmpty());

                    //a peptide without a protein (group) makes no sense
                    Assert.assertFalse(peptide.getPeptideHasProteinGroups().isEmpty());

                    peptide.getPeptideHasProteinGroups().stream().forEach(peptideHasProteinGroup -> {
                        Assert.assertNotNull(peptideHasProteinGroup.getPeptide());
                        peptideHasProteinGroup.getProteinGroup().getProteinGroupHasProteins().stream().forEach(proteinGroupHasProtein -> {
                            Assert.assertNotNull(proteinGroupHasProtein.getProteinAccession());
                            Protein protein = proteinGroupHasProtein.getProtein();
                            Assert.assertNotNull(protein);
                            //       Assert.assertFalse(protein.getProteinAccessions().isEmpty());
                            //      Assert.assertFalse(protein.getProteinAccessions().get(0).getAccession().isEmpty());
                            Assert.assertFalse(protein.getSequence().isEmpty());
                        });
                    });
                    if (!peptide.getPeptideHasModifications().isEmpty()) {
                        peptide.getPeptideHasModifications().stream().forEach(peptideHasModification -> {
                            Assert.assertNotNull(peptideHasModification.getPeptide());
                            Modification modification = peptideHasModification.getModification();
                            Assert.assertNotNull(modification);
                            Assert.assertNotNull(modification.getPeptideHasModifications());
                            Assert.assertFalse(modification.getName().isEmpty());
                        });
                    }
                });
            }
        });

        //get sample from db
        Sample sample = sampleService.findAll().get(0);

        mappedData.getAnalyticalRuns().stream().forEach(analyticalRun -> {
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
        });
    }
}
