package com.compomics.colims.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import junit.framework.Assert;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testPeristProject() {
        Project project = new Project();

        project.setTitle("test project title");
        project.setLabel("testLabel");
        project.setDescription("test project description");

        //set owner
        User owner = userRepository.findByName("user1_name");
        project.setOwner(owner);

        long numberOfProjects = projectRepository.countAll();

        //persist project
        projectRepository.save(project);

        //test AbstractDatabaseEntity properties set by hibernate interceptor
        assertEquals("N/A", project.getUserName());
        assertNotNull(project.getCreationdate());
        assertNotNull(project.getModificationdate());

        assertNotNull(project.getId());
        assertNotNull(project.getOwner());

        //test some methods of the generic repository for this entity.
        assertNotNull(projectRepository.findById(project.getId()));
        assertNotNull(projectRepository.findByExample(project));
        assertEquals(numberOfProjects + 1, projectRepository.countAll());

        //delete project
        projectRepository.delete(project);
        assertEquals(numberOfProjects, projectRepository.countAll());
    }
    
    @Test
    public void testGetUserWithMostProjectOwns(){
        User userWithMostProjectOwns = projectRepository.getUserWithMostProjectOwns();
        
        assertNotNull(userWithMostProjectOwns);
        assertEquals(1L, userWithMostProjectOwns.getId().longValue());
    }
}
