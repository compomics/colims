package com.compomics.colims.distributed.io;

import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;

import java.nio.file.Path;

/**
 * This interface defines the import data contract for the different import resources.
 *
 * @param <T> the DataImport subclass
 * @author Niels Hulstaert
 */
public interface DataMapper<T extends DataImport> {

    /**
     * Map the search input and identification results.
     *
     * @param dataImport           The DataImport subclass instance
     * @param experimentsDirectory the experiments parent directory
     * @param fastasDirectory      the FASTA DBs parent directory
     * @return the mapped data (protein groups and analytical runs)
     * @throws MappingException thrown in case of an error during the mapping
     */
    MappedData mapData(T dataImport, Path experimentsDirectory, Path fastasDirectory) throws MappingException;

    /**
     * Clear resources used during the import.
     *
     * @throws java.lang.Exception in case of an exception
     */
    void clear() throws Exception;

}
