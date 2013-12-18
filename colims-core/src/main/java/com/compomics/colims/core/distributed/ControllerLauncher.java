/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.distributed;

import com.compomics.colims.core.config.distributedconfiguration.client.DistributedProperties;
import com.compomics.colims.core.distributed.searches.controller.searches.SearchController;
import com.compomics.colims.core.distributed.searches.controller.workers.WorkerController;
import com.compomics.colims.core.distributed.storage.processing.controller.StorageController;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author Kenneth
 */
public class ControllerLauncher {

    private static Logger LOGGER = Logger.getLogger(ControllerLauncher.class);

    public static void main(String[] args) {
        try {
            new ControllerLauncher().launch();
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
    }

    public void launch() throws IOException {
        File distPropertiesFile = new ClassPathResource("distributed/config/distribute.properties").getFile();
        DistributedProperties.setPropertiesFile(distPropertiesFile);
        DistributedProperties.reload();
        LOGGER.debug("Starting threads to handle searchtasks");
        SearchController searchController = new SearchController();
        searchController.launch();
        LOGGER.debug("Starting threads to handle workers");
        WorkerController workController = new WorkerController();
        workController.launch();
        LOGGER.debug("Starting threads to handle storage requests");
        StorageController storeController = new StorageController();
        storeController.launch();
    }

}
