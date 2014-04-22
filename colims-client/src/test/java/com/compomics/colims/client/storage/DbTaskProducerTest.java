package com.compomics.colims.client.storage;

import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerDataImport;
import com.compomics.colims.distributed.model.PersistMetadata;
import com.compomics.colims.distributed.model.PersistDbTask;
import com.compomics.colims.distributed.model.enums.PersistType;
import com.compomics.colims.model.Instrument;
import java.util.Date;
import java.util.List;
import javax.jms.JMSException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-client-context.xml", "classpath:colims-client-test-context.xml"})
public class DbTaskProducerTest {
    
    @Value("${distributed.queue.dbtask}")
    private String dbTaskQueueName;
    @Autowired
    private DbTaskProducer storageTaskProducer;
    @Autowired
    private QueueManager queueManager;
    
    @Test
    public void testSendStorageTaskMessage() throws JMSException, Exception {
        final PersistDbTask persistDbTask = new PersistDbTask();
        
        persistDbTask.setEnitityId(1L);
        persistDbTask.setSubmissionTimestamp(Long.MIN_VALUE);
        persistDbTask.setSubmissionTimestamp(System.currentTimeMillis());
        
        PersistMetadata persistMetadata = new PersistMetadata();
        persistMetadata.setDescription("test description");
        persistMetadata.setStorageType(PersistType.PEPTIDESHAKER);
        persistMetadata.setInstrument(new Instrument("test instrument"));
        persistMetadata.setStartDate(new Date());
        persistDbTask.setPersistMetadata(persistMetadata);
        
        DataImport dataImport = new PeptideShakerDataImport(null, null, null);        
        persistDbTask.setDataImport(dataImport);
        
        List<PersistDbTask> messages = queueManager.monitorQueue(dbTaskQueueName);
        //the queue must be empty
        Assert.assertTrue(messages.isEmpty());
        
        //send the test message
        storageTaskProducer.sendDbTask(persistDbTask);
        
        messages = queueManager.monitorQueue(dbTaskQueueName);
        //there should be one message on the queue
        Assert.assertEquals(1, messages.size());
        
        PersistMetadata storageMetaDataOnQueue = messages.get(0).getPersistMetadata();
        Assert.assertEquals(persistMetadata, storageMetaDataOnQueue);
        
        //remove message from queue        
        queueManager.deleteMessage(dbTaskQueueName, messages.get(0).getMessageId());
        
        messages = queueManager.monitorQueue(dbTaskQueueName);
        //the queue must be empty
        Assert.assertTrue(messages.isEmpty());
    }
    
}
