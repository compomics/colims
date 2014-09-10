package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.DataImport;
import java.util.List;

import com.compomics.util.experiment.MsExperiment;
import java.io.File;

/**
 *
 * @author Niels Hulstaert
 */
public class UnpackedPeptideShakerImport extends DataImport {

    private static final long serialVersionUID = -1854524744176973963L;
    
    /**
     * The PeptideShaker .cps archive
     */
    private File peptideShakerCpsArchive;
    /**
     * The root directory of the unpacked PeptideShaker .cps archive
     */
    private File unpackedDirectory;
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

    /**
     * Constructor.
     *
     * @param peptideShakerCpsArchive
     * @param unpackedDirectory
     * @param dbDirectory
     * @param msExperiment
     */
    public UnpackedPeptideShakerImport(final File peptideShakerCpsArchive, final File unpackedDirectory, final File dbDirectory, final MsExperiment msExperiment) {
        this.peptideShakerCpsArchive = peptideShakerCpsArchive;
        this.unpackedDirectory = unpackedDirectory;
        this.dbDirectory = dbDirectory;
        this.msExperiment = msExperiment;
    }

    public File getPeptideShakerCpsArchive() {
        return peptideShakerCpsArchive;
    }

    public void setPeptideShakerCpsArchive(File peptideShakerCpsArchive) {
        this.peptideShakerCpsArchive = peptideShakerCpsArchive;
    }

    public File getUnpackedDirectory() {
        return unpackedDirectory;
    }

    public void setUnpackedDirectory(File unpackedDirectory) {
        this.unpackedDirectory = unpackedDirectory;
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

    public MsExperiment getMsExperiment() {
        return msExperiment;
    }

    public void setMsExperiment(MsExperiment msExperiment) {
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
