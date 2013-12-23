/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.searches.respin.control.processrunner;

import com.compomics.colims.distributed.config.distributedconfiguration.client.RespinProperties;
import com.compomics.colims.distributed.searches.respin.model.processes.common.CommandExceptionGuard;
import com.compomics.colims.distributed.searches.respin.model.processes.peptideshaker.PeptideShakerProcess;
import com.compomics.colims.distributed.searches.respin.model.processes.searchgui.SearchGuiProcess;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth
 */
public class ProcessRunner {

    private final File mgf;
    private final File searchParametersFile;
    private List<String> args;
    private static final Logger LOGGER = Logger.getLogger(ProcessRunner.class);
    private String projectID;
    //default currentProcess parameters for PeptideShaker
    private double maxPrecursorError = 1.0;
    //default currentProcess parameters for SearchGUI
    private File outputDir;
    private ProcessEnum currentProcess;
    private static Process process;
    private static RespinProperties respinProps;

    public ProcessRunner(File mgf, File searchParametersFile) {
        this.mgf = mgf;
        this.searchParametersFile = searchParametersFile;
        try {
            respinProps = RespinProperties.getInstance();
        } catch (IOException|URISyntaxException ex) {
            LOGGER.error(ex);
        } 
    }

    public ProcessRunner setProjectID(String projectID) {
        this.projectID = projectID;
        return this;
    }

    public ProcessRunner setProcess(ProcessEnum process) throws IOException, ConfigurationException {
        if (process.equals(ProcessEnum.SEARCHGUI)) {
            this.args = new SearchGuiProcess(this.mgf, this.searchParametersFile).generateCommand();
            this.currentProcess = ProcessEnum.SEARCHGUI;
        }
        if (process.equals(ProcessEnum.PEPTIDESHAKER)) {
            this.args = new PeptideShakerProcess(this.maxPrecursorError, this.searchParametersFile)
                    .setProjectID(projectID)
                    .generateCommand();
            this.currentProcess = ProcessEnum.PEPTIDESHAKER;
        }
        return this;
    }

    public ProcessRunner setMaxPrecursorError(double maxPrecursorErrror) {
        this.maxPrecursorError = maxPrecursorErrror;
        return this;
    }

    public int startProcess() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        processBuilder.directory(respinProps.getRespinDirectory());
        LOGGER.debug("Current workingdirectory " + respinProps.getRespinDirectory());
        if (currentProcess.equals(ProcessEnum.PEPTIDESHAKER)) {
            LOGGER.debug("Changed workingdirectory to " + new File(respinProps.getPeptideShakerJarPath()).getParent());
            processBuilder.directory(new File(respinProps.getPeptideShakerJarPath()).getParentFile());
        }
        if (currentProcess.equals(ProcessEnum.SEARCHGUI)) {
            LOGGER.debug("Changed workingdirectory to " + new File(respinProps.getSearchGUIJarPath()).getParent());
            processBuilder.directory(new File(respinProps.getSearchGUIJarPath()).getParentFile());
        }
        process = processBuilder.start();
        CommandExceptionGuard guard = new CommandExceptionGuard(process);
        Thread guardThread = new Thread(guard);
        guardThread.start();
        process.waitFor();
        return process.exitValue();
    }

    public ProcessRunner setOutputFolder(File outputDir) {
        this.outputDir = outputDir;
        return this;
    }

    public static void endProcess() {
        if (process != null) {
            process.destroy();
        }
    }
}
