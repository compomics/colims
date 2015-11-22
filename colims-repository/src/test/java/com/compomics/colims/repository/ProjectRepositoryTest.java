package com.compomics.colims.repository;

import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import com.compomics.colims.model.UserBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Autowired
    private UserBean userBean;

    @Test
    public void testPersistProject() {
        Project project = new Project();

        project.setTitle("test project title");
        project.setLabel("testLabel");
        project.setDescription("test project description");

        //set owner and current user in user bean
        User owner = userRepository.findByName("admin1");
        project.setOwner(owner);
        userBean.setCurrentUser(owner);

        long numberOfProjects = projectRepository.countAll();

        //persist project
        projectRepository.persist(project);

        //test AuditableDatabaseEntity audit fields
        Assert.assertEquals("admin1", project.getUserName());
        Assert.assertNotNull(project.getCreationDate());
        Assert.assertNotNull(project.getModificationDate());

        Assert.assertNotNull(project.getId());
        Assert.assertNotNull(project.getOwner());

        //test some methods of the generic repository for this entity.
        Assert.assertNotNull(projectRepository.findById(project.getId()));
        Assert.assertNotNull(projectRepository.findByExample(project));
        Assert.assertEquals(numberOfProjects + 1, projectRepository.countAll());

        //delete project
        projectRepository.remove(project);
        Assert.assertEquals(numberOfProjects, projectRepository.countAll());
    }

    @Test
    public void testGetUserWithMostProjectOwns() {
        User userWithMostProjectOwns = projectRepository.getUserWithMostProjectOwns();

        Assert.assertNotNull(userWithMostProjectOwns);
        Assert.assertEquals(1L, userWithMostProjectOwns.getId().longValue());
    }

    @Test
    public void testFindAllWithEagerFetching() {
        List<Project> projects = projectRepository.findAllWithEagerFetching();

        Assert.assertFalse(projects.isEmpty());
        Assert.assertEquals(3, projects.size());
    }

    @Test
    public void testFetchUsers() {
        List<User> users = projectRepository.fetchUsers(1L);

        Assert.assertFalse(users.isEmpty());
        Assert.assertEquals(2, users.size());
    }
}
