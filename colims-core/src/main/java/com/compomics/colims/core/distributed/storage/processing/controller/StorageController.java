/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.distributed.storage.processing.controller;

import com.compomics.colims.core.config.distributedconfiguration.client.StorageProperties;
import com.compomics.colims.core.config.distributedconfiguration.worker.WorkerProperties;
import com.compomics.colims.core.spring.ApplicationContextProvider;
import com.compomics.colims.core.distributed.storage.processing.controller.storagequeue.StorageQueue;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth
 */
@Component("storageController")
public class StorageController {

    @Autowired
    StorageQueue storageQueue;
    @Autowired
    StorageHandler storageHandler;

    private int port = 24567;
    private ServerSocket serverSocket;
    private final Logger LOGGER = Logger.getLogger(StorageController.class);
    private final ExecutorService threadService = Executors.newCachedThreadPool();
    private boolean disconnected = false;
    private StorageProperties workProperties;

    /**
     *
     * This method starts the socketListener and the StorageQueue
     */
    public void launch() throws IOException {
        File storagePropertiesFile = new ClassPathResource("distributed/config/storage.properties").getFile();
        StorageProperties.setPropertiesFile(storagePropertiesFile);
        StorageProperties.reload();
        workProperties = StorageProperties.getInstance();
        this.port = workProperties.getStorageControllerPort();
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

    public void disconnect() {
        disconnected = true;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
        try {
            storageQueue.disconnect();
        } catch (SQLException ex) {
            LOGGER.error(ex);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private void handleAllIncomingSockets() {
        while (!disconnected) {
            try {
                Socket incomingSocket = serverSocket.accept();
                storageHandler = (StorageHandler) ApplicationContextProvider.getInstance().getApplicationContext().getBean("storageHandler");
                storageHandler.setSocket(incomingSocket);
                threadService.submit(storageHandler);
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
        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                LOGGER.error(ex);
                serverSocket = null;
            }
        }
    }
}
