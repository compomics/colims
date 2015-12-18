package com.compomics.colims.client.event;

import com.compomics.colims.model.AnalyticalRun;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
public class SampleChangeEvent extends EntityChangeEvent {

    /**
     * The sample ID.
     */
    private final Long sampleId;
    /**
     * The analytical runs associated with the sample.
     */
    private List<AnalyticalRun> analyticalRuns;

    /**
     * Constructor.
     *
     * @param type     the change type
     * @param sampleId the sample ID
     */
    public SampleChangeEvent(final Type type, final Long sampleId) {
        super(type);
        this.sampleId = sampleId;
    }

    /**
     * Constructor.
     *
     * @param type           the change type
     * @param sampleId       the sample ID
     * @param analyticalRuns the list of analytical runs
     */
    public SampleChangeEvent(final Type type, final Long sampleId, final List<AnalyticalRun> analyticalRuns) {
        this(type, sampleId);
        this.analyticalRuns = analyticalRuns;
    }

    public Long getSampleId() {
        return sampleId;
    }

    public List<AnalyticalRun> getAnalyticalRuns() {
        return analyticalRuns;
    }
}
