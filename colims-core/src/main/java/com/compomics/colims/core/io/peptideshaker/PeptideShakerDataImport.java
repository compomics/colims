package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.DataImport;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Niels Hulstaert
 */
public class PeptideShakerDataImport extends DataImport {

    private static final String CPS_FILE_PATH = "cps_file_path";
    private static final String MGF_FILE_PATH = "mgf_file_path_";

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
     * @param fastaResource
     * @param mgfResources
     */
    public PeptideShakerDataImport(final File peptideShakerCpsArchive, final File fastaResource, final List<File> mgfResources) {
        super(fastaResource);
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

    @Override
    public Map<String, String> asMap() {
        Map<String, String> properties = super.asMap();

        properties.put(CPS_FILE_PATH, peptideShakerCpsArchive.getAbsolutePath());
        for (int i = 0; i < mgfFiles.size(); i++) {
            properties.put(MGF_FILE_PATH + i, mgfFiles.get(i).getAbsolutePath());
        }

        return properties;
    }
}
