/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.storage.processing.colimsimport.impl;

import com.compomics.colims.core.storage.processing.colimsimport.ColimsFileImporter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("colimsMaxQuantImporter")
public class ColimsMaxQuantImporter implements ColimsFileImporter {

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
    Experiment experiment;

    /**
     *
     * @param quantFolder the folder where the resultfiles of the quant search are located in
     * @return if the folder contains all required files
     */
    @Override
    public boolean validate(File quantFolder){
        return true;
    }
    
    @Override
    public void storeFile(String username, File cpsFileFolder) throws PeptideShakerIOException, MappingException {
        User user = userService.findByName(username);
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
