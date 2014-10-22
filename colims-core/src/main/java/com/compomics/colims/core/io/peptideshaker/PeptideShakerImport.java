package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.model.FastaDb;
import java.io.File;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public class PeptideShakerImport extends DataImport {

    private static final long serialVersionUID = 6181185980673938925L;

    /**
     * The PeptideShaker .cps file.
     */
    private File peptideShakerCpsArchive;
    /**
     * The list of mgf files.
     */
    private List<File> mgfFiles;

    /**
     * Constructor.
     *
     * @param peptideShakerCpsArchive the PeptideShaker .cps file
     * @param fastaDb the FastabDb entity
     * @param mgfFiles the list of MGF files
     */
    public PeptideShakerImport(final File peptideShakerCpsArchive, final FastaDb fastaDb, final List<File> mgfFiles) {
        super(fastaDb);
        this.peptideShakerCpsArchive = peptideShakerCpsArchive;
        this.mgfFiles = mgfFiles;
    }

    public File getPeptideShakerCpsArchive() {
        return peptideShakerCpsArchive;
    }

    public void setPeptideShakerCpsArchive(File peptideShakerCpsArchive) {
        this.peptideShakerCpsArchive = peptideShakerCpsArchive;
    }

    public List<File> getMgfFiles() {
        return mgfFiles;
    }

    public void setMgfFiles(List<File> mgfFiles) {
        this.mgfFiles = mgfFiles;
    }

}
