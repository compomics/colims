package com.compomics.colims.repository;

import com.compomics.colims.model.Project;
import com.compomics.colims.model.ProjectParam;
import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

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
        project.setDescription("test description");
        project.setTitle("test project title");

        List<ProjectParam> projectParams = new ArrayList<ProjectParam>();
        ProjectParam projectParam_1 = new ProjectParam();
        projectParam_1.setAccession("accession_1");
        projectParam_1.setCvLabel("testCvLabel_1");
        projectParam_1.setValue("param value 1");
        projectParam_1.setProject(project);
        projectParams.add(projectParam_1);

        ProjectParam projectParam_2 = new ProjectParam();
        projectParam_2.setAccession("accession_2");
        projectParam_2.setCvLabel("testCvLabel_2");
        projectParam_2.setValue("param value 2");
        projectParam_2.setProject(project);
        projectParams.add(projectParam_2);

        project.setProjectParams(projectParams);
        //set user
        project.setUser(userRepository.findByName("user1"));

        //persist project
        projectRepository.save(project);

        //test AbstractDatabaseEntity properties set by hibernate interceptor
        assertEquals("N/A", project.getUsername());
        assertNotNull(project.getCreationdate());
        assertNotNull(project.getModificationdate());

        assertNotNull(project.getId());
        assertNotNull(project.getUser());
        assertEquals(2, project.getProjectParams().size());
        for (ProjectParam projectParam : project.getProjectParams()) {
            assertNotNull(projectParam.getId());
        }
        //test some methods of the generic repository for this entity.
        assertNotNull(projectRepository.findById(project.getId()));
        assertNotNull(projectRepository.findByExample(project));
        assertEquals(1, projectRepository.countAll());

        //delete project
        projectRepository.delete(project);
        assertEquals(0, projectRepository.countAll());
    }
}
