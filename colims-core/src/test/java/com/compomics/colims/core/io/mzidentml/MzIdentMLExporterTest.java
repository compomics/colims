package com.compomics.colims.core.io.mzidentml;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.AnalyticalRunRepository;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    public void testExport() throws IOException {
        AnalyticalRun run = repository.findById(1L);
        System.out.println(exporter.export(run));
    }

    @Test
    public void testJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(this.getClass().getResource("/config/mzidentml.json"));

        JsonNode cvList = root.get("cvList");

        for (JsonNode node : cvList) {
            Map<String, List> data = mapper.readValue(node, Map.class);
            System.out.println("ok");
        }
    }

    @Test
    public void testClassListMapping() throws IOException {
        CvList cvList = new CvList();
        cvList.getCv().addAll(exporter.getDataList("CvList", Cv.class));
        Assert.assertEquals(cvList.getCv().size(), 4);
    }

    @Test
    public void testClassItemMapping() {
        AnalysisSoftware as = exporter.getDataItem("AnalysisSoftware.PeptideShaker",  AnalysisSoftware.class);
        Assert.assertEquals(as.getName(), "PeptideShaker");
    }
}
