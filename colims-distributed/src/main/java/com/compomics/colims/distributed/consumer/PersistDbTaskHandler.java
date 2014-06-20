package com.compomics.colims.distributed.consumer;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.MaxQuantImport;
import com.compomics.colims.core.io.maxquant.MaxQuantImporter;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImport;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImporter;
import com.compomics.colims.core.io.peptideshaker.UnpackedPeptideShakerImport;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.distributed.model.CompletedDbTask;
import com.compomics.colims.distributed.model.DbTaskError;
import com.compomics.colims.distributed.model.MappedDataImport;
import com.compomics.colims.distributed.model.PersistDbTask;
import com.compomics.colims.distributed.producer.CompletedTaskProducer;
import com.compomics.colims.distributed.producer.DbTaskErrorProducer;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.SearchAndValidationSettings;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Niels Hulstaert
 */
@Component("persistDbTaskHandler")
public class PersistDbTaskHandler {

    private static final Logger LOGGER = Logger.getLogger(PersistDbTaskHandler.class);

    @Autowired
    private CompletedTaskProducer completedTaskProducer;
    @Autowired
    private DbTaskErrorProducer dbTaskErrorProducer;
    @Autowired
    private PeptideShakerIO peptideShakerIO;
    @Autowired
    private PeptideShakerImporter peptideShakerImporter;
    @Autowired
    private MaxQuantImporter maxQuantImporter;
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private UserService userService;
    @Autowired
    private SampleService sampleService;
    @Autowired
    private SearchAndValidationSettingsService searchAndValidationSettingsService;

    public void handlePersistDbTask(PersistDbTask persistDbTask) {
        try {
            Long started = System.currentTimeMillis();

            //check if the entity class is of the right type
            if (!persistDbTask.getDbEntityClass().equals(AnalyticalRun.class)) {
                throw new IllegalArgumentException("The entity to persist should be of class " + AnalyticalRun.class.getName());
            }

            //get the sample
            Sample sample = sampleService.findById(persistDbTask.getEnitityId());
            if (sample == null) {
                throw new IllegalArgumentException("The sample entity with ID " + persistDbTask.getEnitityId() + " was not found in the database.");
            }

            //get the user name for auditing
            String userName = userService.findUserNameById(persistDbTask.getUserId());
            if (userName == null) {
                throw new IllegalArgumentException("The user with ID " + persistDbTask.getUserId() + " was not found in the database.");
            }

            //map the task
            MappedDataImport mappedDataImport = mapDataImport(persistDbTask);

            store(persistDbTask, sample, userName, mappedDataImport);

            //wrap the PersistDbTask in a CompletedTask and send it to the completed task queue
            completedTaskProducer.sendCompletedDbTask(new CompletedDbTask(started, System.currentTimeMillis(), persistDbTask));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            //wrap the StorageTask in a StorageError and send it to the error queue
            dbTaskErrorProducer.sendDbTaskError(new DbTaskError(persistDbTask, e));
        }
    }

    /**
     * Map the persist db task.
     *
     * @param persistDbTask the persist task containing the DataImport object
     * @return
     * @throws MappingException
     */
    private MappedDataImport mapDataImport(PersistDbTask persistDbTask) throws MappingException, IOException, ArchiveException, ClassNotFoundException, SQLException {
        MappedDataImport mappedDataImport = null;

        switch (persistDbTask.getPersistMetadata().getStorageType()) {
            case PEPTIDESHAKER:
                //unpack .cps archive
                UnpackedPeptideShakerImport unpackedPeptideShakerImport = peptideShakerIO.unpackPeptideShakerImport((PeptideShakerImport) (persistDbTask.getDataImport()));

                //clear resources before mapping
                peptideShakerImporter.clear();

                peptideShakerImporter.initImport(unpackedPeptideShakerImport);
                SearchAndValidationSettings searchAndValidationSettings = peptideShakerImporter.importSearchSettings();
                List<AnalyticalRun> analyticalRuns = peptideShakerImporter.importInputAndResults(searchAndValidationSettings, null);

                mappedDataImport = new MappedDataImport(searchAndValidationSettings, null, analyticalRuns);

                //try to delete the temporary directory with the unpacked .cps file
                try {
                    FileUtils.deleteDirectory(unpackedPeptideShakerImport.getUnpackedDirectory());
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage());
                }
//                if (unpackedPeptideShakerImport.getUnpackedDirectory().exists()) {
//                    LOGGER.warn("The directory " + unpackedPeptideShakerImport.getDbDirectory() + " could not be deleted.");
//                }
                break;
            case MAX_QUANT:
                //clear resources before mapping
                maxQuantImporter.clear();
                analyticalRuns = maxQuantImporter.map((MaxQuantImport) persistDbTask.getDataImport());
                break;
            default:
                break;
        }

        return mappedDataImport;
    }

    @Transactional
    private void store(PersistDbTask persistDbTask, Sample sample, String userName, MappedDataImport mappedDataImport) {
        //get experiment for sample
        Experiment experiment = sample.getExperiment();

        //first store the SearchAndValidationSettings
        SearchAndValidationSettings searchAndValidationSettings = mappedDataImport.getSearchAndValidationSettings();
        searchAndValidationSettings.setExperiment(experiment);
        searchAndValidationSettingsService.save(searchAndValidationSettings);        
        
        for (AnalyticalRun analyticalRun : mappedDataImport.getAnalyticalRuns()) {
            analyticalRun.setCreationDate(new Date());
            analyticalRun.setModificationDate(new Date());
            analyticalRun.setUserName(userName);
            analyticalRun.setStartDate(persistDbTask.getPersistMetadata().getStartDate());
            analyticalRun.setSample(sample);
            analyticalRun.setInstrument(persistDbTask.getPersistMetadata().getInstrument());
            analyticalRunService.saveOrUpdate(analyticalRun);
            //rollback test
            throw new IllegalArgumentException();
        }
    }

}
