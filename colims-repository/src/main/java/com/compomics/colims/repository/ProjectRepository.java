/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;

/**
 *
 * @author Niels Hulstaert
 */
public interface ProjectRepository extends GenericRepository<Project, Long> {

    /**
     * Get the user that owns the most projects.
     *
     * @return
     */
    User getUserWithMostProjectOwns();
}
