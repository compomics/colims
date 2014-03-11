package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.DataImport;
import java.io.File;
import java.util.List;

import org.springframework.core.io.Resource;

/**
 *
 * @author Niels Hulstaert
 */
public class PeptideShakerDataImport extends DataImport {

    /**
     * The PeptideShaker .cps file
     */
    private File peptideShakerCpsArchive;
    /**
     * The list of mgf files
     */
    private List<File> mgfResources;

    /**
     * Constructor
     * 
     * @param peptideShakerCpsArchive
     * @param fastaResource
     * @param mgfResources
     */
    public PeptideShakerDataImport(final File peptideShakerCpsArchive, final File fastaResource, final List<File> mgfResources) {
        super(fastaResource);
        this.peptideShakerCpsArchive = peptideShakerCpsArchive;
        this.mgfResources = mgfResources;
    }

    public File getPeptideShakerCpsArchive() {
        return peptideShakerCpsArchive;
    }

    public void setPeptideShakerCpsArchive(File peptideShakerCpsArchive) {
        this.peptideShakerCpsArchive = peptideShakerCpsArchive;
    }

    public List<File> getMgfFiles() {
        return mgfResources;
    }

    public void setMgfFiles(List<File> mgfFiles) {
        this.mgfResources = mgfFiles;
    }            
}
