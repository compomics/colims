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
     * The PeptideShaker .cps file
     */
    private File peptideShakerCpsArchive;
    /**
     * The list of mgf files
     */
    private List<File> mgfFiles;

    /**
     * Constructor
     *
     * @param peptideShakerCpsArchive
     * @param fastaDb
     * @param mgfResources
     */
    public PeptideShakerImport(final File peptideShakerCpsArchive, final FastaDb fastaDb, final List<File> mgfResources) {
        super(fastaDb);
        this.peptideShakerCpsArchive = peptideShakerCpsArchive;
        this.mgfFiles = mgfResources;
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
