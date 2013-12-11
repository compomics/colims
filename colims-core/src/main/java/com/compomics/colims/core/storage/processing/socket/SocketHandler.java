/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.storage.processing.socket;

import com.compomics.colims.core.storage.enums.StorageState;
import com.compomics.colims.core.storage.processing.storagequeue.StorageQueue;
import com.compomics.colims.core.storage.processing.storagequeue.storagetask.StorageTask;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth Verheggen
 */
public class SocketHandler implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(SocketHandler.class);
    private final Socket socket;
    private final StorageQueue queue = StorageQueue.getInstance();
    private BufferedReader in;

    /**
     *
     * @param incomingSocket the incoming socket that needs to be handled
     */
    public SocketHandler(Socket incomingSocket) {
        this.socket = incomingSocket;

    }

    @Override
    public void run() {
        try {
            socket.setKeepAlive(true);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            //read the task from the socket that just sent it
            StorageTask task = readTaskFromInputStream(inputStream);
            //write output from the task back to the client?
            if (task != null) {
                writeTaskStateToOutputStream(outputStream, task);
            }
        } catch (SocketException ex) {
            LOGGER.error(ex);
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
    }

    private StorageTask readTaskFromInputStream(InputStream inputStream) throws IOException {
        in = new BufferedReader(new InputStreamReader(inputStream));
        String response;
        StorageTask task = null;
        while ((response = in.readLine()) != null) {
            String[] responseArgs = response.split(">.<");
            task = queue.addNewTask(responseArgs[1], responseArgs[0]);
            LOGGER.debug("User :" + responseArgs[0] + " has successfully planned storing");
            break;
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
            in.close();
        }
        socket.close();
    }

}
