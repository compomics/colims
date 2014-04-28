package com.compomics.colims.client.event;

import com.compomics.colims.model.Experiment;

/**
 * @author Niels Hulstaert
 */
public class ExperimentChangeEvent extends EntityChangeEvent {

    private final Experiment experiment;

    /**
     *
     * @param type
     * @param experiment
     */
    public ExperimentChangeEvent(final Type type, final Experiment experiment) {
        super(type);
        this.experiment = experiment;
    }

    /**
     *
     * @return
     */
    public Experiment getExperiment() {
        return experiment;
    }
    
}
