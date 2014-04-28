package com.compomics.colims.client;

import java.util.Map;
import org.springframework.core.env.PropertySource;

/**
 *
 * @author Niels Hulstaert
 */
public class DatabasePropertySource extends PropertySource<String> {

    private final Map<String, String> databaseProperties;

    /**
     *
     * @param databaseProperties
     */
    public DatabasePropertySource(Map<String, String> databaseProperties) {
        super("custom");

        this.databaseProperties = databaseProperties;
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public String getProperty(String name) {
        if (databaseProperties.containsKey(name)) {
            return databaseProperties.get(name);
        }
        return null;
    }

    /**
     * Reset the properties after use.
     */
    public void reset() {
        databaseProperties.clear();
    }

}
