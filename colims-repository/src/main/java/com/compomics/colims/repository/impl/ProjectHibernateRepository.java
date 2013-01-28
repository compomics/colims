/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Project;
import com.compomics.colims.repository.ProjectRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("projectRepository")
public class ProjectHibernateRepository extends GenericHibernateRepository<Project, Long> implements ProjectRepository {
}
