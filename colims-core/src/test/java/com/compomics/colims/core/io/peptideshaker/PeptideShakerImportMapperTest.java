package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.bean.PtmFactoryWrapper;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.compress.archivers.ArchiveException;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class PeptideShakerImportMapperTest {

    @Autowired
    private PeptideShakerImportMapper peptideShakerImportMapper;
    @Autowired
    private PeptideShakerIO peptideShakerIO;
    @Autowired
    private SampleService sampleService;
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private PtmFactoryWrapper ptmFactoryWrapper;
    @Autowired
    private AuthenticationBean authenticationBean;
    @Autowired
    private UserService userService;

    @Before
    public void setup() throws FileNotFoundException, IOException, XmlPullParserException {
        //load mods from test resources instead of user folder
        Resource utilitiesMods = new ClassPathResource("data/peptideshaker/searchGUI_mods.xml");
        ptmFactoryWrapper.getPtmFactory().clearFactory();
        ptmFactoryWrapper.getPtmFactory().importModifications(utilitiesMods.getFile(), false);

        //set admin user in authentication bean
        User adminUser = userService.findByName("admin1");
        authenticationBean.setCurrentUser(adminUser);
    }

    @Test
    public void testMap() throws IOException, ArchiveException, ClassNotFoundException, MappingException, SQLException {
        //import PeptideShaker .cps file
        UnpackedPsDataImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("data/peptideshaker/HeLa Example.cps").getFile());
        //set mgf files and fasta file
        List<File> mgfFiles = new ArrayList<>();
        mgfFiles.add(new ClassPathResource("data/peptideshaker/qExactive01819.mgf").getFile());
        unpackedPsDataImport.setMgfFiles(mgfFiles);
        
        File fastaFile = new ClassPathResource("data/peptideshaker/uniprot-human-reviewed-march-2014_concatenated_target_decoy.fasta").getFile();
        FastaDb fastaDb = new FastaDb();
        fastaDb.setName(fastaFile.getName());
        fastaDb.setFileName(fastaFile.getName());
        fastaDb.setFilePath(fastaFile.getAbsolutePath());
        unpackedPsDataImport.setFastaDb(fastaDb);

        //clear resources
        peptideShakerImportMapper.clear();
        
        List<AnalyticalRun> analyticalRuns = peptideShakerImportMapper.map(unpackedPsDataImport);

        //analytical run
        AnalyticalRun testAnalyticalRun = analyticalRuns.get(0);
        Assert.assertNotNull(testAnalyticalRun);
        Assert.assertNull(testAnalyticalRun.getSample());
        Assert.assertNotNull(testAnalyticalRun.getSpectrums());
        Assert.assertEquals(6538, testAnalyticalRun.getSpectrums().size());

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

//        //get sample from db
//        Sample sample = sampleService.findAll().get(0);
//        
//        //set sample and persist
//        for(AnalyticalRun analyticalRun : analyticalRuns){
//            analyticalRun.setSample(sample);
//            analyticalRunService.saveOrUpdate(analyticalRun);
//        }  
//        
//        //do it again
//        analyticalRuns = peptideShakerImportMapper.map(peptideShakerImport);
//        
//        for(AnalyticalRun analyticalRun : analyticalRuns){
//            analyticalRun.setSample(sample);
//            analyticalRunService.saveOrUpdate(analyticalRun);
//        }
    }
}
