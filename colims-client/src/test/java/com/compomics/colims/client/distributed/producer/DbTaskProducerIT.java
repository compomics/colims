package com.compomics.colims.client.distributed.producer;

import com.compomics.colims.client.distributed.QueueManager;
import com.compomics.colims.core.distributed.model.PersistDbTask;
import com.compomics.colims.core.distributed.model.PersistMetadata;
import com.compomics.colims.core.distributed.model.enums.PersistType;
import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.io.PeptideShakerImport;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.Instrument;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-client-context.xml", "classpath:colims-client-test-context.xml"})
public class DbTaskProducerIT {

    @Value("${distributed.queue.dbtask}")
    private String dbTaskQueueName;
    @Autowired
    private DbTaskProducer dbTaskProducer;
    @Autowired
    private QueueManager queueManager;

    /**
     * In this test a PersistDbTask message is sent to the queue. It tests if whether the message has been received and
     * afterwards if it could be deleted.
     *
     * @throws Exception because the queuemanager methods throw this
     */
    @Test
    public void testSendDbTask() throws Exception {
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

        List<File> mgfFiles = Arrays.asList(new File[]{new File("test1"), new File("test2")});
        DataImport dataImport = new PeptideShakerImport(new File("testFile"), new FastaDb(), mgfFiles);
        persistDbTask.setDataImport(dataImport);

        List<PersistDbTask> messages = queueManager.monitorQueue(dbTaskQueueName, PersistDbTask.class);
        //the queue must be empty
        Assert.assertTrue(messages.isEmpty());

        //send the test message
        dbTaskProducer.sendDbTask(persistDbTask);

        messages = queueManager.monitorQueue(dbTaskQueueName, PersistDbTask.class);
        //there should be one message on the queue
        Assert.assertEquals(1, messages.size());

        PersistMetadata storageMetaDataOnQueue = messages.get(0).getPersistMetadata();
        Assert.assertEquals(persistMetadata, storageMetaDataOnQueue);

        //remove message from queue
        queueManager.deleteMessage(dbTaskQueueName, messages.get(0).getMessageId());

        messages = queueManager.monitorQueue(dbTaskQueueName, PersistDbTask.class);
        //the queue must be empty
        Assert.assertTrue(messages.isEmpty());
    }
}
