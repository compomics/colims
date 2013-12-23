/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.searches.respin.model.processes.peptideshaker;

import com.compomics.colims.distributed.config.distributedconfiguration.client.RespinProperties;
import com.compomics.colims.distributed.searches.respin.model.memory.MemoryManager;
import com.compomics.colims.distributed.searches.respin.model.processes.common.RespinProcess;
import com.compomics.software.CommandLineUtils;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth
 */
public class PeptideShakerProcess implements RespinProcess {

    private static final Logger LOGGER = Logger.getLogger(PeptideShakerProcess.class);
    private File searchParamFile;
    private String identificationFiles;
    private double maxPrecursorError;
    private String projectID;
    private File outputDir;
    private static RespinProperties respinProps;

    public PeptideShakerProcess(double maxPrecursorError, File searchParamFile) {
        this.searchParamFile = searchParamFile;
        this.maxPrecursorError = maxPrecursorError;
        try {
            respinProps = RespinProperties.getInstance();
        } catch (IOException | URISyntaxException ex) {
            LOGGER.error(ex);
        }
    }

    @Override
    public List<String> generateCommand() throws IOException {
        List<String> PSCommandLine = new ArrayList<String>();
        PSCommandLine.add("java");
        PSCommandLine.add("-Xmx" + MemoryManager.getAllowedRam() + "M");
        PSCommandLine.add("-cp");
        PSCommandLine.add(getPeptideShakerJar());
        PSCommandLine.add("eu.isas.peptideshaker.cmd.PeptideShakerCLI");
        PSCommandLine.add("-experiment");
        PSCommandLine.add(String.valueOf(projectID));
        PSCommandLine.add("-sample");
        PSCommandLine.add("respin_" + projectID);
        PSCommandLine.add("-replicate");
        PSCommandLine.add("0");
        PSCommandLine.add("-identification_files");
        PSCommandLine.add(findIdentificationFiles());
        PSCommandLine.add("-spectrum_files");
        PSCommandLine.add(getUsedMGFsFromSearchGUI());
        PSCommandLine.add("-search_params");
        PSCommandLine.add(searchParamFile.getAbsolutePath());
        PSCommandLine.add("-exclude_unknown_ptms");
        PSCommandLine.add("0");
        PSCommandLine.add("-max_precursor_error_type");
        PSCommandLine.add("1");
        PSCommandLine.add("-max_precursor_error");
        PSCommandLine.add(String.valueOf(maxPrecursorError));
        PSCommandLine.add("-out");
        PSCommandLine.add(getOutputFolder() + "/" + projectID + ".cps");
        PSCommandLine.add("-reports");
        PSCommandLine.add("0,1,2,3,4");
        //Required for R
      /*
         PSCommandLine.add("-out_txt_1");
         PSCommandLine.add(getOutputFolder() + "/");
         //Requested by Uniprot
   
         PSCommandLine.add("-out_txt_2");
         PSCommandLine.add(Installer.getResultFolder().getAbsolutePath().toString() + "/" + respinProject.getProjectID());
         */
        for (String aCommand : PSCommandLine) {
            System.out.print(aCommand + " ");
        }
        return PSCommandLine;
    }

    private File getOutputFolder() {
        if (outputDir != null) {
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
        } else {
            outputDir = respinProps.getTempResultDirectory();
        }
        return outputDir;
    }

    private String getUsedMGFsFromSearchGUI() throws IOException {
        LineNumberReader reader = null;
        ArrayList<File> mgfFiles = new ArrayList<File>();
        File searchGuiInputFile = new File(System.getProperty("user.home") + "/.compomics/respin/temp_results/searchGUI_input.txt");
        if (searchGuiInputFile.exists()) {
            try {
                reader = new LineNumberReader(new FileReader(searchGuiInputFile));
                String mgfLine;
                while ((mgfLine = reader.readLine()) != null) {
                    mgfFiles.add(new File(mgfLine));
                    LOGGER.debug("Using MGF :" + mgfLine);
                }
            } catch (IOException e) {
                LOGGER.error(e);
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    LOGGER.error(ex);
                }
            }
            return CommandLineUtils.getCommandLineArgument(mgfFiles).replace("\"", "");
        } else {
            throw new IOException("No MGF-file could be found that was used by SearchGUI");
        }

    }

    public String findIdentificationFiles() throws IOException {
        //find all the omx and t.xml in the workspace
        File[] files = respinProps.getTempResultDirectory().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".t.xml") || name.endsWith(".omx")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (files.length != 0) {
            StringBuilder idCommandLineArg = new StringBuilder();
            for (File aFile : files) {
                idCommandLineArg.append(aFile.getAbsolutePath()).append(",");
            }
            try {
                identificationFiles = idCommandLineArg.substring(0, idCommandLineArg.length() - 1);
            } catch (ArrayIndexOutOfBoundsException e) {
                LOGGER.error("No identification filese were present in the resultsfolder");
            }
        } else {
            throw new IOException("No identification files could be found");
        }
        return identificationFiles;
    }

    public String getPeptideShakerJar() {
        return respinProps.getPeptideShakerJarPath();
    }
    /*public String getPeptideShakerJar() {
     File[] peptideShakerJar = ProcessFile.peptideShakerFolder.getFile().listFiles(new FilenameFilter() {
     @Override
     public boolean accept(File dir, String name) {
     if (name.toLowerCase().startsWith("peptideshaker")) {
     return true;
     } else {
     return false;
     }
     }
     });
     if (peptideShakerJar.length == 0) {
     return null;
     } else {
     return peptideShakerJar[0].getAbsolutePath();
     }
     }*/

    public PeptideShakerProcess setOutputFolder(File outputDir) {
        this.outputDir = outputDir;
        return this;
    }

    public PeptideShakerProcess setProjectID(String projectID) {
        this.projectID = projectID;
        return this;
    }
}
