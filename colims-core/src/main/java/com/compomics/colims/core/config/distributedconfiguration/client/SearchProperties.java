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
public class SearchProperties implements DistributedProperties {

    private static final PropertiesConfiguration properties = new PropertiesConfiguration();
    private static File propertiesFile;
    private static final Logger LOGGER = Logger.getLogger(SearchProperties.class);
    private static SearchProperties searchProperties;

    public static void setPropertiesFile(File propertiesFile) {
        SearchProperties.propertiesFile = propertiesFile;
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

    private SearchProperties() {
    }

    public static SearchProperties getInstance() throws IOException {
        if (searchProperties == null) {
            searchProperties = new SearchProperties();
            searchProperties.initiate();
        }
        return searchProperties;
    }

    @Override
    public void save() {
        try {
            properties.save(propertiesFile);
        } catch (ConfigurationException ex) {
            LOGGER.error(ex);
        }
    }

    public void setDefaultProperties() {
        properties.setProperty("search.ip", "127.0.0.1");
        properties.setProperty("search.port", 45679);
    }

    public String getSearchControllerIP() {
        return properties.getProperty("search.ip").toString();
    }

    public int getSearchControllerPort() {
        return (Integer) properties.getProperty("search.port");
    }
}
