/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.searches.incoming;

import com.compomics.colims.core.storage.enums.StorageState;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth Verheggen
 */
public class ClientForSearchConnector {

    private final static Logger LOGGER = Logger.getLogger(ClientForSearchConnector.class);
    private String masterIPAddress = "127.0.0.1";
    private int masterPort = 24568;
    private StorageState state = StorageState.WAITING;
    private BufferedReader in;
    private PrintWriter out;

    /**
     *
     * @param masterIPAddress the IP-address of the storing-node (127.0.0.1 =
     * default)
     * @param masterPort the port that is listening on the storing node (24567 =
     * ClientToControllerConnector
     */
    public ClientForSearchConnector(String masterIPAddress, int masterPort) {
        this.masterIPAddress = masterIPAddress;
        this.masterPort = masterPort;
    }

    public boolean storeFile(String mgfFile, String paramFile, String fastaFile, String userName, String searchName) {
        boolean success = false;
        Socket socket = null;
        try {
            LOGGER.debug("Connecting to : " + masterIPAddress + ":" + masterPort);
            socket = new Socket(masterIPAddress, masterPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(mgfFile + ">.<" + paramFile + ">.<" + fastaFile + ">.<" + userName + ">.<" + searchName);
            out.flush();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response;
            /*while ((response = in.readLine()) != null) {
                state = StorageState.valueOf(response.split(">.<")[1].toUpperCase());
                success = !state.equals(StorageState.ERROR);
            }*/
            //TODO ELABORATE ON THIS
            success = true;
        } catch (UnknownHostException ex) {
            LOGGER.error(ex);
            success = false;
        } catch (IOException ex) {
            LOGGER.error(ex);
            success = false;
        } finally {
            //Failsave method to prevent socket from staying open  = resource-leak
            LOGGER.debug("Closing socket with master");
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    LOGGER.error(ex);
                    LOGGER.error("Forcing socket to close");
                    socket = null;
                }
                try {
                    in.close();
                } catch (IOException ex) {
                    LOGGER.error("Forcing inputstream to close");
                    if (in != null) {
                        in = null;
                    }
                }
                out.flush();
                out.close();
                if (out != null) {
                    out = null;
                }
            }
            return success;
        }
    }

}
