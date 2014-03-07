package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.DataImport;
import java.util.List;

import com.compomics.util.experiment.MsExperiment;
import java.io.File;
import org.springframework.core.io.Resource;

/**
 *
 * @author Niels Hulstaert
 */
public class UnpackedPeptideShakerDataImport extends DataImport {

    /**
     * The location of the db contained in the PeptideShaker .cps archive
     */
    private File dbDirectory;
    /**
     * The list of mgf files
     */
    private List<Resource> mgfResources;
    /**
     * The Utilities MsExperiment
     */
    private MsExperiment msExperiment;

    public UnpackedPeptideShakerDataImport(final MsExperiment msExperiment, final File dbDirectory) {
        this.dbDirectory = dbDirectory;
        this.msExperiment = msExperiment;
    }

    public File getDbDirectory() {
        return dbDirectory;
    }

    public void setDbDirectory(final File dbDirectory) {
        this.dbDirectory = dbDirectory;
    }

    public List<Resource> getMgfResources() {
        return mgfResources;
    }

    public void setMgfResources(final List<Resource> mgfResources) {
        this.mgfResources = mgfResources;
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
    public Resource getMgfResourceByName(final String mgfResourceName) {
        Resource foundMgfResource = null;
        if (mgfResources != null) {
            for (Resource mgfResource : mgfResources) {
                if (mgfResource.getFilename().toLowerCase().equals(mgfResourceName.toLowerCase())) {
                    foundMgfResource = mgfResource;
                    break;
                }
            }
        }

        return foundMgfResource;
    }
}
