/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.storage.processing.controller.storagequeue;

import com.compomics.colims.distributed.spring.ApplicationContextProvider;
import com.compomics.colims.distributed.storage.model.enums.StorageType;
import com.compomics.colims.distributed.storage.processing.controller.storagequeue.storagetask.StorageTask;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kenneth
 */
public class StorageQueueTest {

    public StorageQueueTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of offer method, of class StorageQueue.
     */
    @Test
    public void testOfferAndRetrieve() throws SQLException {
        System.out.println("offerAndRetrieve");
        StorageTask task = new StorageTask(1, "test.cps", "admin", 1, "test", StorageType.PEPTIDESHAKER);
        StorageQueue instance =StorageQueue.getInstance();
        instance.clear();
        long taskID = instance.offerAndGetTaskID(task);
        StorageTask result = instance.getTask(taskID);
        assertEquals("test.cps", result.getFileLocation());
        assertEquals("admin", result.getUserName());
        assertEquals(1, instance.size());
    }
}
