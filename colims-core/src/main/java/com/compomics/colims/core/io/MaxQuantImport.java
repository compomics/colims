package com.compomics.colims.core.io;

import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.model.enums.QuantificationMethod;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Davy
 * @author niels
 */
public class MaxQuantImport extends DataImport {

    private static final long serialVersionUID = 304064762112880171L;

    public static final String NO_LABEL = "none";

    /**
     * The mqpar file path.
     */
    private String mqParFile;
    /**
     * The directory of the combined folder.
     */
    private String combinedDirectory;
    /**
     * The full directory of combined folder.
     */
    private String fullCombinedDirectory;
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
    private List<String> selectedProteinGroupsHeaders;
    /**
     * The quantification label.
     */
    private QuantificationMethod quantificationMethod;

    /**
     * no-arg Constructor.
     */
    public MaxQuantImport() {
    }

    /**
     * Constructor.
     *
     * @param mqParFile                    the mqpar.xml parameter file
     * @param combinedDirectory            file pointer to the MaxQuant combined directory
     * @param fullCombinedDirectory        file pointer to the MaxQuant full combined directory
     * @param fastaDbIds                   the FASTA database map (key: FastaDb type; value: the FastaDb instance ID)
     * @param includeContaminants          whether to import proteins from contaminants file.
     * @param includeUnidentifiedSpectra   whether to import unidentified spectra from APL files.
     * @param selectedProteinGroupsHeaders list of optional headers to store in protein group quantification labeled
     *                                     table.
     * @param quantificationMethod           the quantification type
     */
    public MaxQuantImport(final String mqParFile,
                          final String combinedDirectory,
                          final String fullCombinedDirectory,
                          final EnumMap<FastaDbType, List<Long>> fastaDbIds,
                          boolean includeContaminants,
                          boolean includeUnidentifiedSpectra,
                          List<String> selectedProteinGroupsHeaders,
                          QuantificationMethod quantificationMethod) {
        super(fastaDbIds);
        this.mqParFile = mqParFile;
        this.combinedDirectory = combinedDirectory;
        this.fullCombinedDirectory = fullCombinedDirectory;
        this.includeContaminants = includeContaminants;
        this.includeUnidentifiedSpectra = includeUnidentifiedSpectra;
        this.selectedProteinGroupsHeaders = selectedProteinGroupsHeaders;
        this.quantificationMethod = quantificationMethod;
    }

    public String getMqParFile() {
        return mqParFile;
    }

    public String getCombinedDirectory() {
        return combinedDirectory;
    }

    public boolean isIncludeContaminants() {
        return includeContaminants;
    }

    public boolean isIncludeUnidentifiedSpectra() {
        return includeUnidentifiedSpectra;
    }

    public List<String> getSelectedProteinGroupsHeaders() {
        return selectedProteinGroupsHeaders;
    }

    public QuantificationMethod getQuantificationMethod() {
        return quantificationMethod;
    }

    public void setMqParFile(String mqParFile) {
        this.mqParFile = mqParFile;
    }

    public void setCombinedDirectory(String combinedDirectory) {
        this.combinedDirectory = combinedDirectory;
    }

    public String getFullCombinedDirectory() {
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
        hash = 97 * hash + Objects.hashCode(this.selectedProteinGroupsHeaders);
        hash = 97 * hash + Objects.hashCode(this.quantificationMethod);
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
        if (!Objects.equals(this.quantificationMethod, other.quantificationMethod)) {
            return false;
        }
        if (!Objects.equals(this.mqParFile, other.mqParFile)) {
            return false;
        }
        if (!Objects.equals(this.fullCombinedDirectory, other.fullCombinedDirectory)) {
            return false;
        }
        return Objects.equals(this.selectedProteinGroupsHeaders, other.selectedProteinGroupsHeaders);
    }

}
