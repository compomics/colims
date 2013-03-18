package com.compomics.colims.core.io;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.compomics.colims.core.exception.PeptideShakerIOException;
import com.compomics.colims.core.io.model.PeptideShakerImport;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.biology.Sample;

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
    public void testImportPeptideShakerCpsFile() throws IOException, PeptideShakerIOException {
        PeptideShakerImport peptideShakerImport = peptideShakerIO.importPeptideShakerCpsArchive(new ClassPathResource("test_peptideshaker_project.cps").getFile());

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
