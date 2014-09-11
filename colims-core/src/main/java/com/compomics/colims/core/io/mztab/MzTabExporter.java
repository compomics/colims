/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.mztab;

import com.compomics.colims.model.AnalyticalRun;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
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
    private static final String COLUMN_DELIMETER = "/t";
    private static final String COMMENT_PREFIX = "COM";
    private static final String METADATA_PREFIX = "MTD";

    public void exportAnalyticalRun(File exportDirectory, AnalyticalRun analyticalRun) {        
        try (FileOutputStream fos = new FileOutputStream(new File(exportDirectory, analyticalRun.getName() + MZTAB_EXTENSION));
                OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8").newEncoder());
                BufferedWriter bw = new BufferedWriter(osw);
                PrintWriter pw = new PrintWriter(bw)) {

            pw.println("under development");

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
