/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.storage.processing.controller;

import com.compomics.colims.core.spring.ApplicationContextProvider;
import com.compomics.colims.core.storage.processing.controller.storagequeue.StorageQueue;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth
 */
@Component("socketListener")
public class StorageController {

    @Autowired
    StorageQueue storageQueue;
    @Autowired
    StorageHandler socketHandler;

    private int port = 24567;
    private ServerSocket serverSocket;
    private final Logger LOGGER = Logger.getLogger(StorageController.class);
    private final ExecutorService threadService = Executors.newCachedThreadPool();

    /**
     *
     * This method starts the socketListener and the StorageQueue
     */
    public void launch(int port) {
        this.port = port;
        LOGGER.info("Booting colims storage controller on port " + port);
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            LOGGER.error(ex);
        }

        LOGGER.debug("Starting Queue");
        Thread storingThread = new Thread(storageQueue);
        storingThread.start();

        LOGGER.debug("Accepting sockets: It's go time!");
        handleAllIncomingSockets();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private void handleAllIncomingSockets() {
        while (true) {
            try {
                Socket incomingSocket = serverSocket.accept();
                socketHandler = (StorageHandler) ApplicationContextProvider.getInstance().getApplicationContext().getBean("socketHandler");
                socketHandler.setSocket(incomingSocket);
                threadService.submit(socketHandler);
            } catch (IOException ex) {
                LOGGER.error(ex);
            } finally {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    LOGGER.error(ex);
                }
            }
        }
    }
}
