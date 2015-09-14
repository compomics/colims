package com.compomics.colims.core.io;

import com.compomics.colims.model.FastaDb;

import java.io.File;

/**
 * @author Davy
 */
public class MaxQuantImport extends DataImport {

    private static final long serialVersionUID = 304064762112880171L;

    /**
     * The directory containing the MaxQuant files.
     */
    private File maxQuantDirectory;

    /**
     * Constructor.
     *
     * @param maxQuantDirectory the MaxQuant files directory
     * @param fastaDb           the FastaDb instance
     */
    public MaxQuantImport(final File maxQuantDirectory, final FastaDb fastaDb) {
        super(fastaDb);
        this.maxQuantDirectory = maxQuantDirectory;
    }

    public File getMaxQuantDirectory() {
        return maxQuantDirectory;
    }

    public void setMaxQuantDirectory(final File maxQuantDirectory) {
        this.maxQuantDirectory = maxQuantDirectory;
    }

}