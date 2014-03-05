package com.compomics.colims.distributed.consumer;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.MaxQuantDataImport;
import com.compomics.colims.core.io.maxquant.MaxQuantImportMapper;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerDataImport;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImportMapper;
import com.compomics.colims.distributed.model.StorageError;
import com.compomics.colims.distributed.model.StorageTask;
import com.compomics.colims.distributed.producer.StorageErrorProducer;
import com.compomics.colims.model.AnalyticalRun;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("storageTaskConsumer")
public class StorageTaskConsumer implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(StorageTaskConsumer.class);

    @Autowired
    private StorageErrorProducer storageErrorProducer;
    @Autowired
    private PeptideShakerImportMapper peptideShakerImportMapper;
    @Autowired
    private MaxQuantImportMapper maxQuantImportMapper;

    /**
     * Implementation of <code>MessageListener</code>.
     *
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        try {
            ActiveMQObjectMessage objectMessage = (ActiveMQObjectMessage) message;
            StorageTask storageTask = (StorageTask) objectMessage.getObject();

            LOGGER.info("Received storage task message of type " + storageTask.getStorageMetadata().getStorageType().userFriendlyName());

            try {
                //map the task
                List<AnalyticalRun> analyticalRuns = mapDataImport(storageTask);
            } catch (MappingException e) {
                LOGGER.error(e.getMessage(), e);
                //wrap the StorageTask in a StorageError and send it to the error queue
                storageErrorProducer.sendStorageError(new StorageError(storageTask, e));
            }
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Map the storage task onto a list of analytical runs
     *
     * @param storageTask the storage taks containing the DataImport object
     * @return the list of analytical runs
     * @throws MappingException
     */
    private List<AnalyticalRun> mapDataImport(StorageTask storageTask) throws MappingException {
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();

        switch (storageTask.getStorageMetadata().getStorageType()) {
            case PEPTIDESHAKER:
                analyticalRuns = peptideShakerImportMapper.map((PeptideShakerDataImport) storageTask.getDataImport());
                break;
            case MAX_QUANT:
                analyticalRuns = maxQuantImportMapper.map((MaxQuantDataImport) storageTask.getDataImport());
                break;
            default:
                break;
        }

        return analyticalRuns;
    }

}
