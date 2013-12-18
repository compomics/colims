/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.distributed.searches.controller.searches;

import com.compomics.colims.core.config.distributedconfiguration.client.DistributedProperties;
import com.compomics.colims.core.spring.ApplicationContextProvider;
import com.compomics.colims.core.distributed.storage.processing.controller.storagequeue.StorageQueue;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth
 */
@Component("searchController")
public class SearchController implements Runnable {

    @Autowired
    StorageQueue searchQueue;
    @Autowired
    SearchHandler searchHandler;

    private int port = 24568;
    private ServerSocket serverSocket;
    private final Logger LOGGER = Logger.getLogger(SearchController.class);
    private final ExecutorService threadService = Executors.newCachedThreadPool();
    private boolean disconnected = false;
    private DistributedProperties searchProperties;

    /**
     *
     * This method starts the socketListener and the StorageQueue
     */
    @Override
    public void run() {
        try {
            searchProperties = DistributedProperties.getInstance();
            this.port = searchProperties.getSearchPort();
            LOGGER.info("Booting colims search controller on port " + port);
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            LOGGER.error(ex);
        }

        LOGGER.debug("Starting Queue");
        Thread searchQueueThread = new Thread(searchQueue);
        searchQueueThread.start();

        LOGGER.debug("Accepting sockets: It's searching time!");
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
            searchQueue.disconnect();
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
                searchHandler = (SearchHandler) ApplicationContextProvider.getInstance().getApplicationContext().getBean("searchHandler");
                searchHandler.setSocket(incomingSocket);
                threadService.submit(searchHandler);
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
