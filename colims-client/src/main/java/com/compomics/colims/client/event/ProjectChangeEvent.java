package com.compomics.colims.client.event;

import com.compomics.colims.model.Project;

/**
 * @author Niels Hulstaert
 */
public class ProjectChangeEvent extends EntityChangeEvent {

    private Project project;

    public ProjectChangeEvent(final Type type, final boolean childrenAffected, final Project project) {
        super(type, childrenAffected);
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
