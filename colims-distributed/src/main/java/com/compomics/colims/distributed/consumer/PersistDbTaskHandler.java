package com.compomics.colims.distributed.consumer;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.MaxQuantImporter;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImport;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImporter;
import com.compomics.colims.core.io.peptideshaker.UnpackedPeptideShakerImport;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.distributed.model.CompletedDbTask;
import com.compomics.colims.distributed.model.DbTaskError;
import com.compomics.colims.core.io.MappedDataImport;
import com.compomics.colims.core.service.DataStorageService;
import com.compomics.colims.distributed.model.PersistDbTask;
import com.compomics.colims.distributed.producer.CompletedTaskProducer;
import com.compomics.colims.distributed.producer.DbTaskErrorProducer;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.SearchAndValidationSettings;
import java.io.IOException;
import java.util.List;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class handles a PersistDbTask: map the DataImport en store it in the
 * database.
 *
 * @author Niels Hulstaert
 */
@Component("persistDbTaskHandler")
public class PersistDbTaskHandler {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(PersistDbTaskHandler.class);

    /**
     * The CompletedDbTask sender.
     */
    @Autowired
    private CompletedTaskProducer completedTaskProducer;
    /**
     * The DbTaskError sender.
     */
    @Autowired
    private DbTaskErrorProducer dbTaskErrorProducer;
    /**
     * The PeptideShaker IO handler.
     */
    @Autowired
    private PeptideShakerIO peptideShakerIO;
    /**
     * The PeptideShaker data importer.
     */
    @Autowired
    private PeptideShakerImporter peptideShakerImporter;
    /**
     * The MaxQuant data importer.
     */
    @Autowired
    private MaxQuantImporter maxQuantImporter;
    /**
     * The user entity service.
     */
    @Autowired
    private UserService userService;
    /**
     * The sample entity service.
     */
    @Autowired
    private SampleService sampleService;

    @Autowired
    private DataStorageService dataStorageService;

    public void handlePersistDbTask(PersistDbTask persistDbTask) {
        Long started = System.currentTimeMillis();
        try {
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

            store(mappedDataImport, persistDbTask, sample, userName);

            //wrap the PersistDbTask in a CompletedTask and send it to the completed task queue
            completedTaskProducer.sendCompletedDbTask(new CompletedDbTask(started, System.currentTimeMillis(), persistDbTask));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            //wrap the StorageTask in a StorageError and send it to the error queue
            dbTaskErrorProducer.sendDbTaskError(new DbTaskError(started, System.currentTimeMillis(), persistDbTask, e));
        }
    }

    /**
     * Map the persist db task.
     *
     * @param persistDbTask the persist task containing the DataImport object
     * @return the MappedDataImport instance
     * @throws MappingException thrown in case of a mapping exception
     */
    private MappedDataImport mapDataImport(PersistDbTask persistDbTask) throws MappingException, IOException, ArchiveException, ClassNotFoundException {
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

                maxQuantImporter.initImport(persistDbTask.getDataImport());
                analyticalRuns = maxQuantImporter.importInputAndResults(null, null);

                mappedDataImport = new MappedDataImport(null, null, analyticalRuns);
                break;
            default:
                break;
        }

        return mappedDataImport;
    }

    private void store(MappedDataImport mappedDataImport, PersistDbTask persistDbTask, Sample sample, String userName) {
        dataStorageService.store(mappedDataImport, sample, persistDbTask.getPersistMetadata().getInstrument(), userName, persistDbTask.getPersistMetadata().getStartDate());
    }

}
