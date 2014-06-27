/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.mztab;

import com.compomics.colims.model.AnalyticalRun;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("mzTabExporter")
public class MzTabExporter {

    private static final Logger LOGGER = Logger.getLogger(MzTabExporter.class);
    private static final String MZTAB_EXTENSION = ".mzTab";

    public void exportAnalyticalRun(File exportDirectory, AnalyticalRun analyticalRun) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(exportDirectory, analyticalRun.getName() + MZTAB_EXTENSION))))) {

            pw.println("test");

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}