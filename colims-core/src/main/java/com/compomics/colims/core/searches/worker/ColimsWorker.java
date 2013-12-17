/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.searches.worker;

import com.compomics.colims.core.config.distributedconfiguration.worker.WorkerProperties;
import com.compomics.colims.core.searches.respin.control.common.Respin;
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
 * @author Kenneth
 */
public class ColimsWorker {

    private static final Logger LOGGER = Logger.getLogger(ColimsWorker.class);
    private WorkerProperties workerProperties;
    private BufferedReader in;
    private PrintWriter out;
    private boolean storeAfterRun = true;

    public void launch() throws IOException {
        //load respinProperties
        File workerPropertiesFile = new ClassPathResource("distributed/config/respin.properties").getFile();
        WorkerProperties.setPropertiesFile(workerPropertiesFile);
        WorkerProperties.reload();
        workerProperties = WorkerProperties.getInstance();
        while (true) {
            try {
                Socket socket = new Socket(workerProperties.getWorkerControllerIp(), workerProperties.getWorkerControllerPort());
                try {
                    in = new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream());
                    String response;
                    while ((response = in.readLine()) != null) {
                        String[] responseArgs = response.split(">.<");
                        final String userName = responseArgs[0];
                        final String instrumentName = responseArgs[1];
                        final long sampleId = Long.parseLong(responseArgs[2]);
                        final String searchName = responseArgs[3];
                        final File mgf = new File(responseArgs[4]);
                        final File param = new File(responseArgs[5]);
                        final File fasta = new File(responseArgs[6]);
                        final File outputDir = new File(workerProperties.getStoragePath(), userName + "/" + searchName + "/");
                        if (!outputDir.exists()) {
                            outputDir.mkdirs();
                        }
                        Thread respinThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Respin respin = new Respin();
                                try {
                                    respin.launch(userName, instrumentName, sampleId, mgf, param, fasta, outputDir, searchName, out, storeAfterRun);
                                } catch (Exception ex) {
                                    LOGGER.error(ex);
                                }
                            }
                        });
                        respinThread.start();
                        break;
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
