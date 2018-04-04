package com.compomics.colims.distributed.io.peptideshaker;

import com.compomics.colims.core.io.DataImport;
import eu.isas.peptideshaker.utils.CpsParent;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * The class represents the unarchived PeptideShaker .cps file and all other objects necessary for importing the PS
 * experiment.
 *
 * @author Niels Hulstaert
 */
public class UnpackedPeptideShakerImport extends DataImport {

    private static final long serialVersionUID = -1854524744176973963L;

    /**
     * The PeptideShaker .cps archive.
     */
    private File peptideShakerCpsArchive;
    /**
     * The root directory of the unpacked PeptideShaker .cps archive.
     */
    private File unpackedDirectory;
    /**
     * The list of MGF files.
     */
    private List<Path> mgfFiles;
    /**
     * The CpsParent instance for interacting with the .cps file.
     */
    private transient CpsParent cpsParent;

    /**
     * Constructor.
     *
     * @param peptideShakerCpsArchive the PeptideShaker .cps file
     * @param unpackedDirectory       the directory where the .cps file is unzipped
     * @param cpsParent               the CpsParent instance
     */
    public UnpackedPeptideShakerImport(final File peptideShakerCpsArchive, final File unpackedDirectory, final CpsParent cpsParent) {
        this.peptideShakerCpsArchive = peptideShakerCpsArchive;
        this.unpackedDirectory = unpackedDirectory;
        this.cpsParent = cpsParent;
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

    public List<Path> getMgfFiles() {
        return mgfFiles;
    }

    public void setMgfFiles(List<Path> mgfFiles) {
        this.mgfFiles = mgfFiles;
    }

    public CpsParent getCpsParent() {
        return cpsParent;
    }

    public void setCpsParent(CpsParent cpsParent) {
        this.cpsParent = cpsParent;
    }

    /**
     * Browse through the mgf resources and return the (first) found mgf resource with the given name. Return null if no
     * file with the given name was found.
     *
     * @param mgfResourceName the mgf resource name
     * @return the found mgf resource
     */
//    public Path getMgfFileByName(final String mgfResourceName) {
//        Path foundMgfFile = null;
//        if (mgfFiles != null) {
//            for (Path mgfFile : mgfFiles) {
//                if (mgfFile.getFileName().toString().toLowerCase().equals(mgfResourceName.toLowerCase())) {
//                    foundMgfFile = mgfFile;
//                    break;
//                }
//            }
//        }
//
//        return foundMgfFile;
//    }

}
