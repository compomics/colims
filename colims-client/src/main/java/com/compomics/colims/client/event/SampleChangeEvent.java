package com.compomics.colims.client.event;

import com.compomics.colims.model.Sample;

/**
 * @author Niels Hulstaert
 */
public class SampleChangeEvent extends EntityChangeEvent {

    private Sample sample;

    public SampleChangeEvent(Type type, boolean childrenAffected, Sample sample) {
        super(type, childrenAffected);
        this.sample = sample;
    }

    public Sample getSample() {
        return sample;
    }
    
}
