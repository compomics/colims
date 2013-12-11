package com.compomics.colims.core.io.parser;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.exception.PeptideShakerIOException;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.model.PeptideShakerImport;
import com.compomics.colims.core.mapper.Mapper;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})

public class StresstestCpsInsert {

    @Autowired
    UserService userService;
    @Autowired
    AuthenticationBean authenticationBean;
    PeptideShakerImport peptideShakerImport;
    @Autowired
    PeptideShakerIO peptideShakerIO;
    @Autowired
    Mapper utilitiesExperimentMapper;
    @Autowired
    ProjectService projectService;
    File cpsFileFolder = new File("C:\\Users\\Davy\\Desktop\\java\\colims\\colims-core\\target");
    Experiment experiment;
    @Test
    public void testCpsFolderInsertion() throws PeptideShakerIOException, MappingException {
        User user = userService.findByName("admin1");
        userService.fetchAuthenticationRelations(user);
        authenticationBean.setCurrentUser(user);
        int cpsCounter = 0;

        for (File cpsFolder : cpsFileFolder.listFiles()) {
            cpsCounter++;
            for (File fileInFolder : cpsFolder.listFiles()) {
                if (fileInFolder.getName().contains(".cps")) {
                    peptideShakerImport = peptideShakerIO.unpackPeptideShakerCpsArchive(fileInFolder);
                }
            }
            List<File> mgfFiles = new ArrayList<>();
            for (File fileInFolder : cpsFolder.listFiles()) {
                if (fileInFolder.getName().contains(".mgf")) {       
                    mgfFiles.add(fileInFolder);
                    peptideShakerImport.setMgfFiles(mgfFiles);
                } else if (fileInFolder.getName().contains(".fasta")) {
                    peptideShakerImport.setFastaFile(fileInFolder);
                }
            }
            experiment = new Experiment();
            utilitiesExperimentMapper.map(peptideShakerImport, experiment);
            Project project = new Project();
            project.setDescription("test description");
            project.setTitle("project title");
            project.setLabel(String.valueOf(cpsCounter++));
            List<Experiment> experiments = new ArrayList<>();
            experiments.add(experiment);
            experiment.setProject(project);

            projectService.save(project);
        }
    }
}
