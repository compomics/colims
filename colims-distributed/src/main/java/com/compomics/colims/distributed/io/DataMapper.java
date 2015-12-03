package com.compomics.colims.distributed.io;

import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;

/**
 * This interface defines the import data contract for the different import resources.
 *
 * @author Niels Hulstaert
 */
public interface DataMapper<T extends DataImport> {

    /**
     * Map the search input and identification results.
     *
     * @param dataImport The DataImport subclass instance
     * @return the list of analytical runs
     * @throws MappingException thrown in case of an error during the mapping
     */
    MappedData mapData(T dataImport) throws MappingException;

    /**
     * Clear resources used during the import.
     */
    void clear() throws Exception;

}
