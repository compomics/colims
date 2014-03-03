/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.storage.processing.controller;

import com.compomics.colims.distributed.storage.enums.StorageState;
import com.compomics.colims.distributed.storage.processing.controller.storagequeue.StorageQueue;
import com.compomics.colims.distributed.storage.processing.controller.storagequeue.storagetask.StorageTask;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("storageHandler")
@Scope("prototype")
public class StorageHandler implements Runnable {

    @Autowired
    StorageQueue storageQueue;
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
            StorageTask task = readTaskFromInputStream(inputStream);
            //write output from the task back to the client?
            if (task != null && !socket.isClosed() && !socket.isOutputShutdown()) {
                writeTaskStateToOutputStream(outputStream, task);
            }
        } catch (IOException ex) {

        }
    }

    private StorageTask readTaskFromInputStream(InputStream inputStream) throws IOException {
        in = new BufferedReader(new InputStreamReader(inputStream));
        String response;
        StorageTask task = null;
        while ((response = in.readLine()) != null) {
            String[] responseArgs = response.split(">.<");
            //if the last item is a poll_state statement, send back the state of the taskID
            if (responseArgs[responseArgs.length].equalsIgnoreCase("poll_state")) {
                String userID = responseArgs[0];
                String fileLocation = responseArgs[1];
                StorageState state;
                try {
                    state = storageQueue.getTask(userID, fileLocation).getState();
                    writeTaskStateToOutputStream(outputStream, state);
                } catch (SQLException ex) {
                    LOGGER.error(ex);
                    break;
                }
            } else {
                task = storageQueue.addNewTask(responseArgs[1], responseArgs[0], Long.parseLong(responseArgs[2]), responseArgs[3],responseArgs[responseArgs.length]);
                LOGGER.debug("User :" + responseArgs[0] + " has successfully planned storing");
                break;
            }
        }
        return task;
    }

    private void writeTaskStateToOutputStream(OutputStream outputStream, StorageTask task) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            while (socket.isConnected()) {
                out.write(task.getTaskID() + ">.<" + task.getState());
                out.newLine();
                out.flush();
                if (task.getState().equals(StorageState.STORED) || task.getState().equals(StorageState.ERROR)) {
                    break;
                }
            }
        }
    }

    private void writeTaskStateToOutputStream(OutputStream outputStream, StorageState state) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            while (socket.isConnected()) {
                out.write(state.toString());
                out.newLine();
                out.flush();
            }
        }
    }

}
