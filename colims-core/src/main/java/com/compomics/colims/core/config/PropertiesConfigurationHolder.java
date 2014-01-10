package com.compomics.colims.core.config;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.compomics.colims.core.util.ResourceUtils;

/**
 * Created by IntelliJ IDEA. User: niels Date: 9/11/11 Time: 13:41 To change
 * this template use File | Settings | File Templates.
 */
public final class PropertiesConfigurationHolder extends PropertiesConfiguration {

    private static final Logger LOGGER = Logger.getLogger(PropertiesConfigurationHolder.class);
    private static PropertiesConfigurationHolder ourInstance;

    static {
        try {
            Resource propertiesResource = ResourceUtils.getResourceByRelativePath("colims-core.properties");
            ourInstance = new PropertiesConfigurationHolder(propertiesResource);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (ConfigurationException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Gets the PropertiesConfiguration instance
     *
     * @return the PropertiesConfigurationHolder instance
     */
    public static PropertiesConfigurationHolder getInstance() {
        return ourInstance;
    }

    private PropertiesConfigurationHolder(final Resource propertiesResource) throws ConfigurationException, IOException {
        super(propertiesResource.getURL());
    }
}
