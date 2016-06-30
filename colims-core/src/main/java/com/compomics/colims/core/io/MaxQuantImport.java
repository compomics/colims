package com.compomics.colims.core.io;

import com.compomics.colims.model.enums.FastaDbType;
import java.io.File;
import java.nio.file.Path;
import java.util.EnumMap;

/**
 * @author Davy
 * @author niels
 */
public class MaxQuantImport extends DataImport {

    private static final long serialVersionUID = 304064762112880171L;

    /**
     * The directory containing the MaxQuant files.
     */
    private Path parameterFilePath;

    /**
     * The directory of Combined Folder.
     */
    private Path combinedFolderDirectory;

    /**
     * no-arg Constructor.
     */
    public MaxQuantImport() {
    }

    /**
     * Constructor of MaxQuantImport
     * @param parameterFilePath
     * @param combinedFolderDirectory
     * @param fastaDbIds 
     */
    public MaxQuantImport(final Path parameterFilePath, final Path combinedFolderDirectory, final EnumMap<FastaDbType, Long> fastaDbIds) {
        super(fastaDbIds);
        this.parameterFilePath = parameterFilePath;
        this.combinedFolderDirectory = combinedFolderDirectory;
    }

    public Path getParameterFilePath() {
        return parameterFilePath;
    }

    public void setParameterFilePath(final Path parameterFilePath) {
        this.parameterFilePath = parameterFilePath;
    }

    public Path getCombinedFolderDirectory() {return combinedFolderDirectory;}

    public void setCombinedFolderDirectory(Path combinedFolderDirectory) {this.combinedFolderDirectory = combinedFolderDirectory;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MaxQuantImport that = (MaxQuantImport) o;

        if (parameterFilePath != null ? !parameterFilePath.equals(that.parameterFilePath) : that.parameterFilePath != null)
            return false;
        if (combinedFolderDirectory != null ? !combinedFolderDirectory.equals(that.combinedFolderDirectory) : that.combinedFolderDirectory != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (parameterFilePath != null ? parameterFilePath.hashCode() : 0);
        result = 31 * result + (combinedFolderDirectory != null ? combinedFolderDirectory.hashCode() : 0);
        return result;
    }
}
