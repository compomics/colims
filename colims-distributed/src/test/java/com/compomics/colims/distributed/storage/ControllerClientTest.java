/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.storage;

import com.compomics.colims.distributed.spring.ApplicationContextProvider;
import com.compomics.colims.distributed.storage.enums.StorageType;
import com.compomics.colims.distributed.storage.incoming.ClientForStorageConnector;
import com.compomics.colims.distributed.storage.processing.controller.StorageController;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    StorageController storageController;

    private Thread listener;

    private final File testTaskDbAddress = new File(System.getProperty("user.home") + "/.compomics/ColimsController/StorageController/");
    private static final Logger LOGGER = Logger.getLogger(ControllerClientTest.class);

    public ControllerClientTest() {
    }

    @Before
    public void startListener() {
        try {
            storageController = (StorageController) ApplicationContextProvider.getInstance().getApplicationContext().getBean("storageController");
            FileUtils.deleteDirectory(testTaskDbAddress);
        } catch (IOException ex) {
            LOGGER.error(ex);
            LOGGER.debug("Could not delete file...");
        }
        listener = new Thread(storageController);
        listener.start();
    }

    @After
    public void stopListener() throws IOException, SQLException {
        storageController.disconnect();
        listener.interrupt();
        try {
            FileUtils.deleteDirectory(testTaskDbAddress);
        } catch (IOException e) {
            FileUtils.deleteDirectory(testTaskDbAddress);
        }
    }

    /**
     * Test of offer method, of class StorageQueue.
     */
    @Test
    public void testOfferAndRetrieve() throws IOException {
        System.out.println("Test communication between client and controller");
        // Assert.fail("Not yet finished");
        ClientForStorageConnector creator = new ClientForStorageConnector("127.0.0.1", 45678);
        File cpsFileToStore = new ClassPathResource("test_peptideshaker_project_3.cps").getFile();
        creator.storeFile("admin1", cpsFileToStore.getAbsolutePath(), 1, "instrument_1", StorageType.PEPTIDESHAKER);
    }

}
