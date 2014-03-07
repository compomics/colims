package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.peptideshaker.UnpackedPeptideShakerDataImport;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
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

    @Test
    public void testUnpackPeptideShakerCpsFile() throws IOException, ArchiveException, ClassNotFoundException {
        UnpackedPeptideShakerDataImport peptideShakerImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("data/peptideshaker/test_peptideshaker_project.cps").getFile());

        File dbDirectory = peptideShakerImport.getDbDirectory();
        Assert.assertNotNull(dbDirectory);
        Assert.assertTrue(dbDirectory.exists());

        MsExperiment msExperiment = peptideShakerImport.getMsExperiment();
        Assert.assertNotNull(msExperiment);
        Assert.assertNotNull(msExperiment.getSamples());
        Sample sample = msExperiment.getSample(0);
        Assert.assertNotNull(sample);
        Assert.assertNotNull(msExperiment.getAnalysisSet(sample));
    }
    
}
