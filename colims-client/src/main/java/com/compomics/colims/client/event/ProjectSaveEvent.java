package com.compomics.colims.client.event;

/**
 *
 * @author Niels Hulstaert
 */
public class ProjectSaveEvent {

    private Long projectId;

    public ProjectSaveEvent(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
