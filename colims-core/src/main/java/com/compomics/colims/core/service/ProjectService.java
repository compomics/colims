/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;

import java.util.List;

/**
 * This interface provides service methods for the Project class.
 *
 * @author Niels Hulstaert
 */
public interface ProjectService extends GenericService<Project, Long> {

    /**
     * Count the projects by title.
     *
     * @param project the Project instance
     * @return the number of found projects
     */
    Long countByTitle(Project project);

    /**
     * Find all projects and fetch the associated experiments and samples.
     *
     * @return the found projects
     */
    List<Project> findAllWithEagerFetching();

    /**
     * Get the user that owns the most projects.
     *
     * @return the User instance
     */
    User getUserWithMostProjectOwns();

    /**
     * Fetch the users linked to the given project.
     *
     * @param project the Project instance
     */
    void fetchUsers(Project project);

}
