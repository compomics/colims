/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.storage;

import com.compomics.colims.core.storage.enums.StorageState;
import com.compomics.colims.core.storage.processing.storagequeue.StorageQueue;
import com.compomics.colims.core.storage.processing.storagequeue.storagetask.StorageTask;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kenneth
 */
public class DistributedStorageTest {

    private final File testTaskDbAddress = new File(System.getProperty("user.home") + "/.compomics/ColimsController/");

    public DistributedStorageTest() {
    }

    /**
     * Test of offer method, of class StorageQueue.
     */
    @Test
    public void testOfferAndRetrieve() throws IOException {
        FileUtils.deleteDirectory(testTaskDbAddress);
        System.out.println("Test offer file to store");
        StorageTask task = null;
        StorageQueue instance = StorageQueue.getInstance(testTaskDbAddress.getAbsolutePath());
        task = instance.addNewTask("myFiles/testingFile.cps", "admin1");
        StorageTask taskFromDb = instance.getTask(task.getTaskID(), true);
        assertEquals(taskFromDb.getFileLocation(), "myFiles/testingFile.cps");
        assertEquals(taskFromDb.getTaskID(), 1);
        assertEquals(taskFromDb.getState(), StorageState.WAITING);
        assertEquals(taskFromDb.getUserName(), "admin1");
        assertEquals(instance.peek().getTaskID(), 1);
    }

}
