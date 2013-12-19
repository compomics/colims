/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.distributed.storage.processing.colimsimport.impl;

import com.compomics.colims.core.distributed.storage.processing.colimsimport.ColimsFileImporter;
import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.exception.PeptideShakerIOException;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.model.PeptideShakerImport;
import com.compomics.colims.core.mapper.PeptideShakerImportMapper;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Davy Maddelein
 */
@Component("colimsCpsImporter")
public class ColimsCpsImporter implements ColimsFileImporter {

    @Autowired
    UserService userService;
    @Autowired
    SampleService sampleService;
    @Autowired
    AuthenticationBean authenticationBean;
    PeptideShakerImport peptideShakerImport;
    @Autowired
    PeptideShakerIO peptideShakerIO;
    @Autowired
    PeptideShakerImportMapper peptideShakerImportMapper;
    @Autowired
    ProjectService projectService;
    @Autowired
    InstrumentService instrumentService;
    @Autowired
    AnalyticalRunService analyticalRunService;
    private static final Logger LOGGER = Logger.getLogger(ColimsCpsImporter.class);

    /**
     *
     * @param cpsFileFolder the folder where the resultfiles of the
     * peptideshaker search are located in
     * @return if the folder contains all required files
     */
    @Override
    public boolean validate(File cpsFileFolder) {
        LOGGER.debug("Validating inputfolder...");
        boolean validatedCps = false;
        boolean validatedMGF = false;
        boolean validatedFasta = false;
        for (File aFile : cpsFileFolder.listFiles()) {
            if (aFile.getName().endsWith(".cps")) {
                validatedCps = true;
            }
            if (aFile.getName().endsWith(".mgf")) {
                validatedMGF = true;
            }
            if (aFile.getName().endsWith(".fasta")) {
                validatedFasta = true;
            }
        }
        LOGGER.debug("Found cps : " + validatedCps);
        LOGGER.debug("Found mgf : " + validatedMGF);
        LOGGER.debug("Found fasta : " + validatedFasta);
        return (validatedCps
                && validatedMGF
                && validatedFasta);
    }

    @Override
    public void storeFile(String username, File cpsFileFolder, long sampleID, String instrumentName) throws PeptideShakerIOException, MappingException {
        try {
            User user = userService.findByName(username);
            userService.fetchAuthenticationRelations(user);
            authenticationBean.setCurrentUser(user);

            Sample sample = sampleService.findById(sampleID);

            Instrument instrument = instrumentService.findByName(instrumentName);

            for (File fileInFolder : cpsFileFolder.listFiles()) {
                if (fileInFolder.getName().contains(".cps")) {
                    peptideShakerImport = peptideShakerIO.unpackPeptideShakerCpsArchive(fileInFolder);
                }
            }
            List<File> mgfFiles = new ArrayList<>();
            for (File fileInFolder : cpsFileFolder.listFiles()) {
                if (fileInFolder.getName().contains(".mgf")) {
                    mgfFiles.add(fileInFolder);
                    peptideShakerImport.setMgfFiles(mgfFiles);
                } else if (fileInFolder.getName().contains(".fasta")) {
                    peptideShakerImport.setFastaFile(fileInFolder);
                }
            }

            List<AnalyticalRun> analyticalRunForThisSample = peptideShakerImportMapper.map(peptideShakerImport);

            for (AnalyticalRun anAnalyticalRun : analyticalRunForThisSample) {
                anAnalyticalRun.setSample(sample);
                anAnalyticalRun.setInstrument(instrument);
                analyticalRunService.save(anAnalyticalRun);
            }

            sample.setAnalyticalRuns(analyticalRunForThisSample);

           // sampleService.update(sample);
            /*
             experiment = new Experiment();
             utilitiesExperimentMapper.map(peptideShakerImport, experiment);
             Project project = new Project();
             project.setDescription("test description");
             project.setTitle("project title");
             project.setOwner(user);
             project.setLabel(label);
             List<Experiment> experiments = new ArrayList<>();
             experiments.add(experiment);
             experiment.setProject(project);
             projectService.save(project);*/
        } catch (IOException | SQLException | ClassNotFoundException | InterruptedException | IllegalArgumentException | MzMLUnmarshallerException ex) {
            LOGGER.error(ex);
        }
    }
}
