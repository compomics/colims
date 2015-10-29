package com.compomics.colims.repository;

import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@Rollback
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
        User owner = userRepository.findByName("admin1");
        project.setOwner(owner);

        long numberOfProjects = projectRepository.countAll();

        //persist project
        projectRepository.save(project);

        //test AuditableDatabaseEntity properties set by hibernate interceptor
        Assert.assertEquals("N/A", project.getUserName());
        Assert.assertNotNull(project.getCreationDate());
        Assert.assertNotNull(project.getModificationDate());

        Assert.assertNotNull(project.getId());
        Assert.assertNotNull(project.getOwner());

        //test some methods of the generic repository for this entity.
        Assert.assertNotNull(projectRepository.findById(project.getId()));
        Assert.assertNotNull(projectRepository.findByExample(project));
        Assert.assertEquals(numberOfProjects + 1, projectRepository.countAll());

        //delete project
        projectRepository.delete(project);
        Assert.assertEquals(numberOfProjects, projectRepository.countAll());
    }

    @Test
    public void testGetUserWithMostProjectOwns() {
        User userWithMostProjectOwns = projectRepository.getUserWithMostProjectOwns();

        Assert.assertNotNull(userWithMostProjectOwns);
        Assert.assertEquals(1L, userWithMostProjectOwns.getId().longValue());
    }
}
