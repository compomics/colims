package com.compomics.colims.client.event;

/**
 * @author Niels Hulstaert
 */
public class AnalyticalRunChangeEvent extends EntityChangeEvent {

    /**
     * The analytical run ID.
     */
    private final Long analyticalRunId;
    /**
     * The parent sample ID;
     */
    private final Long parentSampleId;

    /**
     * Constructor.
     *
     * @param type            the change event type
     * @param analyticalRunId the analytical run ID
     * @param parentSampleId  the parent sample ID
     */
    public AnalyticalRunChangeEvent(final Type type, final Long analyticalRunId, final Long parentSampleId) {
        super(type);
        this.analyticalRunId = analyticalRunId;
        this.parentSampleId = parentSampleId;
    }

    public Long getAnalyticalRunId() {
        return analyticalRunId;
    }

    public Long getParentSampleId() {
        return parentSampleId;
    }
}
