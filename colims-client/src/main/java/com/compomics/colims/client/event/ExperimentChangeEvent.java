package com.compomics.colims.client.event;

/**
 * @author Niels Hulstaert
 */
public class ExperimentChangeEvent extends EntityChangeEvent {

    /**
     * The experiment ID.
     */
    private final Long experimentId;

    /**
     * Constructor.
     *
     * @param type         the change type
     * @param experimentId the experiment ID
     */
    public ExperimentChangeEvent(final Type type, final Long experimentId) {
        super(type);
        this.experimentId = experimentId;
    }

    public Long getExperimentId() {
        return experimentId;
    }
}
