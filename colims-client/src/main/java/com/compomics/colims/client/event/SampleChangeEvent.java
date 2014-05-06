package com.compomics.colims.client.event;

import com.compomics.colims.model.Sample;

/**
 * @author Niels Hulstaert
 */
public class SampleChangeEvent extends EntityChangeEvent {

    private final Sample sample;

    /**
     *
     * @param type
     * @param sample
     */
    public SampleChangeEvent(final Type type, final Sample sample) {
        super(type);
        this.sample = sample;
    }

    /**
     *
     * @return
     */
    public Sample getSample() {
        return sample;
    }
    
}
