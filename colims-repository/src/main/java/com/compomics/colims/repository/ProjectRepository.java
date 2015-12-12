/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;

import java.util.List;

/**
 * This interface provides repository methods for the Project class.
 *
 * @author Niels Hulstaert
 */
public interface ProjectRepository extends GenericRepository<Project, Long> {

    /**
     * Count the projects by title.
     *
     * @param project the Project instance
     * @return the number of found projects
     */
    Long countByTitle(Project project);

    /**
     * Find all projects and fetch the associated experiments.
     *
     * @return the found projects
     */
    List<Project> findAllWithFetchedExperiments();

    /**
     * Find the project by ID and fetch the associated experiments.
     *
     * @param projectId the project ID
     * @return the found project
     */
    Project findByIdWithFetchedExperiments(Long projectId);

    /**
     * Get the user that owns the most projects.
     *
     * @return the User instance
     */
    User getUserWithMostProjectOwns();

    /**
     * Fetch the project users for the given project.
     *
     * @param projectId the project ID
     * @return the list of project users
     */
    List<User> fetchUsers(Long projectId);

}
