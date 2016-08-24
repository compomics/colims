package com.compomics.colims.core.io;

import com.compomics.colims.model.enums.FastaDbType;
import java.io.File;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Objects;

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
     * Import proteins from contaminants file or not.
     */
    private boolean includeContaminants;
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
     * @param includeContaminants 
     */
    public MaxQuantImport(final Path parameterFilePath, final Path combinedFolderDirectory, final EnumMap<FastaDbType, Long> fastaDbIds, boolean includeContaminants) {
        super(fastaDbIds);
        this.parameterFilePath = parameterFilePath;
        this.combinedFolderDirectory = combinedFolderDirectory;
        this.includeContaminants = includeContaminants;
    }

    public Path getParameterFilePath() {
        return parameterFilePath;
    }

    public void setParameterFilePath(final Path parameterFilePath) {
        this.parameterFilePath = parameterFilePath;
    }

    public Path getCombinedFolderDirectory() {return combinedFolderDirectory;}

    public void setCombinedFolderDirectory(Path combinedFolderDirectory) {this.combinedFolderDirectory = combinedFolderDirectory;}

    public boolean isIncludeContaminants() {
        return includeContaminants;
    }

    public void setIncludeContaminants(boolean includeContaminants) {
        this.includeContaminants = includeContaminants;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.parameterFilePath);
        hash = 53 * hash + Objects.hashCode(this.combinedFolderDirectory);
        hash = 53 * hash + (this.includeContaminants ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MaxQuantImport other = (MaxQuantImport) obj;
        if (this.includeContaminants != other.includeContaminants) {
            return false;
        }
        if (!Objects.equals(this.parameterFilePath, other.parameterFilePath)) {
            return false;
        }
        if (!Objects.equals(this.combinedFolderDirectory, other.combinedFolderDirectory)) {
            return false;
        }
        return true;
    }

}
