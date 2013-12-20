/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.searches.controller.workers.worker;

import com.compomics.colims.distributed.searches.controller.workers.WorkerState;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

/**
 *
 * @author Kenneth
 */
public class Worker implements Comparable {

    WorkerState state = WorkerState.AVAILABLE;
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public Worker(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    public WorkerState getState() {
        return state;
    }

    public void setState(WorkerState state) {
        this.state = state;
    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    //TODO make a listening loop here that waits for returning state messages to set them in the worker?    
    
    @Override
    public int compareTo(Object o) {
        if (o instanceof Worker) {
            Worker otherTask = (Worker) o;
            if (getSocket().getPort() > otherTask.getSocket().getPort()) {
                return 1;
            } else if (getSocket().getPort() == otherTask.getSocket().getPort()) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.socket);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Worker other = (Worker) obj;
        if (!Objects.equals(this.socket.getInetAddress().getHostAddress(), other.socket.getInetAddress().getHostAddress())) {
            return false;
        }
        return true;
    }

}
