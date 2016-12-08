package com.compomics.colims.core.io.mzidentml;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.AnalyticalRunRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftware;
import uk.ac.ebi.jmzidml.model.mzidml.Cv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
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
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();
        AnalyticalRun run = repository.findById(1L);
        analyticalRuns.add(run);

        try (StringWriter writer = new StringWriter()) {
            exporter.export(writer, analyticalRuns);

            String export = writer.toString();
//            System.out.println(export);

            Assert.assertFalse(export.isEmpty());
        }

//        File testExportFile = new File("/home/niels/Desktop/testMzIdentMl.mzid");
//        try (
//                BufferedWriter bufferedWriter = Files.newBufferedWriter(testExportFile.toPath())
//        ) {
//            exporter.export(bufferedWriter, analyticalRuns);
//        }
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
