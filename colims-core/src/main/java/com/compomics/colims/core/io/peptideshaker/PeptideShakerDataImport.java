package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.DataImport;
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
    private Resource peptideShakerCpsArchive;
    /**
     * The list of mgf resources
     */
    private List<Resource> mgfResources;

    public PeptideShakerDataImport(Resource peptideShakerCpsArchive, List<Resource> mgfResources) {
        this.peptideShakerCpsArchive = peptideShakerCpsArchive;
        this.mgfResources = mgfResources;
    }

    public Resource getPeptideShakerCpsArchive() {
        return peptideShakerCpsArchive;
    }

    public void setPeptideShakerCpsArchive(Resource peptideShakerCpsArchive) {
        this.peptideShakerCpsArchive = peptideShakerCpsArchive;
    }

    public List<Resource> getMgfResources() {
        return mgfResources;
    }

    public void setMgfResources(List<Resource> mgfFiles) {
        this.mgfResources = mgfFiles;
    }            
}
