package com.compomics.colims.core.io;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinGroup;

import java.util.List;
import java.util.Set;

/**
 * Holder class for the mapped analytical runs.
 * <p/>
 * Created by Niels Hulstaert on 3/12/15.
 */
public class MappedData {

    /**
     * The mapped run.
     */
    private List<AnalyticalRun> analyticalRuns;
    /**
     * The set of mapped protein groups.
     */
    private Set<ProteinGroup> proteinGroups;

    /**
     * Constructor.
     *
     * @param analyticalRuns the mapped analytical runs
     * @param proteinGroups  the map of protein groups
     */
    public MappedData(List<AnalyticalRun> analyticalRuns, Set<ProteinGroup> proteinGroups) {
        this.analyticalRuns = analyticalRuns;
        this.proteinGroups = proteinGroups;
    }

    public List<AnalyticalRun> getAnalyticalRuns() {
        return analyticalRuns;
    }

    public void setAnalyticalRuns(List<AnalyticalRun> analyticalRuns) {
        this.analyticalRuns = analyticalRuns;
    }

    public Set<ProteinGroup> getProteinGroups() {
        return proteinGroups;
    }

    public void setProteinGroups(Set<ProteinGroup> proteinGroups) {
        this.proteinGroups = proteinGroups;
    }
}
