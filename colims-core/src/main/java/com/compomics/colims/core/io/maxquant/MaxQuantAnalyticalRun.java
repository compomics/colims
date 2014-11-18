package com.compomics.colims.core.io.maxquant;

import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author Davy
 */
@Component("maxQuantAnalyticalRun")
public class MaxQuantAnalyticalRun {

    private String analyticalRunName;
    private final List<MSnSpectrum> spectraFoundInAnalyticalRun = new ArrayList<>();
    private SearchParameters runParameters;

    public void addASpectrum(MSnSpectrum aSpectrum) {
        spectraFoundInAnalyticalRun.add(aSpectrum);
    }

    public List<MSnSpectrum> getListOfSpectra() {
        return Collections.unmodifiableList(spectraFoundInAnalyticalRun);
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
