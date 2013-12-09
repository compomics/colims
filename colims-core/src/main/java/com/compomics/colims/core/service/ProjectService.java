/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import java.util.List;

import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;

/**
 *
 * @author Niels Hulstaert
 */
public interface ProjectService extends GenericService<Project, Long> {

    /**
     * Find the project by title.
     *
     * @param title the project title
     * @return the found project
     */
    Project findByTitle(String title);
    
    /**
     * Find all projects and load the according experiments.
     * 
     * @return the found projects
     */
    List<Project> findAllWithEagerFetching();
    
    /**
     * Get the user that owns the most projects.
     *
     * @return
     */
    User getUserWithMostProjectOwns();
}
