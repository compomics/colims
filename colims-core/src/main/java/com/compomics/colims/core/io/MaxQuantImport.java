package com.compomics.colims.core.io;

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
     * @param fastaDbId         the FastaDb ID
     */
    public MaxQuantImport(final File maxQuantDirectory, final Long fastaDbId) {
        super(fastaDbId);
        this.maxQuantDirectory = maxQuantDirectory;
    }

    public File getMaxQuantDirectory() {
        return maxQuantDirectory;
    }

    public void setMaxQuantDirectory(final File maxQuantDirectory) {
        this.maxQuantDirectory = maxQuantDirectory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MaxQuantImport that = (MaxQuantImport) o;

        return !(maxQuantDirectory != null ? !maxQuantDirectory.equals(that.maxQuantDirectory) : that.maxQuantDirectory != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (maxQuantDirectory != null ? maxQuantDirectory.hashCode() : 0);
        return result;
    }
}