package com.compomics.colims.distributed.consumer;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.MaxQuantDataImport;
import com.compomics.colims.core.io.maxquant.MaxQuantImportMapper;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerDataImport;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImportMapper;
import com.compomics.colims.core.io.peptideshaker.UnpackedPsDataImport;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.distributed.model.CompletedDbTask;
import com.compomics.colims.distributed.model.DbTaskError;
import com.compomics.colims.distributed.model.PersistDbTask;
import com.compomics.colims.distributed.producer.CompletedTaskProducer;
import com.compomics.colims.distributed.producer.DbTaskErrorProducer;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Sample;
import com.google.common.io.Files;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private PeptideShakerImportMapper peptideShakerImportMapper;
    @Autowired
    private MaxQuantImportMapper maxQuantImportMapper;
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private UserService userService;
    @Autowired
    private SampleService sampleService;

    public void handlePersistDbTask(PersistDbTask persistDbTask) {
        try {
            Long started = System.currentTimeMillis();

            //map the task
            List<AnalyticalRun> analyticalRuns = mapDataImport(persistDbTask);

            //store the analytical run(s)
            storeAnalyticalRuns(persistDbTask, analyticalRuns);

            //wrap the PersistDbTask in a CompletedTask and send it to the completed task queue
            completedTaskProducer.sendCompletedDbTask(new CompletedDbTask(started, System.currentTimeMillis(), persistDbTask));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            //wrap the StorageTask in a StorageError and send it to the error queue
            dbTaskErrorProducer.sendDbTaskError(new DbTaskError(persistDbTask, e));
        }
    }

    /**
     * Map the persist db task onto a list of analytical runs
     *
     * @param persistDbTask the persist task containing the DataImport object
     * @return the list of analytical runs
     * @throws MappingException
     */
    private List<AnalyticalRun> mapDataImport(PersistDbTask persistDbTask) throws MappingException, IOException, ArchiveException, ClassNotFoundException, SQLException {
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();

        switch (persistDbTask.getPersistMetadata().getStorageType()) {
            case PEPTIDESHAKER:
                //unpack .cps archive
                UnpackedPsDataImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerDataImport(((PeptideShakerDataImport) persistDbTask.getDataImport()));

                //clear resources before mapping
                peptideShakerImportMapper.clear();

                analyticalRuns = peptideShakerImportMapper.mapAnalyticalRuns(unpackedPsDataImport);

                //delete the temporary directory with the unpacked .cps file
                FileUtils.deleteDirectory(unpackedPsDataImport.getDirectory());
                if (!unpackedPsDataImport.getDirectory().exists()) {
                    LOGGER.warn("The directory " + unpackedPsDataImport.getDbDirectory() + " could not be deleted.");
                }
                break;
            case MAX_QUANT:
                //clear resources before mapping
                maxQuantImportMapper.clear();
                analyticalRuns = maxQuantImportMapper.map((MaxQuantDataImport) persistDbTask.getDataImport());
                break;
            default:
                break;
        }

        return analyticalRuns;
    }

    /**
     * Store the analytical runs in the database.
     *
     * @param persistDbTask the persist database task
     * @param analyticalRuns the list of analytical runs
     */
    private void storeAnalyticalRuns(PersistDbTask persistDbTask, List<AnalyticalRun> analyticalRuns) {
        //find the user name by ID for auditing
        String userName = userService.findUserNameById(persistDbTask.getUserId());
        //find the sample by ID
        Sample sample = sampleService.findById(persistDbTask.getEnitityId());
        if (sample == null) {
            throw new IllegalArgumentException("The sample with ID " + persistDbTask.getEnitityId() + " was not found in the database.");
        }

        for (AnalyticalRun analyticalRun : analyticalRuns) {
            analyticalRun.setCreationDate(new Date());
            analyticalRun.setModificationDate(new Date());
            analyticalRun.setUserName(userName);
            analyticalRun.setStartDate(persistDbTask.getPersistMetadata().getStartDate());
            analyticalRun.setSample(sample);
            analyticalRun.setInstrument(persistDbTask.getPersistMetadata().getInstrument());
            analyticalRunService.saveOrUpdate(analyticalRun);
        }
    }

}
