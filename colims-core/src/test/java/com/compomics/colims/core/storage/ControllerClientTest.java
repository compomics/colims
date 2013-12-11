/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.storage;

import com.compomics.colims.core.storage.incoming.SocketCreator;
import com.compomics.colims.core.storage.processing.socket.SocketListener;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Kenneth
 */
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
        boolean success = creator.storeFile("admin1", "myFiles/testingFile.cps");
        listener.interrupt();
        Assert.assertTrue(success);
    }

}
