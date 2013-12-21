/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed;

import com.compomics.colims.distributed.searches.controller.searches.SearchController;
import com.compomics.colims.distributed.searches.controller.workers.WorkerController;
import com.compomics.colims.distributed.storage.processing.controller.StorageController;
import com.compomics.colims.distributed.config.distributedconfiguration.client.DistributedProperties;
import com.compomics.colims.distributed.spring.ApplicationContextProvider;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
public class ControllerLauncher {

    private static final Logger LOGGER = Logger.getLogger(ControllerLauncher.class);
    private SearchController searchController;
    private WorkerController workerController;
    private StorageController storageController;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public static void main(String[] args) {
        ApplicationContextProvider.getInstance().getApplicationContext();
        LOGGER.setLevel(Level.ERROR);
        try {
            if (parseArgs(args)) {
                new ControllerLauncher().launch();
            } else {
                LOGGER.error("Could not launch the controllers, please try again with different parameters");
            }
        } catch (IOException | URISyntaxException | ParseException ex) {
            LOGGER.error("An error has occurred : ");
            LOGGER.error(ex);
            ex.printStackTrace();
        }
    }

    public void launch() throws IOException {

        searchController = (SearchController) ApplicationContextProvider.getInstance().getApplicationContext().getBean("searchController");
        workerController = (WorkerController) ApplicationContextProvider.getInstance().getApplicationContext().getBean("workerController");
        storageController = (StorageController) ApplicationContextProvider.getInstance().getApplicationContext().getBean("storageController");

        LOGGER.info("Starting threads to handle searchtasks");
        executor.execute(searchController);
        LOGGER.info("Starting threads to handle workers");
        executor.execute(workerController);
        LOGGER.info("Starting threads to handle storage requests");
        executor.execute(storageController);

        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        LOGGER.debug("All controllers have shut down");
    }

    public void shutdownControllers() {
        executor.shutdownNow();
    }

    private static boolean parseArgs(String[] args) throws IOException, ParseException, URISyntaxException {
        boolean parseAble = true;
        DistributedProperties.getInstance().setDefaultProperties();
        DistributedProperties.reload();
        // create Options object
        Options options = new Options();
        options.addOption("se", true, "port that will listen for clients requesting to search  (default 45678)");
        options.addOption("st", true, "port that will listen for clients requesting to store  (default 45679)");
        options.addOption("wo", true, "port that will listen for new working units  (default 45680)");

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("st")) {
            DistributedProperties.getInstance().setStoragePort(Integer.parseInt(cmd.getOptionValue("storage_port")));
        }

        if (cmd.hasOption("se")) {
            DistributedProperties.getInstance().setStoragePort(Integer.parseInt(cmd.getOptionValue("search_port")));
        }

        if (cmd.hasOption("wo")) {
            DistributedProperties.getInstance().setStoragePort(Integer.parseInt(cmd.getOptionValue("worker_port")));
        }

        System.out.println("Running controllers on the following ports : ");
        System.out.println("Searches \t\t: " + DistributedProperties.getInstance().getSearchPort());
        System.out.println("Storage \t\t: " + DistributedProperties.getInstance().getStoragePort());
        System.out.println("Workers \t\t: " + DistributedProperties.getInstance().getWorkerPort());
        return parseAble;
    }

}
