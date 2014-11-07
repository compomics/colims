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
     * @param dataImport the DataImport instance
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
     * @return the mapped SearchAndValidationSettings instance
     * @throws MappingException thrown in case of an error during the mapping
     */
    SearchAndValidationSettings importSearchSettings() throws MappingException;

    /**
     * Import the QuantificationSettings.
     *
     * @return the mapped QuantificationSettings instance
     * @throws MappingException thrown in case of an error during the mapping
     */
    QuantificationSettings importQuantSettings() throws MappingException;

    /**
     * Import the search input and identification results.
     *
     * @param searchAndValidationSettings the SearchAndValidationSettings instance
     * @param quantificationSettings the QuantificationSettings instance
     * @return the list of analytical runs
     * @throws MappingException thrown in case of an error during the mapping
     */
    List<AnalyticalRun> importInputAndResults(SearchAndValidationSettings searchAndValidationSettings, QuantificationSettings quantificationSettings) throws MappingException;

}
