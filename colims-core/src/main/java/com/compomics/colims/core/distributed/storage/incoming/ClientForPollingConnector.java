/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.distributed.storage.incoming;

import com.compomics.colims.core.config.distributedconfiguration.client.DistributedProperties;
import com.compomics.colims.core.distributed.storage.enums.StorageState;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth Verheggen
 */
public class ClientForPollingConnector {

    private final static Logger LOGGER = Logger.getLogger(ClientForPollingConnector.class);
    private String masterIPAddress = "127.0.0.1";
    private int masterPort = 24567;
    private BufferedReader in;
    private PrintWriter out;

    /**
     *
     * @param masterIPAddress the IP-address of the storing-node (127.0.0.1 =
     * default)
     * @param masterPort the port that is listening on the storing node (24567 =
     * ClientToControllerConnector
     */
    public ClientForPollingConnector(String masterIPAddress, int masterPort) {
        this.masterIPAddress = masterIPAddress;
        this.masterPort = masterPort;
    }

    public ClientForPollingConnector() throws IOException {
        //load respinProperties
        this.masterIPAddress = DistributedProperties.getInstance().getControllerIP();
        this.masterPort = DistributedProperties.getInstance().getStoragePort();
    }

    /**
     *
     * @param Username the user that wants to store the tasks
     * @param fileLocation the filelocation of the file that needs to be
     * imported to colims
     * @return if the method was succesfull storing the file
     */
    public StorageState pollStateOfFile(String Username, String fileLocation) {
        StorageState state = StorageState.WAITING;
        Socket socket = null;
        try {
            LOGGER.debug("Connecting to : " + masterIPAddress + ":" + masterPort);
            socket = new Socket(masterIPAddress, masterPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Username + ">.<" + fileLocation + ">.<" + "poll_state");
            out.flush();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response;
            while ((response = in.readLine()) != null) {
                state = StorageState.valueOf(response.toUpperCase());
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
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
            return state;
        }
    }

}
