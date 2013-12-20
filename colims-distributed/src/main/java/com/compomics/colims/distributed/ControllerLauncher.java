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
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author Kenneth
 */
public class ControllerLauncher {

    private static final Logger LOGGER = Logger.getLogger(ControllerLauncher.class);

    public static void main(String[] args) {
        try {
            new ControllerLauncher().launch();
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
    }
    private SearchController searchController;
    private WorkerController workerController;
    private StorageController storageController;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

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

    public void shutdownControllers(){
        executor.shutdownNow();
    }
    
    
}
