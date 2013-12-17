/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.searches.controller;

import com.compomics.colims.core.spring.ApplicationContextProvider;
import com.compomics.colims.core.storage.processing.controller.storagequeue.StorageQueue;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth
 */
@Component("searchController")
public class SearchController {

    @Autowired
    StorageQueue searchQueue;
    @Autowired
    SearchHandler searchHandler;

    private int port = 24568;
    private ServerSocket serverSocket;
    private final Logger LOGGER = Logger.getLogger(SearchController.class);
    private final ExecutorService threadService = Executors.newCachedThreadPool();
    private boolean disconnected = false;

    /**
     *
     * This method starts the socketListener and the StorageQueue
     */
    public void launch(int port) {
        this.port = port;
        LOGGER.info("Booting colims search controller on port " + port);
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            LOGGER.error(ex);
        }

        LOGGER.debug("Starting Queue");
        Thread storingThread = new Thread(searchQueue);
        storingThread.start();

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
