package com.compomics.colims.distributed.io;

import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import org.jdom2.JDOMException;

/**
 * This interface defines the import data contract for the different import resources.
 *
 * @author Niels Hulstaert
 * @param <T> the DataImport subclass
 */
public interface DataMapper<T extends DataImport> {

    /**
     * Map the search input and identification results.
     *
     * @param dataImport The DataImport subclass instance
     * @return the mapped data (protein groups and analytical runs)
     * @throws MappingException thrown in case of an error during the mapping
     */
    MappedData mapData(T dataImport) throws MappingException;

    /**
     * Clear resources used during the import.
     * @throws java.lang.Exception in case of an exception
     */
    void clear() throws Exception;

}
