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
     * Constructor.
     *
     * @param type the change type
     * @param sample the Sample instance
     */
    public SampleChangeEvent(final Type type, final Sample sample) {
        super(type);
        this.sample = sample;
    }

    public Sample getSample() {
        return sample;
    }
    
}
