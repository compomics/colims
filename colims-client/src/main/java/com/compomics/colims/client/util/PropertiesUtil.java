
package com.compomics.colims.client.util;

import com.compomics.colims.core.util.ResourceUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.core.io.Resource;

/**
 *
 * @author demet
 */
public class PropertiesUtil {

    /**
     * Private constructor to prevent initialization.
     */
    private PropertiesUtil() {
    }
    
    /**
     * Parse properties file.
     * @param path
     * @return properties configuration object.
     * @throws IOException
     * @throws ConfigurationException 
     */
    public static PropertiesConfiguration parsePropertiesFile(String path) throws IOException, ConfigurationException {
        
        Resource resource = ResourceUtils.getResourceByRelativePath(path);
        if(resource == null){
            throw new FileNotFoundException("Properties file was not found.");
        }
        PropertiesConfiguration config = new PropertiesConfiguration(resource.getFile());
        
        return config;
    }
    
    /**
     * Update properties file (Add a new property).
     * @param config
     * @param key
     * @param value
     * @return properties configuration object.
     * @throws ConfigurationException
     * @throws IOException 
     */
    public static PropertiesConfiguration addProperty(PropertiesConfiguration config, String key, Object value) throws ConfigurationException, IOException {

        config.setProperty(key, value);
        config.save();
        
        return config;
    }
}