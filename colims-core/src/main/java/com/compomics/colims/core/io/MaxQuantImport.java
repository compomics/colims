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
     * The mqpar file path.
     */
    private Path mqParFile;

    /**
     * The directory of Combined Folder.
     */
    private Path combinedDirectory;

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
     * Selected quantification label
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
     * @param combinedDirectory           File pointer to MaxQuant combined directory
     * @param fastaDbIds                  the FASTA database map (key: FastaDb type; value: the FastaDb instance ID)
     * @param includeContaminants         whether to import proteins from contaminants file.
     * @param includeUnidentifiedSpectra  whether to import unidentified spectra from APL files.
     * @param selectedProteinGroupHeaders list of optional headers to store in protein group quantification labeled
     *                                    table.
     * @param quantificationLabel         the quantification label
     */
    public MaxQuantImport(final Path mqParFile,
                          final Path combinedDirectory,
                          final EnumMap<FastaDbType, List<Long>> fastaDbIds,
                          boolean includeContaminants,
                          boolean includeUnidentifiedSpectra,
                          List<String> selectedProteinGroupHeaders,
                          String quantificationLabel) {
        super(fastaDbIds);
        this.mqParFile = mqParFile;
        this.combinedDirectory = combinedDirectory;
        this.includeContaminants = includeContaminants;
        this.includeUnidentifiedSpectra = includeUnidentifiedSpectra;
        this.selectedProteinGroupHeaders = selectedProteinGroupHeaders;
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

    public List<String> getSelectedProteinGroupHeaders() {
        return selectedProteinGroupHeaders;
    }

    public String getQuantificationLabel() {
        return quantificationLabel;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.mqParFile);
        hash = 89 * hash + Objects.hashCode(this.combinedDirectory);
        hash = 89 * hash + (this.includeContaminants ? 1 : 0);
        hash = 89 * hash + (this.includeUnidentifiedSpectra ? 1 : 0);
        hash = 89 * hash + Objects.hashCode(this.selectedProteinGroupHeaders);
        hash = 89 * hash + Objects.hashCode(this.quantificationLabel);
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
        if (!Objects.equals(this.quantificationLabel, other.quantificationLabel)) {
            return false;
        }
        if (!Objects.equals(this.mqParFile, other.mqParFile)) {
            return false;
        }
        if (!Objects.equals(this.combinedDirectory, other.combinedDirectory)) {
            return false;
        }
        return Objects.equals(this.selectedProteinGroupHeaders, other.selectedProteinGroupHeaders);
    }


}
