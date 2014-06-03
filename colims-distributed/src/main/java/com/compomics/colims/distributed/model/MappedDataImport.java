
package com.compomics.colims.distributed.model;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.SearchAndValidationSettings;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public class MappedDataImport {
    
    private Sample sample;
    private SearchAndValidationSettings searchAndValidationSettings;
    private QuantificationSettings quantificationSettings;
    private List<AnalyticalRun> analyticalRuns;

    public MappedDataImport(Sample sample, SearchAndValidationSettings searchAndValidationSettings, QuantificationSettings quantificationSettings, List<AnalyticalRun> analyticalRuns) {
        this.sample = sample;
        this.searchAndValidationSettings = searchAndValidationSettings;
        this.quantificationSettings = quantificationSettings;
        this.analyticalRuns = analyticalRuns;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
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
