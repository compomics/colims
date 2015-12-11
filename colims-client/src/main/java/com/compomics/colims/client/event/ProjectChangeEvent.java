package com.compomics.colims.client.event;

/**
 * @author Niels Hulstaert
 */
public class ProjectChangeEvent extends EntityChangeEvent {

    /**
     * The project ID.
     */
    private final Long projectId;

    /**
     * Constructor.
     *
     * @param type      the change type
     * @param projectId the project ID
     */
    public ProjectChangeEvent(final Type type, final Long projectId) {
        super(type);
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }
}
