package com.compomics.colims.core.io;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.SearchAndValidationSettings;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface DataImporter {

    /**
     * Init the import.
     *
     * @param dataImport
     */
    void initImport(DataImport dataImport);
    
    /**
     * Clear resources used during the import.
     * 
     */
    void clear();

    /**
     * Import the SearchAndValidationSettings.
     *
     * @return
     * @throws com.compomics.colims.core.io.MappingException
     */
    SearchAndValidationSettings importSearchSettings() throws MappingException;

    /**
     * Import the QuantificationSettings.
     *
     * @return
     * @throws com.compomics.colims.core.io.MappingException
     */
    QuantificationSettings importQuantSettings() throws MappingException;

    /**
     * Import the search input and identification results.
     *
     * @param searchAndValidationSettings
     * @param quantificationSettings
     * @return
     * @throws com.compomics.colims.core.io.MappingException
     */
    List<AnalyticalRun> importInputAndResults(SearchAndValidationSettings searchAndValidationSettings, QuantificationSettings quantificationSettings) throws MappingException;

}
