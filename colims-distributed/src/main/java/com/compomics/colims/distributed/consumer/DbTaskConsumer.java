package com.compomics.colims.distributed.consumer;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.MaxQuantDataImport;
import com.compomics.colims.core.io.maxquant.MaxQuantImportMapper;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerDataImport;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImportMapper;
import com.compomics.colims.core.io.peptideshaker.UnpackedPsDataImport;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.distributed.model.DbTaskError;
import com.compomics.colims.distributed.model.PersistDbTask;
import com.compomics.colims.distributed.model.CompletedDbTask;
import com.compomics.colims.distributed.model.DbTask;
import com.compomics.colims.distributed.model.DeleteDbTask;
import com.compomics.colims.distributed.producer.DbTaskErrorProducer;
import com.compomics.colims.distributed.producer.CompletedTaskProducer;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.AuthenticationBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("dbTaskConsumer")
public class DbTaskConsumer implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(DbTaskConsumer.class);

    @Autowired
    private CompletedTaskProducer storedTaskProducer;
    @Autowired
    private DbTaskErrorProducer storageErrorProducer;
    @Autowired
    private PeptideShakerIO peptideShakerIO;
    @Autowired
    private PeptideShakerImportMapper peptideShakerImportMapper;
    @Autowired
    private MaxQuantImportMapper maxQuantImportMapper;
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private SampleService sampleService;
    @Autowired
    private AuthenticationBean authenticationBean;

    /**
     * Implementation of <code>MessageListener</code>.
     *
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        try {
            ActiveMQObjectMessage objectMessage = (ActiveMQObjectMessage) message;
            DbTask dbTask = (DbTask) objectMessage.getObject();

            if (dbTask instanceof PersistDbTask) {
                LOGGER.info("Received db task message of type " + ((PersistDbTask) dbTask).getPersistMetadata().getStorageType().userFriendlyName());
            } else if (dbTask instanceof DeleteDbTask) {
                LOGGER.info("Received db task message of type " + ((DeleteDbTask) dbTask).getDbEntityType().userFriendlyName());
            }

//            try {
//                Long started = System.currentTimeMillis();
//
//                //map the task
//                List<AnalyticalRun> analyticalRuns = mapDataImport(storageTask);
//
//                //store the analytical run(s)
//                storeAnalyticalRuns(storageTask, analyticalRuns);
//
//                //wrap the StorageTask in a StoredTask and send it to the stored task queue
//                storedTaskProducer.sendCompletedDbTask(new CompletedDbTask(started, System.currentTimeMillis(), storageTask));
//            } catch (Exception e) {
//                LOGGER.error(e.getMessage(), e);
//                //wrap the StorageTask in a StorageError and send it to the error queue
//                storageErrorProducer.sendStorageError(new DbTaskError(storageTask, e));
//            }
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Map the persist db task onto a list of analytical runs
     *
     * @param persistDbTask the persist task containing the DataImport object
     * @return the list of analytical runs
     * @throws MappingException
     */
    private List<AnalyticalRun> mapDataImport(PersistDbTask persistDbTask) throws MappingException, IOException, ArchiveException, ClassNotFoundException {
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();

        switch (persistDbTask.getPersistMetadata().getStorageType()) {
            case PEPTIDESHAKER:
                //unpack .cps archive
                UnpackedPsDataImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerDataImport(((PeptideShakerDataImport) persistDbTask.getDataImport()));
                analyticalRuns = peptideShakerImportMapper.map(unpackedPsDataImport);
                break;
            case MAX_QUANT:
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
        for (AnalyticalRun analyticalRun : analyticalRuns) {
            analyticalRun.setCreationDate(new Date());
            analyticalRun.setModificationDate(new Date());
            //@todo find the user name by id
//            analyticalRun.setUserName(persistDbTask.getMessageId());
            analyticalRun.setStartDate(persistDbTask.getPersistMetadata().getStartDate());
            //@todo find the sample name by id
//            analyticalRun.setSample(persistDbTask.getPersistMetadata().getSample());
            analyticalRun.setInstrument(persistDbTask.getPersistMetadata().getInstrument());
            analyticalRunService.saveOrUpdate(analyticalRun);
        }
    }

}
