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

    public static final String LABEL_FREE = "label free";
    public static final String TMT = "TMT";
    public static final String ITRAQ = "iTRAQ";
    public static final String SILAC = "SILAC";
    public static final String ICAT = "ICAT";
    public static final String SRM = "Selected Reaction Monitoring";
    public static final String NO_LABEL = "none";

    /**
     * The mqpar file path.
     */
    private Path mqParFile;
    /**
     * The directory of the combined folder.
     */
    private Path combinedDirectory;
    /**
     * The full directory of combined folder.
     */
    private Path fullCombinedDirectory;
    /**
     * Whether to import proteins from contaminants file.
     */
    private boolean includeContaminants;
    /**
     * Whether to import unidentified spectra from APL files.
     */
    private boolean includeUnidentifiedSpectra;
    /**
     * The list of selected protein group headers.
     */
    private List<String> selectedProteinGroupHeaders;
    /**
     * The quantification label.
     */
    private String quantificationLabel;

    /**
     * no-arg Constructor.
     */
    public MaxQuantImport() {
    }

    /**
     * Constructor.
     *
     * @param mqParFile                   the mqpar.xml parameter file
     * @param combinedDirectory           File pointer to the MaxQuant combined directory
     * @param fullCombinedDirectory       File pointer to the MaxQuant full combined directory
     * @param fastaDbIds                  the FASTA database map (key: FastaDb type; value: the FastaDb instance ID)
     * @param includeContaminants         whether to import proteins from contaminants file.
     * @param includeUnidentifiedSpectra  whether to import unidentified spectra from APL files.
     * @param selectedProteinGroupsHeaders list of optional headers to store in protein group quantification labeled
     *                                    table.
     * @param quantificationLabel         the quantification label
     */
    public MaxQuantImport(final Path mqParFile,
                          final Path combinedDirectory,
                          final Path fullCombinedDirectory,
                          final EnumMap<FastaDbType, List<Long>> fastaDbIds,
                          boolean includeContaminants,
                          boolean includeUnidentifiedSpectra,
                          List<String> selectedProteinGroupsHeaders,
                          String quantificationLabel) {
        super(fastaDbIds);
        this.mqParFile = mqParFile;
        this.combinedDirectory = combinedDirectory;
        this.fullCombinedDirectory = fullCombinedDirectory;
        this.includeContaminants = includeContaminants;
        this.includeUnidentifiedSpectra = includeUnidentifiedSpectra;
        this.selectedProteinGroupHeaders = selectedProteinGroupsHeaders;
        this.quantificationLabel = quantificationLabel;
    }

    public Path getMqParFile() {
        return mqParFile;
    }

    public Path getCombinedDirectory() {
        return combinedDirectory;
    }

    public boolean isIncludeContaminants() {
        return includeContaminants;
    }

    public boolean isIncludeUnidentifiedSpectra() {
        return includeUnidentifiedSpectra;
    }

    public List<String> getSelectedProteinGroupsHeaders() {
        return selectedProteinGroupHeaders;
    }

    public String getQuantificationLabel() {
        return quantificationLabel;
    }

    public void setMqParFile(Path mqParFile) {
        this.mqParFile = mqParFile;
    }

    public void setCombinedDirectory(Path combinedDirectory) {
        this.combinedDirectory = combinedDirectory;
    }

    public Path getFullCombinedDirectory() {
        return fullCombinedDirectory;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.mqParFile);
        hash = 97 * hash + Objects.hashCode(this.combinedDirectory);
        hash = 97 * hash + Objects.hashCode(this.fullCombinedDirectory);
        hash = 97 * hash + (this.includeContaminants ? 1 : 0);
        hash = 97 * hash + (this.includeUnidentifiedSpectra ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.selectedProteinGroupHeaders);
        hash = 97 * hash + Objects.hashCode(this.quantificationLabel);
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
        if (this.includeUnidentifiedSpectra != other.includeUnidentifiedSpectra) {
            return false;
        }
        if (!Objects.equals(this.quantificationLabel, other.quantificationLabel)) {
            return false;
        }
        if (!Objects.equals(this.mqParFile, other.mqParFile)) {
            return false;
        }
        if (!Objects.equals(this.fullCombinedDirectory, other.fullCombinedDirectory)) {
            return false;
        }
        return Objects.equals(this.selectedProteinGroupHeaders, other.selectedProteinGroupHeaders);
    }

}
