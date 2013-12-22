package com.compomics.colims.client.event;

import com.compomics.colims.model.Experiment;

/**
 * @author Niels Hulstaert
 */
public class ExperimentChangeEvent extends EntityChangeEvent {

    private Experiment experiment;

    public ExperimentChangeEvent(Type type, boolean childrenAffected, Experiment experiment) {
        super(type, childrenAffected);
        this.experiment = experiment;
    }

    public Experiment getExperiment() {
        return experiment;
    }
    
}
