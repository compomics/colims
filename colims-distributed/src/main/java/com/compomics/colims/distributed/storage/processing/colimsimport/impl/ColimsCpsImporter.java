/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.storage.processing.colimsimport.impl;

import com.compomics.colims.distributed.storage.processing.colimsimport.ColimsFileImporter;
import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.exception.PeptideShakerIOException;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.model.PeptideShakerImport;
import com.compomics.colims.core.mapper.PeptideShakerImportMapper;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.naming.AuthenticationException;
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
    ExperimentService experimentService;
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
    private Object utilitiesExperimentMapper;

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
    
    private User login(String username) throws AuthenticationException {
        if (!username.equals("distributed")) {
            User user = userService.findByName(username);
            userService.fetchAuthenticationRelations(user);
            authenticationBean.setCurrentUser(user);
            return user;
        } else {
            throw new AuthenticationException("Cannot log in with distributed");
        }
    }
       
    private void mapPeptideShakerIOInFolder(File cpsFileFolder) throws PeptideShakerIOException {
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
    }
     
    @Override
    public void storeFile(String username, File cpsFileFolder, long sampleID, String instrumentName) throws PeptideShakerIOException, MappingException, AuthenticationException {
        try {
            //STEP 1 = login with distributed User
            //STEP 2 = login with actual user != distributedUser
            login(username);
            //STEP 2 map the inputdata
            mapPeptideShakerIOInFolder(cpsFileFolder);
            //STEP3 find the sample
            Sample sample = sampleService.findById(sampleID);
            //STEP 4 find the instrument
            Instrument instrument = instrumentService.findByName(instrumentName);
            //STEP 5 find the experiments
            Experiment experiment = sample.getExperiment();
            experiment.setTitle(cpsFileFolder.getName());
            experiment.setStorageLocation(cpsFileFolder.getAbsolutePath());
            experiment.setNumber(System.currentTimeMillis());
            experimentService.save(experiment);
            //STEP 5 find the analytical runs
            List<AnalyticalRun> analyticalRunForThisSample = peptideShakerImportMapper.map(peptideShakerImport);
            for (AnalyticalRun anAnalyticalRun : analyticalRunForThisSample) {
                anAnalyticalRun.setSample(sample);
                anAnalyticalRun.setInstrument(instrument);
                analyticalRunService.save(anAnalyticalRun);
            }
            //STEP 6 save the sample
            sample.setAnalyticalRuns(analyticalRunForThisSample);
            sampleService.update(sample);
            
        } catch (IOException | SQLException | ClassNotFoundException | InterruptedException | IllegalArgumentException | MzMLUnmarshallerException ex) {
            LOGGER.error(ex);
        }
    }
    
}
