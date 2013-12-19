/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.distributed.searches.controller.workers;

import com.compomics.colims.core.config.distributedconfiguration.client.DistributedProperties;
import com.compomics.colims.core.distributed.searches.controller.workers.worker.Worker;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth
 */
@Component("workerController")
public class WorkerController implements Runnable {

    @Autowired
    WorkerQueue workerQueue;

    private int port = 24568;
    private ServerSocket serverSocket;
    private final Logger LOGGER = Logger.getLogger(WorkerController.class);
    private boolean disconnected = false;
    private DistributedProperties workProperties;

    /**
     *
     * This method starts the socketListener and the StorageQueue
     *
     * @param port
     */
    @Override
    public void run() {
        try {
            workProperties = DistributedProperties.getInstance();
            this.port = workProperties.getWorkerPort();
            LOGGER.info("Booting colims worker controller on port " + port);
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            LOGGER.error(ex);
        }

        LOGGER.debug("Starting Worker Queue");
        Thread workerQueueThread = new Thread(workerQueue);
        workerQueueThread.start();
        LOGGER.debug("Accepting sockets: It's working time!");
        handleAllIncomingSockets();
    }

    public void disconnect() throws IOException, SQLException {
        if (serverSocket != null) {
            serverSocket.close();
        }
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
                workerQueue.offer(new Worker(incomingSocket));
            } catch (IOException ex) {

            }
        }
    }
}
