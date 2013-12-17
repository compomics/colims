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

/**
 *
 * @author Kenneth
 */
public class StorageProperties {

    private static final PropertiesConfiguration properties = new PropertiesConfiguration();
    private static File propertiesFile;
    private static final Logger LOGGER = Logger.getLogger(SearchProperties.class);
    private static StorageProperties storageProperties;

    public static void setPropertiesFile(File propertiesFile) {
        StorageProperties.propertiesFile = propertiesFile;
    }

    public static void reload() throws FileNotFoundException {
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (ConfigurationException ex) {
            LOGGER.error(ex);
        }
    }

    private void initiate() throws IOException {

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

    private StorageProperties() {
    }

    public static StorageProperties getInstance() throws IOException {
        if (storageProperties == null) {
            storageProperties = new StorageProperties();
            storageProperties.initiate();
        }
        return storageProperties;
    }

    public void save() {
        try {
            properties.save(propertiesFile);
        } catch (ConfigurationException ex) {
            LOGGER.error(ex);
        }
    }

    private void setDefaultProperties() {
        properties.setProperty("search.ip", "127.0.0.1");
        properties.setProperty("search.port", 45679);
    }

    public String getStorageControllerIP() {
        return properties.getProperty("search.ip").toString();
    }

    public int getStorageControllerPort() {
        return (Integer) properties.getProperty("search.port");
    }
}
