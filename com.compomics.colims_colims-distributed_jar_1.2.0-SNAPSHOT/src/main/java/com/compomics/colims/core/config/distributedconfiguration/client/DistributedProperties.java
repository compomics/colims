/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.config.distributedconfiguration.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author Kenneth
 */
public class DistributedProperties {

    private static final PropertiesConfiguration properties = new PropertiesConfiguration();
    private static File propertiesFile;
    private static final Logger LOGGER = Logger.getLogger(DistributedProperties.class);
    private static DistributedProperties searchProperties;

    public static void setPropertiesFile(File propertiesFile) {
        DistributedProperties.propertiesFile = propertiesFile;
    }

    public static void reload() throws FileNotFoundException {
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (ConfigurationException ex) {
            LOGGER.error(ex);
        }
    }

    private void initiate() throws IOException {
        propertiesFile = new ClassPathResource("distributed/config/distribute.properties").getFile();
        if (!propertiesFile.exists()) {
            propertiesFile.getParentFile().mkdirs();
            propertiesFile.createNewFile();
            setDefaultProperties();
        } else {
            try {
                properties.load(new FileInputStream(propertiesFile));
            } catch (ConfigurationException ex) {
                LOGGER.error(ex);
            }
        }
    }

    private DistributedProperties() {
    }

    public static DistributedProperties getInstance() throws IOException {
        if (searchProperties == null) {
            searchProperties = new DistributedProperties();
            searchProperties.initiate();
        }
        return searchProperties;
    }

    public void save() {
        try {
            properties.save(propertiesFile);
        } catch (ConfigurationException ex) {
            LOGGER.error(ex);
        }
    }

    /**/
    public void setDefaultProperties() {
        properties.setProperty("master.ip", "127.0.0.1");
        properties.setProperty("master.search.port", 45679);
        properties.setProperty("master.storage.port", 45678);
        properties.setProperty("master.worker.port", 45680);
        properties.setProperty("worker.storage.path", "C:/Users/Kenneth/.compomics/tempExample/");
        save();
    }

    public String getControllerIP() {
        return properties.getString("master.ip").toString();
    }

    public int getSearchPort() {
        return properties.getInt("master.search.port");
    }

    public int getStoragePort() {
        return properties.getInt("master.storage.port");
    }

    public int getWorkerPort() {
        return properties.getInt("master.worker.port");
    }

    public String getStoragePath() {
        return properties.getString("worker.storage.path");
    }
}
