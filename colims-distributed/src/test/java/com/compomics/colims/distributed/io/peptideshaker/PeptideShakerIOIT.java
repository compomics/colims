package com.compomics.colims.distributed.io.peptideshaker;

import com.compomics.colims.core.io.PeptideShakerImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.util.experiment.MsExperiment;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class PeptideShakerIOIT {

    @Autowired
    private FastaDbService fastaDbService;
    @Autowired
    private PeptideShakerIO peptideShakerIO;

    /**
     * Test the unpacking of a PS .cps file.
     *
     * @throws IOException thrown in case of an IO related problem
     * @throws ArchiveException thrown in case of an archive related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a
     * class by it's string name.
     * @throws java.sql.SQLException thrown in case of a database access error
     * @throws InterruptedException thrown in case a thread is interrupted
     */
    @Test
    public void testUnpackPeptideShakerCpsFile() throws IOException, ArchiveException, ClassNotFoundException, SQLException, InterruptedException {
        UnpackedPeptideShakerImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("data/peptideshaker/colims_test_ps_file.cpsx").getFile());

        Assert.assertNotNull(unpackedPsDataImport);

        File directory = unpackedPsDataImport.getUnpackedDirectory();
        Assert.assertNotNull(directory);
        Assert.assertTrue(directory.exists());

        MsExperiment msExperiment = unpackedPsDataImport.getCpsParent().getExperiment();
        Assert.assertNotNull(msExperiment);

        //delete directory
        FileUtils.deleteDirectory(directory);
        Assert.assertFalse(directory.exists());
    }

    /**
     * Test the unpacking of a PeptideShakerImport instance.
     *
     * @throws IOException thrown in case of an IO related problem
     * @throws ArchiveException thrown in case of an archive related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a
     * class by it's string name.
     * @throws java.sql.SQLException thrown in case of a database access error
     * @throws InterruptedException thrown in case a thread is interrupted
     */
    @Test
    public void testUnpackPeptideShakerDataImport() throws IOException, ArchiveException, ClassNotFoundException, SQLException, InterruptedException {
        File peptideShakerCpsFile = new ClassPathResource("data/peptideshaker/colims_test_ps_file.cpsx").getFile();

        File fastaFile = new ClassPathResource("data/peptideshaker/uniprot-human-reviewed-trypsin-august-2015_concatenated_target_decoy.fasta").getFile();
        FastaDb fastaDb = new FastaDb();
        fastaDb.setName(fastaFile.getName());
        fastaDb.setFileName(fastaFile.getName());
        fastaDb.setFilePath(fastaFile.getAbsolutePath());

        //save the fasta db. We don't have it as an insert statement in the import.sql file
        //as the file path might be different depending on the OS
        fastaDbService.persist(fastaDb);

        EnumMap<FastaDbType, Long> fastaDbIds = new EnumMap<>(FastaDbType.class);
        fastaDbIds.put(FastaDbType.PRIMARY, fastaDb.getId());

        List<File> mgfFiles = new ArrayList<>();
        mgfFiles.add(new ClassPathResource("data/peptideshaker/qExactive01819.mgf").getFile());

        PeptideShakerImport peptideShakerImport = new PeptideShakerImport(peptideShakerCpsFile, fastaDbIds, mgfFiles);
        UnpackedPeptideShakerImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerImport(peptideShakerImport);

        Assert.assertNotNull(unpackedPsDataImport);

        File directory = unpackedPsDataImport.getUnpackedDirectory();
        Assert.assertNotNull(directory);
        Assert.assertTrue(directory.exists());

        MsExperiment msExperiment = unpackedPsDataImport.getCpsParent().getExperiment();
        Assert.assertNotNull(msExperiment);

        Assert.assertEquals(fastaDb.getId(), unpackedPsDataImport.getFastaDbIds().get(FastaDbType.PRIMARY));
        Assert.assertEquals(mgfFiles, unpackedPsDataImport.getMgfFiles());

        //delete directory
        FileUtils.deleteDirectory(directory);
        Assert.assertFalse(directory.exists());
    }
}
