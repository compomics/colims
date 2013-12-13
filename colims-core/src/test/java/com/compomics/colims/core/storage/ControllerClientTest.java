/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.storage;

import com.compomics.colims.core.storage.incoming.ClientToControllerConnector;
import com.compomics.colims.core.storage.processing.controller.StorageController;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Kenneth
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})

public class ControllerClientTest {

    @Autowired
    StorageController storageController;

    private Thread listener;

    public ControllerClientTest() {
    }

    @Before
    public void startListener() {
        listener = new Thread(new Runnable() {
            @Override
            public void run() {
                storageController.launch(45678);
            }
        });
        listener.start();
    }

    @After
    public void stopListener() {
        listener.interrupt();
    }

    /**
     * Test of offer method, of class StorageQueue.
     */
    @Test
    public void testOfferAndRetrieve() throws IOException {
        System.out.println("Test communication between client and controller");
        ClientToControllerConnector creator = new ClientToControllerConnector("127.0.0.1", 45678);
        File cpsFileToStore = new ClassPathResource("test_peptideshaker_project_2.cps").getFile();
        boolean success = creator.storeFile("admin1", cpsFileToStore.getAbsolutePath(), 1, "instrument_1");
        Assert.assertTrue(success);
    }

}
