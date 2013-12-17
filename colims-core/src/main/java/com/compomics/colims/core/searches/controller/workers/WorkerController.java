/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.searches.controller.workers;

import com.compomics.colims.core.searches.controller.workers.worker.Worker;
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
@Component("workController")
public class WorkerController {

    @Autowired
    WorkerQueue workerQueue;

    private int port = 24568;
    private ServerSocket serverSocket;
    private final Logger LOGGER = Logger.getLogger(WorkerController.class);
    private boolean disconnected = false;

    /**
     *
     * This method starts the socketListener and the StorageQueue
     * @param port
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
