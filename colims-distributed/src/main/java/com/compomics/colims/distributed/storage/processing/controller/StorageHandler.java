/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.storage.processing.controller;

import com.compomics.colims.distributed.spring.ApplicationContextProvider;
import com.compomics.colims.distributed.storage.processing.controller.storagequeue.StorageQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth Verheggen
 */
public class StorageHandler implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(StorageHandler.class);
    private Socket socket;

    private BufferedReader in;
    private InputStream inputStream;
    private OutputStream outputStream;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            //read the task from the socket that just sent it
            readTaskFromInputStream(inputStream);
        } catch (IOException ex) {

        }
    }

    private boolean readTaskFromInputStream(InputStream inputStream) throws IOException {
        boolean scheduled = false;
        in = new BufferedReader(new InputStreamReader(inputStream));
        String response;
        StorageQueue storageQueue = StorageQueue.getInstance();
        LOGGER.info("Awaiting information to store...");
        boolean endRecieved = false;
        while ((response = in.readLine()) != null & !endRecieved) {
            LOGGER.info("Analyzing incoming stream");
            if (response.contains(">.|")) {
                LOGGER.info("End signal recieved");
                endRecieved = true;
            } else {
                LOGGER.info("Parsing input");
                try {
                    String[] responseArgs = response.split(">.<");
                    storageQueue.addNewTask(responseArgs[1], responseArgs[0], Long.parseLong(responseArgs[2]), responseArgs[3], responseArgs[responseArgs.length-1]);
                    LOGGER.info("User :" + responseArgs[0] + " has successfully planned storing (" + (storageQueue.size() - 1) + " in front)");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        in.close();
        getSocket().close();
        LOGGER.info("Project was succesfully planned!");
        return scheduled;
    }

}
