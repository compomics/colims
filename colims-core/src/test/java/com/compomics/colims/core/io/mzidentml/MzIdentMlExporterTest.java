package com.compomics.colims.core.io.mzidentml;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.AnalyticalRunRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftware;
import uk.ac.ebi.jmzidml.model.mzidml.Cv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Iain
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@Transactional
@Rollback
public class MzIdentMlExporterTest {

    @Autowired
    private MzIdentMlExporter exporter;

    @Autowired
    private AnalyticalRunRepository repository;

    /**
     * Test the MZIdentML export of an analytical run.
     *
     * @throws IOException error thrown in case of a I/O related problem
     */
    @Test
    public void testExport() throws IOException {
        //get the run from the in memory database
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();
        AnalyticalRun run = repository.findById(1L);
        analyticalRuns.add(run);

//        File mzIdentMLFile = File.createTempFile("/home/niels/Desktop/testMzIdentMl", ".mzid");
        File mzIdentMLFile = new File("/home/niels/Desktop/testMzIdentMl.mzid");
//        File mgfFile = File.createTempFile("/home/niels/Desktop/testMgf", "mgf");
        File mgfFile = new File("/home/niels/Desktop/testMgf.mgf");

        exporter.export(mzIdentMLFile.toPath(), mgfFile.toPath(), new ClassPathResource("data").getFile().toPath(), analyticalRuns);
    }

    @Test
    public void testGetMzIdentMlElements() throws IOException {
        List<Cv> cvs = exporter.getChildMzIdentMlElements("/CvList", Cv.class);
        Assert.assertEquals(5, cvs.size());
    }

    @Test
    public void testGetMzIdentMlElement() throws IOException {
        AnalysisSoftware analysisSoftware = exporter.getMzIdentMlElement("/AnalysisSoftware/PeptideShaker", AnalysisSoftware.class);
        Assert.assertEquals("PeptideShaker", analysisSoftware.getName());
    }
}
