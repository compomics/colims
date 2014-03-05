package com.compomics.colims.client.storage;

import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerDataImport;
import com.compomics.colims.distributed.model.StorageMetadata;
import com.compomics.colims.distributed.model.StorageTask;
import com.compomics.colims.distributed.model.enums.StorageType;
import com.compomics.colims.model.Sample;
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
public class StorageTaskProducerTest {
    
    @Value("${distributed.queue.storage}")
    private String storageQueueName;
    @Autowired
    private StorageTaskProducer storageTaskProducer;
    @Autowired
    private QueueMonitor queueMonitor;
    
    @Test
    public void testSendStorageTaskMessage() throws JMSException {
        final StorageTask storageTask = new StorageTask();
        
        StorageMetadata storageMetadata = new StorageMetadata();
        storageMetadata.setDescription("test description");
        storageMetadata.setSample(new Sample("test sample name"));
        storageMetadata.setStorageType(StorageType.PEPTIDESHAKER);
        storageMetadata.setSubmissionTimestamp(System.currentTimeMillis());
        storageMetadata.setUserName("test user");        
        storageTask.setStorageMetadata(storageMetadata);
        
        DataImport dataImport = new PeptideShakerDataImport(null, null);        
        storageTask.setDataImport(dataImport);
        
        List<StorageMetadata> messages = queueMonitor.monitorStorageQueue(storageQueueName);
        //the queue must be empty
        Assert.assertTrue(messages.isEmpty());
        
        //send the test message
        storageTaskProducer.sendStorageTask(storageTask);
        
        messages = queuemonitorStorageQueueMessages(storageQueueName);
        //there should be one message on the queue
        Assert.assertEquals(1, messages.size());
        
        StorageMetadata storageMetaDataOnQueue = messages.get(0);
        Assert.assertEquals(storageMetadata, storageMetaDataOnQueue);
    }
    
}
