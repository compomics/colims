package com.compomics.colims.client.event;

import com.compomics.colims.model.Experiment;

/**
 * @author Niels Hulstaert
 */
public class ExperimentChangeEvent extends EntityChangeEvent {

    /**
     * The Experiment instance.
     */
    private final Experiment experiment;

    /**
     * Constructor.
     *
     * @param type the change type
     * @param experiment the Experiment instance
     */
    public ExperimentChangeEvent(final Type type, final Experiment experiment) {
        super(type);
        this.experiment = experiment;
    }

    public Experiment getExperiment() {
        return experiment;
    }
    
}
