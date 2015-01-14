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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Iain on 13/01/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MzIdentMLExporterTest {
    @Autowired
    private MzIdentMLExporter exporter;

    @Autowired
    private AnalyticalRunRepository repository;

    @Test
    public void testExport() throws IOException {
        //AnalyticalRun run = repository.findById(1L);
        exporter.export();
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
}
