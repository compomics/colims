package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.model.FastaDb;
import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.compomics.util.experiment.MsExperiment;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class PeptideShakerIOTest {

    @Autowired
    private PeptideShakerIO peptideShakerIO;

    /**
     * Test the unpacking of a PS .cps file.
     *
     * @throws IOException
     * @throws ArchiveException
     * @throws ClassNotFoundException
     */
    @Test
    public void testUnpackPeptideShakerCpsFile() throws IOException, ArchiveException, ClassNotFoundException {
        UnpackedPeptideShakerImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("data/peptideshaker/HeLa Example.cps").getFile());

        Assert.assertNotNull(unpackedPsDataImport);
        
        File directory = unpackedPsDataImport.getUnpackedDirectory();
        Assert.assertNotNull(directory);
        Assert.assertTrue(directory.exists());

        File dbDirectory = unpackedPsDataImport.getDbDirectory();
        Assert.assertNotNull(dbDirectory);
        Assert.assertTrue(dbDirectory.exists());

        MsExperiment msExperiment = unpackedPsDataImport.getMsExperiment();
        Assert.assertNotNull(msExperiment);
        
        //delete directory
        FileUtils.deleteDirectory(directory);
        Assert.assertFalse(directory.exists());
    }

    /**
     * Test the unpacking of a PeptideShakerImport instance.
     *
     * @throws IOException
     * @throws ArchiveException
     * @throws ClassNotFoundException
     */
    @Test
    public void testUnpackPeptideShakerDataIdmport() throws IOException, ArchiveException, ClassNotFoundException {
        File peptideShakerCpsFile = new ClassPathResource("data/peptideshaker/HeLa Example.cps").getFile();
        File fastaFile = new ClassPathResource("data/peptideshaker/uniprot-human-reviewed-march-2014_concatenated_target_decoy.fasta").getFile();
        FastaDb fastaDb = new FastaDb();
        fastaDb.setName(fastaFile.getName());
        fastaDb.setFileName(fastaFile.getName());
        fastaDb.setFilePath(fastaFile.getAbsolutePath());

        List<File> mgfFiles = new ArrayList<>();
        mgfFiles.add(new ClassPathResource("data/peptideshaker/qExactive01819.mgf").getFile());

        PeptideShakerImport peptideShakerImport = new PeptideShakerImport(peptideShakerCpsFile, fastaDb, mgfFiles);
        UnpackedPeptideShakerImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerImport(peptideShakerImport);

        Assert.assertNotNull(unpackedPsDataImport);

        File directory = unpackedPsDataImport.getUnpackedDirectory();
        Assert.assertNotNull(directory);
        Assert.assertTrue(directory.exists());

        File dbDirectory = unpackedPsDataImport.getDbDirectory();
        Assert.assertNotNull(dbDirectory);
        Assert.assertTrue(dbDirectory.exists());

        MsExperiment msExperiment = unpackedPsDataImport.getMsExperiment();
        Assert.assertNotNull(msExperiment);

        Assert.assertEquals(fastaDb, unpackedPsDataImport.getFastaDb());
        Assert.assertEquals(mgfFiles, unpackedPsDataImport.getMgfFiles());

        //delete directory
        FileUtils.deleteDirectory(directory);
        Assert.assertFalse(directory.exists());
    }
}
