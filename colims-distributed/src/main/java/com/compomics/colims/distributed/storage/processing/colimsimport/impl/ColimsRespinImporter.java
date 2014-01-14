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
import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.core.service.InstrumentTypeService;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.InstrumentType;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.CvTermType;
import com.compomics.colims.repository.AuthenticationBean;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.naming.AuthenticationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Davy Maddelein
 */
@Component("colimsRespinImporter")
public class ColimsRespinImporter implements ColimsFileImporter {

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
    InstrumentTypeService instrumentTypeService;
    @Autowired
    CvTermService cvTermService;
    @Autowired
    AnalyticalRunService analyticalRunService;
    private static final Logger LOGGER = Logger.getLogger(ColimsRespinImporter.class);

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
        if (!username.equals("distributedUser")) {
            User user = userService.findByName(username);
            userService.fetchAuthenticationRelations(user);
            authenticationBean.setCurrentUser(user);
            return user;
        } else {
            throw new AuthenticationException("Cannot log in with distributedUser");
        }
    }

    private Sample createRespinSample() {
        //STEP 2 = create a sample
        Sample respinSample = new Sample();
        respinSample.setName("Respin Sample");
        Protocol mostUsedProtocol = sampleService.getMostUsedProtocol();
        if (mostUsedProtocol != null) {
            respinSample.setProtocol(mostUsedProtocol);
        }
        return respinSample;
    }

    private Project createRespinProject(User user, File cpsFileFolder) {
        //STEP 2 = create a sample
        //STEP 1 = create a project
        Project project = new Project();
        project.setDescription("Respin_Automated_Storage");
        project.setTitle(cpsFileFolder.getName() + "_" + System.currentTimeMillis());
        project.setLabel(String.valueOf(System.currentTimeMillis()));
        project.setOwner(user);
        //STEP 3= save
        return project;
    }

    private Experiment createRespinExperiment() {
        Experiment experiment = new Experiment();
        experiment.setDescription("Respin Searched Experiment");
        experiment.setNumber(System.currentTimeMillis());
        return experiment;
    }

    private Instrument getRespinInstrument() {
        Instrument respinInstrument = instrumentService.findByName("Respin_Instrument");

        if (respinInstrument == null) {
            Date creationDate = Calendar.getInstance().getTime();
            respinInstrument = new Instrument("Respin_Instrument");

            InstrumentCvTerm detector = (InstrumentCvTerm) cvTermService.findByAccession("respin_detector", CvTermType.DETECTOR);
            if (detector == null) {
                detector = new InstrumentCvTerm(CvTermType.DETECTOR, "Respin", "Respin", "respin_detector", "respin_detector");
                detector.setCreationdate(creationDate);
                detector.setModificationdate(creationDate);
                detector.setUserName("respin");
                cvTermService.saveOrUpdate(detector);
            }

            InstrumentCvTerm analyzer = (InstrumentCvTerm) cvTermService.findByAccession("respin_analyzer", CvTermType.ANALYZER);
            if (analyzer == null) {
                analyzer = new InstrumentCvTerm(CvTermType.ANALYZER, "Respin", "Respin", "respin_analyzer", "respin_analyzer");
                analyzer.setCreationdate(creationDate);
                analyzer.setModificationdate(creationDate);
                analyzer.setUserName("respin");
                cvTermService.saveOrUpdate(analyzer);
            }

            InstrumentCvTerm source = (InstrumentCvTerm) cvTermService.findByAccession("respin_source", CvTermType.SOURCE);
            if (source == null) {
                source = new InstrumentCvTerm(CvTermType.SOURCE, "Respin", "Respin", "respin_source", "respin_source");
                source.setCreationdate(creationDate);
                source.setModificationdate(creationDate);
                source.setUserName("respin");
                cvTermService.saveOrUpdate(source);
            }

            List<InstrumentCvTerm> analyzers = new ArrayList<>();
            analyzers.add(analyzer);
            respinInstrument.setAnalyzers(analyzers);
            respinInstrument.setDetector(detector);
            respinInstrument.setSource(source);
            respinInstrument.setCreationdate(creationDate);
            respinInstrument.setModificationdate(creationDate);
            respinInstrument.setUserName("respin");

            InstrumentType respinType = instrumentTypeService.findByName("respin_instrumentType");
            if (respinType == null) {
                respinType = new InstrumentType();
                respinType.setDescription("Respin mediated instruments");
                respinType.setName("respin_instrumentType");
                respinType.setCreationdate(creationDate);
                respinType.setModificationdate(creationDate);
                respinType.setUserName("respin");
                List<Instrument> respinInstruments = new ArrayList<>();
                respinInstruments.add(respinInstrument);
                respinType.setInstruments(respinInstruments);
                instrumentTypeService.saveOrUpdate(respinType);
            }
            respinInstrument.setInstrumentType(respinType);
            instrumentService.saveOrUpdate(respinInstrument);
        }
        return respinInstrument;
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
            User user = login(username);
            Project respinProject = createRespinProject(user, cpsFileFolder);
            Sample respinSample = createRespinSample();
            Experiment respinExperiment = createRespinExperiment();
            Instrument respinInstrument = getRespinInstrument();

            mapPeptideShakerIOInFolder(cpsFileFolder);

            List<AnalyticalRun> analyticalRunForThisSample = peptideShakerImportMapper.map(peptideShakerImport);
            for (AnalyticalRun anAnalyticalRun : analyticalRunForThisSample) {
                anAnalyticalRun.setSample(respinSample);
                anAnalyticalRun.setInstrument(respinInstrument);
                analyticalRunService.save(anAnalyticalRun);
            }
            List<Sample> respinSampleList = new ArrayList<>();
            respinSampleList.add(respinSample);
            //link it all together
            respinExperiment.setSamples(respinSampleList);
            List<Experiment> respinExperimentList = new ArrayList<>();
            respinExperimentList.add(respinExperiment);
            respinProject.setExperiments(respinExperimentList);
            //save all
            sampleService.saveOrUpdate(respinSample);
            experimentService.saveOrUpdate(respinExperiment);
            projectService.saveOrUpdate(respinProject);
        } catch (IOException | SQLException | ClassNotFoundException | InterruptedException | IllegalArgumentException | MzMLUnmarshallerException ex) {
            LOGGER.error(ex);
        }
    }

}
