package com.compomics.colims.client.playground;

import com.compomics.colims.client.storage.QueueManager;
import com.compomics.colims.client.storage.impl.QueueManagerImpl;
import com.compomics.colims.core.bean.PtmFactoryWrapper;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.UnpackedPsDataImport;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImportMapper;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.client.storage.DbTaskProducer;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerDataImport;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.distributed.model.PersistMetadata;
import com.compomics.colims.distributed.model.PersistDbTask;
import com.compomics.colims.distributed.model.enums.DbEntityType;
import com.compomics.colims.distributed.model.enums.PersistType;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.jms.InvalidSelectorException;
import javax.jms.JMSException;
import javax.management.openmbean.OpenDataException;
import org.apache.commons.compress.archivers.ArchiveException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xmlpull.v1.XmlPullParserException;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground2 {
    
    public static void main(String[] args) throws IOException, MappingException, SQLException, FileNotFoundException, ClassNotFoundException, InterruptedException, IllegalArgumentException, MzMLUnmarshallerException, XmlPullParserException, ArchiveException, JMSException, InvalidSelectorException, OpenDataException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-client-context.xml");

//        QueueManager queueManager = applicationContext.getBean("queueManager", QueueManager.class);
        InstrumentService instrumentService = applicationContext.getBean("instrumentService", InstrumentService.class);
        Instrument instrument = instrumentService.findAll().get(0);
        DbTaskProducer storageTaskProducer = applicationContext.getBean("storageTaskProducer", DbTaskProducer.class);
        
        PersistDbTask persistDbTask = new PersistDbTask();
        persistDbTask.setSubmissionTimestamp(System.currentTimeMillis());
        persistDbTask.setUserId(1L);
        persistDbTask.setDbEntityType(DbEntityType.ANALYTICAL_RUN);
        PersistMetadata storageMetadata = new PersistMetadata();
        storageMetadata.setStorageType(PersistType.PEPTIDESHAKER);
        storageMetadata.setDescription("test description2");        
        storageMetadata.setInstrument(instrument);
        
        persistDbTask.setPersistMetadata(storageMetadata);
        persistDbTask.setDataImport(new PeptideShakerDataImport(null, null, null));
        
        storageTaskProducer.sendDbTask(persistDbTask);

//        QueueMonitor queueMonitor = applicationContext.getBean("queueMonitor", QueueMonitor.class);
//        List<StorageMetadata> messages = queueMonitor.getMessages("test");
//        PeptideShakerIO peptideShakerIO = applicationContext.getBean("peptideShakerIO", PeptideShakerIO.class);
//        PeptideShakerImportMapper peptideShakerImportMapper = applicationContext.getBean("peptideShakerImportMapper", PeptideShakerImportMapper.class);
//        UserService userService = applicationContext.getBean("userService", UserService.class);
//        SampleService sampleService = applicationContext.getBean("sampleService", SampleService.class);
//        AnalyticalRunService analyticalRunService = applicationContext.getBean("analyticalRunService", AnalyticalRunService.class);
//        PtmFactoryWrapper ptmFactoryWrapper = applicationContext.getBean("ptmFactoryWrapper", PtmFactoryWrapper.class);
//        AuthenticationBean authenticationBean = applicationContext.getBean("authenticationBean", AuthenticationBean.class);
//
//        //load mods from test resources instead of user folder
//        Resource utilitiesMods = new ClassPathResource("searchGUI_mods.xml");
//        ptmFactoryWrapper.getPtmFactory().clearFactory();
//        ptmFactoryWrapper.getPtmFactory().importModifications(utilitiesMods.getFile(), false);
//
//        //set admin user in authentication bean
//        User adminUser = userService.findByName("admin1");
//        userService.fetchAuthenticationRelations(adminUser);
//        authenticationBean.setCurrentUser(adminUser);
//
//        //import PeptideShaker .cps file
//        PeptideShakerDataImport peptideShakerImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("test_peptideshaker_project.cps").getFile());
//        //set mgf files and fasta file
//        List<File> mgfFiles = new ArrayList<>();
//        mgfFiles.add(new ClassPathResource("input_spectra.mgf").getFile());
//        peptideShakerImport.setMgfFiles(mgfFiles);
//        peptideShakerImport.setFastaFile(new ClassPathResource("uniprot_sprot_101104_human_concat.fasta").getFile());
//
//        List<AnalyticalRun> analyticalRuns = peptideShakerImportMapper.map(peptideShakerImport);
//
//        //get sample from db
//        Sample sample = sampleService.findAll().get(0);
//
//        //set sample and persist
//        for (AnalyticalRun analyticalRun : analyticalRuns) {
//            analyticalRun.setSample(sample);
//            analyticalRunService.save(analyticalRun);
//        }
    }
}