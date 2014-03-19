package com.compomics.colims.core.io.peptideshaker;

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
import com.compomics.util.experiment.biology.Sample;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.compress.archivers.ArchiveException;

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
        UnpackedPsDataImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("data/peptideshaker/test_peptideshaker_project.cps").getFile());

        Assert.assertNotNull(unpackedPsDataImport);

        File dbDirectory = unpackedPsDataImport.getDbDirectory();
        Assert.assertNotNull(dbDirectory);
        Assert.assertTrue(dbDirectory.exists());

        MsExperiment msExperiment = unpackedPsDataImport.getMsExperiment();
        Assert.assertNotNull(msExperiment);
    }

    /**
     * Test the unpacking of a PeptideShakerDataImport instance.
     *
     * @throws IOException
     * @throws ArchiveException
     * @throws ClassNotFoundException
     */
    @Test
    public void testUnpackPeptideShakerDataIdmport() throws IOException, ArchiveException, ClassNotFoundException {
        File peptideShakerCpsFile = new ClassPathResource("data/peptideshaker/test_peptideshaker_project.cps").getFile();
        File fastaFile = new ClassPathResource("data/peptideshaker/uniprot_sprot_101104_human_concat.fasta").getFile();
        List<File> mgfFiles = new ArrayList<>();
        mgfFiles.add(new ClassPathResource("data/peptideshaker/input_spectra.mgf").getFile());

        PeptideShakerDataImport peptideShakerDataImport = new PeptideShakerDataImport(peptideShakerCpsFile, fastaFile, mgfFiles);
        UnpackedPsDataImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerDataImport(peptideShakerDataImport);

        Assert.assertNotNull(unpackedPsDataImport);

        File dbDirectory = unpackedPsDataImport.getDbDirectory();
        Assert.assertNotNull(dbDirectory);
        Assert.assertTrue(dbDirectory.exists());

        MsExperiment msExperiment = unpackedPsDataImport.getMsExperiment();
        Assert.assertNotNull(msExperiment);

        Assert.assertEquals(fastaFile, unpackedPsDataImport.getFastaFile());
        Assert.assertEquals(mgfFiles, unpackedPsDataImport.getMgfFiles());
    }
}
