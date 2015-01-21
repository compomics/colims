package com.compomics.colims.core.io;

import com.compomics.colims.model.AnalyticalRun;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
public interface DataImporter<T extends DataImport> {

    /**
     * Clear resources used during the import.
     */
    void clear() throws Exception;

    /**
     * Import the search input and identification results.
     *
     * @param dataImport The DataImport subclass instance
     * @return the list of analytical runs
     * @throws MappingException thrown in case of an error during the mapping
     */
    List<AnalyticalRun> importData(T dataImport) throws MappingException;

}
