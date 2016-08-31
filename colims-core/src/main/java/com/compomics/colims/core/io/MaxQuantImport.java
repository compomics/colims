package com.compomics.colims.core.io;

import com.compomics.colims.model.enums.FastaDbType;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
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
     * Whether to import proteins from contaminants file.
     */
    private boolean includeContaminants;
    
    /**
     * Whether to import unidentified spectra from APL files.
     */
    private boolean includeUnidentifiedSpectra;
    
    /**
     * List of selected Protein Group Headers
     */
    private List<String> selectedProteinGroupHeaders;
    
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
     * @param includeUnidentifiedSpectra 
     * @param selectedProteinGroupHeaders 
     */
    public MaxQuantImport(final Path parameterFilePath, final Path combinedFolderDirectory, final EnumMap<FastaDbType, 
            Long> fastaDbIds, boolean includeContaminants, boolean includeUnidentifiedSpectra, List<String> selectedProteinGroupHeaders) {
        super(fastaDbIds);
        this.parameterFilePath = parameterFilePath;
        this.combinedFolderDirectory = combinedFolderDirectory;
        this.includeContaminants = includeContaminants;
        this.includeUnidentifiedSpectra = includeUnidentifiedSpectra;
        this.selectedProteinGroupHeaders = selectedProteinGroupHeaders;
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

    public boolean isIncludeUnidentifiedSpectra() {
        return includeUnidentifiedSpectra;
    }

    public void setIncludeUnidentifiedSpectra(boolean includeUnidentifiedSpectra) {
        this.includeUnidentifiedSpectra = includeUnidentifiedSpectra;
    }

    public List<String> getSelectedProteinGroupHeaders() {
        return selectedProteinGroupHeaders;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.parameterFilePath);
        hash = 67 * hash + Objects.hashCode(this.combinedFolderDirectory);
        hash = 67 * hash + (this.includeContaminants ? 1 : 0);
        hash = 67 * hash + (this.includeUnidentifiedSpectra ? 1 : 0);
        hash = 67 * hash + Objects.hashCode(this.selectedProteinGroupHeaders);
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
        if (this.includeUnidentifiedSpectra != other.includeUnidentifiedSpectra) {
            return false;
        }
        if (!Objects.equals(this.parameterFilePath, other.parameterFilePath)) {
            return false;
        }
        if (!Objects.equals(this.combinedFolderDirectory, other.combinedFolderDirectory)) {
            return false;
        }
        if (!Objects.equals(this.selectedProteinGroupHeaders, other.selectedProteinGroupHeaders)) {
            return false;
        }
        return true;
    }

}
