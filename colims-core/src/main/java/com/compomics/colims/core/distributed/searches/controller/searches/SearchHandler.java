/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.distributed.searches.controller.searches;


import com.compomics.colims.core.distributed.searches.controller.searches.searchtask.SearchTask;
import com.compomics.colims.core.distributed.storage.enums.StorageState;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("searchHandler")
@Scope("prototype")
public class SearchHandler implements Runnable {

    @Autowired
    SearchQueue searchQueue;
    private static final Logger LOGGER = Logger.getLogger(SearchHandler.class);
    private Socket socket;

    private BufferedReader in;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            socket.setKeepAlive(true);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            //read the task from the socket that just sent it
            SearchTask task = readTaskFromInputStream(inputStream);
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

    private SearchTask readTaskFromInputStream(InputStream inputStream) throws IOException {
        in = new BufferedReader(new InputStreamReader(inputStream));
        String response;
        SearchTask task = null;
        while ((response = in.readLine()) != null) {
            String[] responseArgs = response.split(">.<");
            task = searchQueue.addNewTask(
                    responseArgs[0],
                    responseArgs[1],
                    responseArgs[2],
                    responseArgs[3],
                    responseArgs[4],
                    responseArgs[5],
                    Long.parseLong(responseArgs[6])
            );
            LOGGER.debug("User :" + responseArgs[0] + " has successfully planned a search");
            break;
        }
        return task;
    }

    private void writeTaskStateToOutputStream(OutputStream outputStream, SearchTask task) throws IOException {
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
