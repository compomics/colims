/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.distributed.searches.controller.searches.incoming;

import com.compomics.colims.core.config.distributedconfiguration.client.SearchProperties;
import com.compomics.colims.core.distributed.searches.respin.model.enums.RespinState;
import com.compomics.colims.core.distributed.storage.enums.StorageState;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author Kenneth Verheggen
 */
public class ClientForSearchConnector {

    private final static Logger LOGGER = Logger.getLogger(ClientForSearchConnector.class);
    private String masterIPAddress = "127.0.0.1";
    private int masterPort = 24568;
    private RespinState state;
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
        this.state = RespinState.NEW;
        this.masterIPAddress = masterIPAddress;
        this.masterPort = masterPort;
    }

    public ClientForSearchConnector() throws IOException {
        this.state = RespinState.NEW;
        //load respinProperties
        File searchPropertiesFile = new ClassPathResource("distributed/config/search.properties").getFile();
        SearchProperties.setPropertiesFile(searchPropertiesFile);
        SearchProperties.reload();
        this.masterIPAddress = SearchProperties.getInstance().getSearchControllerIP();
        this.masterPort = SearchProperties.getInstance().getSearchControllerPort();
    }

    public boolean storeFile(String mgfFile, String paramFile, String fastaFile, String userName, String searchName, String instrumentName, long sampleID) {
        boolean success = false;
        Socket socket = null;
        try {
            LOGGER.debug("Connecting to : " + masterIPAddress + ":" + masterPort);
            socket = new Socket(masterIPAddress, masterPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(mgfFile + ">.<"
                    + paramFile + ">.<"
                    + fastaFile + ">.<"
                    + userName + ">.<"
                    + searchName + ">.<"
                    + instrumentName + ">.<"
                    + sampleID);
            out.flush();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response;
            while ((response = in.readLine()) != null & !success) {
                state = RespinState.valueOf(response.split(">.<")[1].toUpperCase());
                success = !state.equals(StorageState.ERROR);
            }
            //TODO ELABORATE ON THIS
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
        }
        return success;
    }

}
