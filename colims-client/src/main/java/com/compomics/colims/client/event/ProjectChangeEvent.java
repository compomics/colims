package com.compomics.colims.client.event;

import com.compomics.colims.model.Project;

/**
 * @author Niels Hulstaert
 */
public class ProjectChangeEvent extends EntityChangeEvent {

    private final Project project;

    public ProjectChangeEvent(final Type type, final Project project) {
        super(type);
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
