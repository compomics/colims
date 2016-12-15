package com.compomics.colims.distributed.io.peptideshaker;

import com.compomics.colims.core.io.PeptideShakerImport;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-simple-test-context.xml"})
public class PeptideShakerIOIT {

    @Autowired
    private PeptideShakerIO peptideShakerIO;

    /**
     * Test the unpacking of a PS .cps file.
     *
     * @throws IOException            thrown in case of an IO related problem
     * @throws ArchiveException       thrown in case of an archive related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a class by it's string name.
     * @throws java.sql.SQLException  thrown in case of a database access error
     * @throws InterruptedException   thrown in case a thread is interrupted
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
     * @throws IOException            thrown in case of an IO related problem
     * @throws ArchiveException       thrown in case of an archive related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a class by it's string name.
     * @throws java.sql.SQLException  thrown in case of a database access error
     * @throws InterruptedException   thrown in case a thread is interrupted
     */
    @Test
    public void testUnpackPeptideShakerDataImport() throws IOException, ArchiveException, ClassNotFoundException, SQLException, InterruptedException {
        Path peptideShakerCpsFile = Paths.get("colims_test_ps_file.cpsx");

        EnumMap<FastaDbType, List<Long>> fastaDbIds = new EnumMap<>(FastaDbType.class);
        fastaDbIds.put(FastaDbType.PRIMARY, new ArrayList<>(Arrays.asList(5L)));

        List<Path> mgfFiles = new ArrayList<>();
        mgfFiles.add(Paths.get("qExactive01819.mgf"));

        Path experimentsDirectory = new ClassPathResource("data/peptideshaker").getFile().toPath();
        PeptideShakerImport peptideShakerImport = new PeptideShakerImport(peptideShakerCpsFile, fastaDbIds, mgfFiles);
        UnpackedPeptideShakerImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerImport(peptideShakerImport, experimentsDirectory);

        Assert.assertNotNull(unpackedPsDataImport);

        File directory = unpackedPsDataImport.getUnpackedDirectory();
        Assert.assertNotNull(directory);
        Assert.assertTrue(directory.exists());

        MsExperiment msExperiment = unpackedPsDataImport.getCpsParent().getExperiment();
        Assert.assertNotNull(msExperiment);

        Assert.assertEquals(5L, unpackedPsDataImport.getFastaDbIds().get(FastaDbType.PRIMARY).get(0).longValue());
        Assert.assertEquals(mgfFiles.get(0).getFileName(), unpackedPsDataImport.getMgfFiles().get(0).getFileName());

        //delete directory
        FileUtils.deleteDirectory(directory);
        Assert.assertFalse(directory.exists());
    }
}
