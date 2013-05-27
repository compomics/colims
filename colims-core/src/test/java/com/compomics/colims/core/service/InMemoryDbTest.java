package com.compomics.colims.core.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import com.compomics.colims.core.io.parser.MzMLParser;
import com.compomics.colims.model.Experiment;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class InMemoryDbTest {

    private static final Logger LOGGER = Logger.getLogger(InMemoryDbTest.class);
    @Autowired
    private MzMLParser mzMLParser;
    @Autowired
    private ExperimentService experimentService;

    /**
     * A simple H2 db test.
     */
    @Test
    public void testParseMzMLAndPersistExperiment() throws IOException, MzMLUnmarshallerException {
        //import test mzML file
        List<File> mzMLFiles = new ArrayList<File>();
        File mzMLFile = new ClassPathResource("test_mzML_1.mzML").getFile();
        mzMLFiles.add(mzMLFile);

        mzMLParser.importMzMLFiles(mzMLFiles);

        //try to parse unknown mzML file, should throw IllegalArgumentArgumentException
        Experiment experiment = mzMLParser.parseMzmlFile("test_mzML_1.mzML");

        //experimentService.save(experiment);

        System.out.println("test");
    }
}
