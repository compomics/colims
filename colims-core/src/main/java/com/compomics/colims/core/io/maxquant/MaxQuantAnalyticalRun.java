package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.identification.SearchParameters;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Davy
 */
@Component("maxQuantAnalyticalRun")
public class MaxQuantAnalyticalRun {

    /**
     * The MaxQuant directory.
     */
    private File maxQuantDirectory;
    /**
     * The analytical run name.
     */
    private String analyticalRunName;
    private Map<Integer, Spectrum> spectraFoundInAnalyticalRun = new HashMap<>();
    private SearchParameters runParameters;

    /**
     * No-arg constructor.
     */
    public MaxQuantAnalyticalRun() {
    }

    /**
     * Constructor.
     *
     * @param analyticalRunName the run name
     * @param maxQuantDirectory the MaxQuant file directory
     */
    public MaxQuantAnalyticalRun(String analyticalRunName, File maxQuantDirectory) {
        this.analyticalRunName = analyticalRunName;
        this.maxQuantDirectory = maxQuantDirectory;
    }

    public void setAnalyticalRunName(String analyticalRunName) {
        this.analyticalRunName = analyticalRunName;
    }

    public String getAnalyticalRunName() {
        return analyticalRunName;
    }

    public File getMaxQuantDirectory() {
        return maxQuantDirectory;
    }

    public void setMaxQuantDirectory(File maxQuantDirectory) {
        this.maxQuantDirectory = maxQuantDirectory;
    }

    public SearchParameters getParametersForRun() {
        return runParameters;
    }

    public void setParametersForRun(SearchParameters parametersForRun) {
        this.runParameters = parametersForRun;
    }

    public void addASpectrum(Integer id, Spectrum aSpectrum) {
        spectraFoundInAnalyticalRun.put(id, aSpectrum);
    }

    public Map<Integer, Spectrum> getSpectra() {
        return Collections.unmodifiableMap(spectraFoundInAnalyticalRun);
    }
}
