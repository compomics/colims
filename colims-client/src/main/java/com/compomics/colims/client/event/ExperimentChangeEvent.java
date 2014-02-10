package com.compomics.colims.client.event;

import com.compomics.colims.model.Experiment;

/**
 * @author Niels Hulstaert
 */
public class ExperimentChangeEvent extends EntityChangeEvent {

    private final Experiment experiment;

    public ExperimentChangeEvent(final Type type, final Experiment experiment) {
        super(type);
        this.experiment = experiment;
    }

    public Experiment getExperiment() {
        return experiment;
    }
    
}
