package com.compomics.colims.core.io.model;

import com.compomics.util.experiment.MsExperiment;
import java.io.File;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public class PeptideShakerImport {

    /**
     * The location of the db contained in the PeptideShaker .cps archive
     */
    private File dbDirectory;
    /**
     * The list of mgf files
     */
    private List<File> mgfFiles;
    /**
     * The fasta file
     */
    private File fastaFile;
    private MsExperiment msExperiment;

    public PeptideShakerImport(MsExperiment msExperiment, File dbDirectory) {
        this.dbDirectory = dbDirectory;
        this.msExperiment = msExperiment;
    }

    public File getDbDirectory() {
        return dbDirectory;
    }

    public void setDbDirectory(File dbDirectory) {
        this.dbDirectory = dbDirectory;
    }

    public List<File> getMgfFiles() {
        return mgfFiles;
    }

    public void setMgfFiles(List<File> mgfFiles) {
        this.mgfFiles = mgfFiles;
    }

    public File getFastaFile() {
        return fastaFile;
    }

    public void setFastaFile(File fastaFile) {
        this.fastaFile = fastaFile;
    }

    public MsExperiment getMsExperiment() {
        return msExperiment;
    }

    public void setMsExperiment(MsExperiment msExperiment) {
        this.msExperiment = msExperiment;
    }

    /**
     * Browse through the mgf files and return the (first) found mgf file.
     * Return null if no file with the given name was found.
     *
     * @param mgfFileName the mgf file name
     * @return the found mgf file
     */
    public File getMgfFileByName(String mgfFileName) {
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
