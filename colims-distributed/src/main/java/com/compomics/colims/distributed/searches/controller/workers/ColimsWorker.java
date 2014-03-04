/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.searches.controller.workers;

import com.compomics.colims.distributed.config.distributedconfiguration.client.DistributedProperties;
import com.compomics.colims.distributed.searches.respin.control.common.Respin;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth
 */
public class ColimsWorker {

    private static final Logger LOGGER = Logger.getLogger(ColimsWorker.class);
    private DistributedProperties workerProperties;
    private BufferedReader in;
    private PrintWriter out;
    private boolean storeAfterRun = true;

    public void launch() throws IOException, URISyntaxException {
        //load respinProperties

        workerProperties = DistributedProperties.getInstance();
        while (true) {
            try {
                Socket socket = new Socket(workerProperties.getControllerIP(), workerProperties.getWorkerPort());
                try {
                    in = new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream());
                    String response;
                    while ((response = in.readLine()) != null) {
                        if (response.equals(">.|")) {
                            break;
                        } else {
                            String[] responseArgs = response.split(">.<");
                            final String userName = responseArgs[0];
                            final String instrumentName = responseArgs[1];
                            final long sampleId = Long.parseLong(responseArgs[2]);
                            final String searchName = responseArgs[3];
                            final File mgf = new File(responseArgs[4]);
                            final File param = new File(responseArgs[5]);
                            final File fasta = new File(responseArgs[6]);
                            final File outputDir = new File(workerProperties.getStoragePath() + "/" + userName + "/" + searchName + "/");
                            if (!outputDir.exists()) {
                                outputDir.mkdirs();
                            }
                            Thread respinThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Respin respin = new Respin();
                                    try {
                                        respin.launch(userName, instrumentName, sampleId, mgf, param, fasta, outputDir, searchName, storeAfterRun);
                                    } catch (Exception ex) {
                                        LOGGER.error(ex);
                                    }
                                }
                            });
                            respinThread.start();
                        }
                    }
                } catch (IOException | NumberFormatException ex) {
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
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException ex) {
                    LOGGER.error(ex);
                }
            } catch (Exception e) {
                LOGGER.error("Something went wrong, trying to reconnect...");
            } finally {
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException ex) {
                    LOGGER.error(ex);
                }
            }
        }
    }
}
