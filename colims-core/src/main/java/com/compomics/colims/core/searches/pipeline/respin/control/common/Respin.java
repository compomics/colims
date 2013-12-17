/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.searches.pipeline.respin.control.common;

import com.compomics.colims.core.searches.pipeline.respin.control.configuration.RespinProperties;
import com.compomics.colims.core.searches.pipeline.respin.model.exception.RespinException;
import com.compomics.colims.core.searches.pipeline.respin.model.processes.respinprocess.RespinCommandLine;
import com.compomics.colims.core.searches.pipeline.respin.model.enums.RespinState;
import com.compomics.colims.core.searches.pipeline.respin.model.processes.respinprocess.RespinProcess;
import com.compomics.util.experiment.biology.EnzymeFactory;
import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Kenneth
 */
public class Respin {

    private static Logger LOGGER = Logger.getLogger(Respin.class);
    private static File fastaFile;
    private static File paramFile;
    private static File mgfFile;
    private static String projectID;
    private static final EnzymeFactory enzymeFactory = EnzymeFactory.getInstance();
    private static File outputDir;
    private static RespinCommandLine command;
    private static RespinProperties respProps;

    public void launch(File mgf, File searchparameters, File fasta, File outputFolder, String projectId) throws RespinException, Exception {
        this.mgfFile = mgf;
        this.paramFile = searchparameters;
        this.fastaFile = fasta;
        this.outputDir = outputFolder;
        this.projectID = projectId;

        try {
            prepareLogging();
            LOGGER = Logger.getLogger(Respin.class);
            respProps = RespinProperties.getInstance();
            System.out.println("PROCESS_UPDATE>>>PARSING_ARGUMENTS");
            //------------------------------------------------------------------------------

            //------------------------------------------------------------------------------
            if (initRespin()) {
                makeLocalCopies();
                RespinProcess process = new RespinProcess(command);
                for (RespinState state : RespinState.values()) {
                    if (!state.equals(RespinState.CLOSED)) {
                        System.out.println("PROCESS_UPDATE>>>" + state.toString().toUpperCase());
                        state.prceed(process);
                    }
                }
                System.out.println("PROCESS_UPDATE>>>PROCESS_COMPLETED");
            } else {
                throw new RespinException("No valid commandline !");
            }
        } catch (NullPointerException | RespinException | IOException | ParseException | XmlPullParserException ex) {
            LOGGER.error(ex);
            //throw this error further up for enduse
            throw new RespinException("Command could not be executed");
        }
    }

    private static boolean initRespin() throws ParseException, XmlPullParserException, IOException {
        //setup the commandline
        command = new RespinCommandLine(mgfFile, paramFile, fastaFile, projectID);
        //INITIATE THE REQUIRED FACTORIES
        File enzymeFile = respProps.getEnzymeFile();
        enzymeFactory.importEnzymes(enzymeFile);
        //MAKE THE RESPINCOMMAND
        if (projectID == null) {
            String filename = mgfFile.getAbsolutePath();
            projectID = filename.toUpperCase().substring(filename.indexOf("."));
        }
        //
        LOGGER.debug("Preparing respin");
        command.setFasta(fastaFile);
        LOGGER.debug("Accepted Fasta");
        command.setOutputDir(outputDir);
        LOGGER.debug("Accepted Output Directory");
        command.setProjectID(projectID);
        LOGGER.debug("Accepted projectID");
        return true;
    }

    public static void makeLocalCopies() {
        try {
            File inputParentFolder = new File(System.getProperty("user.home") + "/.compomics/respin/temp_input/");
            FileUtils.deleteDirectory(inputParentFolder);
            inputParentFolder.mkdirs();
            //new locations : 
            File localMgfFile = new File(inputParentFolder, mgfFile.getName());
            File localFastaFile = new File(inputParentFolder, fastaFile.getName());
            File localParamFile = new File(inputParentFolder, paramFile.getName());
            //copy them to local storage
            FileUtils.copyFile(mgfFile, localMgfFile);
            FileUtils.copyFile(fastaFile, localFastaFile);
            FileUtils.copyFile(paramFile, localParamFile);
            //update them in the commandline
            mgfFile = localMgfFile;
            fastaFile = localFastaFile;
            paramFile = localParamFile;
        } catch (IOException e) {
            //Does it matter? 
            LOGGER.error(e);
        }

    }

    public static void prepareLogging() throws IOException {
        File loggingFile = new File(System.getProperty("user.home") + "/.compomics/respin/temp_results/respin.log");
        if (loggingFile.exists()) {
            loggingFile.delete();
        }
        //  loggingFile.createNewFile();
        System.setProperty("respin.logging.file", loggingFile.getAbsolutePath());
    }
}
