/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed;

import com.compomics.colims.distributed.searches.controller.searches.SearchController;
import com.compomics.colims.distributed.searches.controller.workers.WorkerController;
import com.compomics.colims.distributed.storage.processing.controller.StorageController;
import com.compomics.colims.core.spring.ApplicationContextProvider;
import com.compomics.colims.distributed.config.distributedconfiguration.client.DistributedProperties;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.xmlpull.v1.XmlPullParserException;

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
        try {
            if (parseArgs(args)) {
                new ControllerLauncher().launch();
            } else {
                LOGGER.error("Could not launch the controllers, please try again with different parameters");
            }
        } catch (IOException | ParseException | XmlPullParserException ex) {
            LOGGER.error("An error has occurred : ");
            LOGGER.error(ex);
        }
    }

    public void launch() throws IOException {
        File distPropertiesFile = new ClassPathResource("distributed/config/distribute.properties").getFile();
        DistributedProperties.setPropertiesFile(distPropertiesFile);
        DistributedProperties.reload();

        searchController = (SearchController) ApplicationContextProvider.getInstance().getApplicationContext().getBean("searchController");
        workerController = (WorkerController) ApplicationContextProvider.getInstance().getApplicationContext().getBean("workerController");
        storageController = (StorageController) ApplicationContextProvider.getInstance().getApplicationContext().getBean("storageController");

        LOGGER.debug("Starting threads to handle searchtasks");
        executor.execute(searchController);
        LOGGER.debug("Starting threads to handle workers");
        executor.execute(workerController);
        LOGGER.debug("Starting threads to handle storage requests");
        executor.execute(storageController);

        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        LOGGER.debug("All controllers have shut down");
    }

    public void shutdownControllers() {
        executor.shutdownNow();
    }

    private static boolean parseArgs(String[] args) throws ParseException, XmlPullParserException, IOException {
        boolean parseAble = true;
        File distPropertiesFile = new ClassPathResource("distributed/config/distribute.properties").getFile();
        DistributedProperties.setPropertiesFile(distPropertiesFile);
        DistributedProperties.reload();

        // create Options object
        Options options = new Options();
        options.addOption("storage_port", true, "port that will listen for cleints requesting to store");
        options.addOption("search_port", true, "port that will listen for clients requesting tosearch");
        options.addOption("worker_port", true, "port that will listen for new working units");

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("storage_port")) {
            DistributedProperties.getInstance().setStoragePort(Integer.parseInt(cmd.getOptionValue("storage_port")));
        } else {
            LOGGER.debug("-worker_port \t\t The storage port is a mandatory parameter");
            parseAble = false;
        }

        if (cmd.hasOption("search_port")) {
            DistributedProperties.getInstance().setStoragePort(Integer.parseInt(cmd.getOptionValue("search_port")));
        } else {
            LOGGER.debug("-worker_port \t\t The search port is a mandatory parameter");
            parseAble = false;
        }

        if (cmd.hasOption("worker_port")) {
            DistributedProperties.getInstance().setStoragePort(Integer.parseInt(cmd.getOptionValue("worker_port")));
        } else {
            LOGGER.debug("-worker_port \t\t The worker port is a mandatory parameter");
            parseAble = false;
        }
        return parseAble;
    }

}
