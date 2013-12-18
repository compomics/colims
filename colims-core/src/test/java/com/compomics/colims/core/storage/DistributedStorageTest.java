/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.storage;

import com.compomics.colims.core.distributed.storage.enums.StorageState;
import com.compomics.colims.core.distributed.storage.processing.controller.storagequeue.StorageQueue;
import com.compomics.colims.core.distributed.storage.processing.controller.storagequeue.storagetask.StorageTask;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Kenneth
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class DistributedStorageTest {

    private final File testTaskDbAddress = new File(System.getProperty("user.home") + "/.compomics/ColimsController/");
    private static final Logger LOGGER = Logger.getLogger(DistributedStorageTest.class);
    @Autowired
    StorageQueue storageQueue;

    public DistributedStorageTest() {
    }

    @Before
    public void clearDbBefore() {
        try {
            FileUtils.deleteDirectory(testTaskDbAddress);
        } catch (IOException ex) {
       
        }
    }

    @After
    public void clearDbAfter() throws IOException, SQLException {
        try {
            FileUtils.deleteDirectory(testTaskDbAddress);
        } catch (IOException e) {
            storageQueue.disconnect();
            FileUtils.deleteDirectory(testTaskDbAddress);
        }
    }

    /**
     * Test of offer method, of class StorageQueue.
     */
    @Test
    public void testOfferAndRetrieve() throws IOException, SQLException {
        System.out.println("Test offer file to store");
        StorageTask task = null;
        task = storageQueue.addNewTask("myFiles/testingFile.cps", "admin1", 1, "instrument_1");
        StorageTask taskFromDb = storageQueue.getTask(task.getTaskID());
        storageQueue.disconnect();
        assertEquals(taskFromDb.getFileLocation(), "myFiles/testingFile.cps");
        assertEquals(taskFromDb.getTaskID(), 1);
        assertEquals(taskFromDb.getState(), StorageState.WAITING);
        assertEquals(taskFromDb.getUserName(), "admin1");
        assertEquals(taskFromDb.getInstrumentId(), "instrument_1");
        assertEquals(storageQueue.peek().getTaskID(), 1);
    }

}
