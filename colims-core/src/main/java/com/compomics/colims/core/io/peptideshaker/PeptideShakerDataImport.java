package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.DataImport;
import java.io.File;
import java.util.List;

import com.compomics.util.experiment.MsExperiment;

/**
 *
 * @author Niels Hulstaert
 */
    public class PeptideShakerDataImport extends DataImport {

    /**
     * The location of the db contained in the PeptideShaker .cps archive
     */
    private File dbDirectory;
    /**
     * The list of mgf files
     */
    private List<File> mgfFiles;
    private MsExperiment msExperiment;

    public PeptideShakerDataImport(final MsExperiment msExperiment, final File dbDirectory) {
        this.dbDirectory = dbDirectory;
        this.msExperiment = msExperiment;
    }

    public File getDbDirectory() {
        return dbDirectory;
    }

    public void setDbDirectory(final File dbDirectory) {
        this.dbDirectory = dbDirectory;
    }

    public List<File> getMgfFiles() {
        return mgfFiles;
    }

    public void setMgfFiles(final List<File> mgfFiles) {
        this.mgfFiles = mgfFiles;
    }   

    public MsExperiment getMsExperiment() {
        return msExperiment;
    }

    public void setMsExperiment(final MsExperiment msExperiment) {
        this.msExperiment = msExperiment;
    }

    /**
     * Browse through the mgf files and return the (first) found mgf file with
     * the given name. Return null if no file with the given name was found.
     *
     * @param mgfFileName the mgf file name
     * @return the found mgf file
     */
    public File getMgfFileByName(final String mgfFileName) {
        File foundMgfFile = null;
        if (mgfFiles != null) {
            for (File mgfFile : mgfFiles) {
                if (mgfFile.getName().toLowerCase().equals(mgfFileName.toLowerCase())) {
                    foundMgfFile = mgfFile;
                    break;
                }
            }
        }

        return foundMgfFile;
    }
}
