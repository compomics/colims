/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.storage;

import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.model.PeptideShakerImport;
import com.compomics.colims.core.mapper.Mapper;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.core.storage.incoming.SocketCreator;
import com.compomics.colims.core.storage.processing.colimsimport.ColimsCpsImporter;
import com.compomics.colims.core.storage.processing.socket.SocketListener;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.repository.AuthenticationBean;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
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

    public ControllerClientTest() {
    }

    /**
     * Test of offer method, of class StorageQueue.
     */
    @Test
    public void testOfferAndRetrieve() throws IOException {
        System.out.println("Test communication between client and controller");
        Thread listener = new Thread(new Runnable() {
            @Override
            public void run() {
                new SocketListener(45678).launch();
            }
        });
        listener.start();
        SocketCreator creator = new SocketCreator("127.0.0.1", 45678);
        File cpsFileToStore = new ClassPathResource("test_peptideshaker_project_2.cps").getFile();
        boolean success = creator.storeFile("admin1", cpsFileToStore.getAbsolutePath());
        listener.interrupt();
        Assert.assertTrue(success);
    }

}
