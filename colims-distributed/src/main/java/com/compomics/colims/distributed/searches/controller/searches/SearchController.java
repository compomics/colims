/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.searches.controller.searches;

import com.compomics.colims.distributed.storage.processing.controller.storagequeue.StorageQueue;
import com.compomics.colims.core.spring.ApplicationContextProvider;
import com.compomics.colims.distributed.config.distributedconfiguration.client.DistributedProperties;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth
 */
@Component("searchController")
public class SearchController implements Runnable {

    @Autowired
    SearchQueue searchQueue;
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
        } catch (URISyntaxException ex) {
            LOGGER.error(ex);
        }

        LOGGER.debug("Starting Search Queue");
        Thread searchQueueThread = new Thread(searchQueue);
        searchQueueThread.start();

        LOGGER.debug("Accepting sockets: It's searching time!");
        handleAllIncomingSockets();
    }

    public void disconnect() throws IOException, SQLException {
        if (serverSocket != null) {
            serverSocket.close();
        }
        searchQueue.disconnect();
        disconnected = true;
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
            }
        }
    }
}
