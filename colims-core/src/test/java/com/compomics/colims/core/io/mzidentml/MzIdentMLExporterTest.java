package com.compomics.colims.core.io.mzidentml;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.AnalyticalRunRepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
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
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;

/**
 * Created by Iain on 13/01/2015.
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

    @Test
    public void testExport() throws IOException {
        AnalyticalRun run = repository.findById(1L);
        System.out.println(exporter.export(run));
    }

    @Test
    public void testJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(new ClassPathResource("/config/mzidentml.json").getURL());

        JsonNode cvList = root.get("cvList");

        for (JsonNode node : cvList) {
            Map<String, List> data = mapper.readValue(node, Map.class);
            System.out.println("ok");
        }
    }

    @Test
    public void testClassListMapping() throws IOException {
        exporter.init();
        CvList cvList = new CvList();
        cvList.getCv().addAll(exporter.getDataList("CvList", Cv.class));
        Assert.assertEquals(4, cvList.getCv().size());
    }

    @Test
    public void testClassItemMapping() {
        exporter.init();
        AnalysisSoftware as = exporter.getDataItem("AnalysisSoftware.PeptideShaker", AnalysisSoftware.class);
        Assert.assertEquals("PeptideShaker", as.getName());
    }
}
