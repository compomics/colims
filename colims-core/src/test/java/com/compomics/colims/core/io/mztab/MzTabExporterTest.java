/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.mztab;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.mzml.MzMLParser;
import com.compomics.colims.model.*;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml" })
public class MzTabExporterTest {

    @Autowired
    private MzTabExporter mzTabExporter;

    /**
     * Test the export of a single run.
     *
     * @throws IOException
     * @throws MzMLUnmarshallerException
     * @throws MappingException
     */
    @Test
    public void testExportSingleRun_1() throws IOException, MzMLUnmarshallerException, MappingException {
        mzTabExporter.exportAnalyticalRun(new File("testFile"), new MzTabExport());

        System.out.println("test");
    }
}
