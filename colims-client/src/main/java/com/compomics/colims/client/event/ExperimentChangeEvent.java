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
     * The experiment ID.
     */
    private final Long experimentId;

    /**
     * Constructor.
     *
     * @param type       the change type
     * @param experiment the Experiment instance
     */
    public ExperimentChangeEvent(final Type type, final Experiment experiment) {
        super(type);
        this.experiment = experiment;
        this.experimentId = null;
    }

    /**
     * Constructor.
     *
     * @param type         the change type
     * @param experimentId the experiment ID
     */
    public ExperimentChangeEvent(final Type type, final Long experimentId) {
        super(type);
        this.experiment = null;
        this.experimentId = experimentId;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public Long getExperimentId() {
        return experimentId;
    }
}
