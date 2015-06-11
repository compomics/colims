package com.compomics.colims.core.io.mzidentml;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.AnalyticalRunRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftware;
import uk.ac.ebi.jmzidml.model.mzidml.Cv;
import uk.ac.ebi.jmzidml.model.mzidml.CvList;

import java.io.IOException;

/**
 * @author Iain
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class MzIdentMLExporterTest {

    @Autowired
    private MzIdentMLExporter exporter;

    @Autowired
    private AnalyticalRunRepository repository;

    /**
     * Test the MZIdentML export of an analytical run.
     *
     * @throws IOException error thrown in case of a I/O related problem
     */
    @Test
    public void testExport() throws IOException {
        AnalyticalRun run = repository.findById(1L);
        String export = exporter.export(run);
        System.out.println(export);
        Assert.assertFalse(export.isEmpty());
    }

    @Test
    public void testClassListMapping() throws IOException {
        CvList cvList = new CvList();
        cvList.getCv().addAll(exporter.getDataList("CvList", Cv.class));
        Assert.assertEquals(2, cvList.getCv().size());
    }

    @Test
    public void testClassItemMapping() throws IOException {
        AnalysisSoftware as = exporter.getDataItem("AnalysisSoftware.PeptideShaker", AnalysisSoftware.class);
        Assert.assertEquals(as.getName(), "PeptideShaker");
    }
}
