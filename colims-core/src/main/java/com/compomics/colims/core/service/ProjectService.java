/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.Project;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface ProjectService extends GenericService<Project, Long> {

    /**
     * Find all projects and load the according experiments.
     * 
     * @return the found projects
     */
    List<Project> findAllWithEagerFetching();
}
