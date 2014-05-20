package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.DataImport;
import java.util.List;

import com.compomics.util.experiment.MsExperiment;
import java.io.File;

/**
 *
 * @author Niels Hulstaert
 */
public class UnpackedPsDataImport extends DataImport {

    /**
     * The root directory of the unpacked PeptideShaker .cps archive
     */
    private File directory;
    /**
     * The directory of the db contained in the PeptideShaker .cps archive
     */
    private File dbDirectory;
    /**
     * The list of mgf files
     */
    private List<File> mgfFiles;
    /**
     * The Utilities MsExperiment
     */
    private MsExperiment msExperiment;

    public UnpackedPsDataImport(final MsExperiment msExperiment, final File directory, final File dbDirectory) {        
        this.msExperiment = msExperiment;
        this.directory = directory;
        this.dbDirectory = dbDirectory;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
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
     * Browse through the mgf resources and return the (first) found mgf
     * resource with the given name. Return null if no file with the given name
     * was found.
     *
     * @param mgfResourceName the mgf resource name
     * @return the found mgf resource
     */
    public File getMgfFileByName(final String mgfResourceName) {
        File foundMgfFile = null;
        if (mgfFiles != null) {
            for (File mgfFile : mgfFiles) {
                if (mgfFile.getName().toLowerCase().equals(mgfResourceName.toLowerCase())) {
                    foundMgfFile = mgfFile;
                    break;
                }
            }
        }

        return foundMgfFile;
    }
}
