package com.compomics.colims.core.io.maxquant;

import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;

import java.util.*;

import org.springframework.stereotype.Component;

/**
 *
 * @author Davy
 */
@Component("maxQuantAnalyticalRun")
public class MaxQuantAnalyticalRun {

    private String analyticalRunName;
    private Map<Integer, MSnSpectrum> spectraFoundInAnalyticalRun = new HashMap<>();
    private SearchParameters runParameters;

    public void addASpectrum(Integer id, MSnSpectrum aSpectrum) {
        spectraFoundInAnalyticalRun.put(id, aSpectrum);
    }

    public Map<Integer, MSnSpectrum> getListOfSpectra() {
        return Collections.unmodifiableMap(spectraFoundInAnalyticalRun);
    }

    public void setAnalyticalRunName(String analyticalRunName) {
        this.analyticalRunName = analyticalRunName;
    }

    public String getAnalyticalRunName() {
        return analyticalRunName;
    }

    public SearchParameters getParametersForRun() {
        return runParameters;
    }

    public void setParametersForRun(SearchParameters parametersForRun) {
        this.runParameters = parametersForRun;
    }
}
