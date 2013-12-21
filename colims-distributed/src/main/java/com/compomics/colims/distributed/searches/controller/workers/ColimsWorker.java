/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.searches.controller.workers;

import com.compomics.colims.distributed.ControllerLauncher;
import com.compomics.colims.distributed.config.distributedconfiguration.client.DistributedProperties;
import com.compomics.colims.distributed.searches.respin.control.common.Respin;
import com.compomics.colims.distributed.spring.ApplicationContextProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
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

    public static void main(String[] args) throws IOException, URISyntaxException {
        ApplicationContextProvider.getInstance().getApplicationContext();
        LOGGER.setLevel(Level.ERROR);
        try {
            if (parseArgs(args)) {
                new ColimsWorker().launch();
            } else {
                LOGGER.error("Could not launch the controllers, please try again with different parameters");
            }
        } catch (IOException | URISyntaxException | ParseException ex) {
            LOGGER.error("An error has occurred : ");
            LOGGER.error(ex);
            ex.printStackTrace();
        }
    }

    private static boolean parseArgs(String[] args) throws IOException, ParseException, URISyntaxException {
        boolean parseAble = true;
        DistributedProperties.getInstance().setDefaultProperties();
        DistributedProperties.reload();
        // create Options object
        Options options = new Options();
        options.addOption("wo", true, "port that new working units will connect to (default 45680)");

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("wo")) {
            DistributedProperties.getInstance().setStoragePort(Integer.parseInt(cmd.getOptionValue("worker_port")));
        }
        if (cmd.hasOption("ci")) {
            DistributedProperties.getInstance().setControllerIP(Integer.parseInt(cmd.getOptionValue("worker_port")));
        }
        
        System.out.println("Connecting to " + DistributedProperties.getInstance().getControllerIP() + " on port " + DistributedProperties.getInstance().getWorkerPort());
        return parseAble;
    }

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
