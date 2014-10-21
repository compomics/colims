package com.compomics.colims.core.io;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.SearchAndValidationSettings;
import java.util.List;

/**
 * This convenience class holds the entities that need to be stored in the
 * database.
 *
 * @author Niels Hulstaert
 */
public class MappedDataImport {

    /**
     * The SearchAndValidationSettings to import.
     */
    private SearchAndValidationSettings searchAndValidationSettings;
    /**
     * The QuantificationSettings to import.
     */
    private QuantificationSettings quantificationSettings;
    /**
     * The list of analytical to import.
     */
    private List<AnalyticalRun> analyticalRuns;

    public MappedDataImport(SearchAndValidationSettings searchAndValidationSettings, QuantificationSettings quantificationSettings, List<AnalyticalRun> analyticalRuns) {
        this.searchAndValidationSettings = searchAndValidationSettings;
        this.quantificationSettings = quantificationSettings;
        this.analyticalRuns = analyticalRuns;
    }

    public SearchAndValidationSettings getSearchAndValidationSettings() {
        return searchAndValidationSettings;
    }

    public void setSearchAndValidationSettings(SearchAndValidationSettings searchAndValidationSettings) {
        this.searchAndValidationSettings = searchAndValidationSettings;
    }

    public QuantificationSettings getQuantificationSettings() {
        return quantificationSettings;
    }

    public void setQuantificationSettings(QuantificationSettings quantificationSettings) {
        this.quantificationSettings = quantificationSettings;
    }

    public List<AnalyticalRun> getAnalyticalRuns() {
        return analyticalRuns;
    }

    public void setAnalyticalRuns(List<AnalyticalRun> analyticalRuns) {
        this.analyticalRuns = analyticalRuns;
    }

}
