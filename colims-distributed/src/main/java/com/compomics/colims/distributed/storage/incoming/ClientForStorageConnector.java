/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.storage.incoming;

import com.compomics.colims.distributed.config.distributedconfiguration.client.DistributedProperties;
import com.compomics.colims.distributed.storage.enums.StorageState;
import com.compomics.colims.distributed.storage.enums.StorageType;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth Verheggen
 */
public class ClientForStorageConnector {

    private final static Logger LOGGER = Logger.getLogger(ClientForStorageConnector.class);
    private static final String DELIMITER = ">.<";
    private static final String END_DELIMITER = ">.|";
    private String masterIPAddress = "127.0.0.1";
    private int masterPort = 24567;
    private StorageState state = StorageState.WAITING;
    private PrintWriter out;

    /**
     *
     * @param masterIPAddress the IP-address of the storing-node (127.0.0.1 =
     * default)
     * @param masterPort the port that is listening on the storing node (24567 =
     * ClientToControllerConnector
     */
    public ClientForStorageConnector(String masterIPAddress, int masterPort) {
        this.masterIPAddress = masterIPAddress;
        this.masterPort = masterPort;
    }

    public ClientForStorageConnector() throws IOException, URISyntaxException {
        this.state = StorageState.WAITING;
        //load respinProperties
        this.masterIPAddress = DistributedProperties.getInstance().getControllerIP();
        this.masterPort = DistributedProperties.getInstance().getStoragePort();
    }

    /**
     *
     * @param Username the user that wants to store the tasks
     * @param fileLocation the filelocation of the file that needs to be
     * imported to colims
     * @param sampleID
     * @param instrumentName
     * @param type
     * @return if the method was succesfull storing the file
     */
    public void storeFile(String Username, String fileLocation, long sampleID, String instrumentName, StorageType type) {
        boolean success = false;
        Socket socket = null;
        try {
            LOGGER.debug("Connecting to : " + masterIPAddress + ":" + masterPort);
            socket = new Socket(masterIPAddress, masterPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Username + DELIMITER
                    + fileLocation + DELIMITER
                    + sampleID + DELIMITER
                    + instrumentName + DELIMITER
                    + type.toString());
            out.flush();
            out.println(END_DELIMITER);
            out.flush();
        } catch (UnknownHostException ex) {
            LOGGER.error(ex);
            success = false;
        } catch (IOException ex) {
            LOGGER.error(ex);
            success = false;
        } finally {
            //Failsave method to prevent socket from staying open = resource-leak
            LOGGER.debug("Closing socket with master");
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    LOGGER.error(ex);
                    LOGGER.error("Forcing socket to close");
                    socket = null;
                }
                out.flush();
                out.close();
                if (out != null) {
                    out = null;
                }
            }
        }
    }

}
