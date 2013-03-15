/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Project;
import com.compomics.colims.repository.ProjectRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("projectRepository")
public class ProjectHibernateRepository extends GenericHibernateRepository<Project, Long> implements ProjectRepository {
}
