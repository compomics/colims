package com.compomics.colims.client.event;

import com.compomics.colims.model.Sample;

/**
 * @author Niels Hulstaert
 */
public class SampleChangeEvent extends EntityChangeEvent {

    /**
     * The Sample instance.
     */
    private final Sample sample;
    /**
     * The sample ID.
     */
    private final Long sampleId;

    /**
     * Constructor.
     *
     * @param type   the change type
     * @param sample the Sample instance
     */
    public SampleChangeEvent(final Type type, final Sample sample) {
        super(type);
        this.sample = sample;
        this.sampleId = null;
    }

    /**
     * Constructor.
     *
     * @param type     the change type
     * @param sampleId the sample ID
     */
    public SampleChangeEvent(final Type type, final Long sampleId) {
        super(type);
        this.sample = null;
        this.sampleId = sampleId;
    }

    public Sample getSample() {
        return sample;
    }

    public Long getSampleId() {
        return sampleId;
    }
}
