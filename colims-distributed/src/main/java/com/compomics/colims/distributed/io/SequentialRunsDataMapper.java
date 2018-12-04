package com.compomics.colims.distributed.io;

import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/**
 * This interface defines the import data contract for the different import resources.
 *
 * @param <T> the DataImport subclass
 * @author Niels Hulstaert
 */
public interface SequentialRunsDataMapper<T extends DataImport> {

    /**
     * @param dataImport      the DataImport subclass instance
     * @param fastasDirectory the FASTA DBs parent directory
     * @return
     */
    Set<String> getRunNames(T dataImport, Path fastasDirectory) throws IOException, JDOMException;

    /**
     * Map the search input and identification results.
     *
     * @param analyticalRun the analytical run to map
     * @return the mapped data (protein groups and analytical runs)
     * @throws MappingException thrown in case of an error during the mapping
     */
    MappedData mapData(String analyticalRun) throws MappingException;

    /**
     * Clear resources used during the import.
     *
     * @throws Exception in case of an exception
     */
    void clear() throws Exception;

    /**
     * Clear resources used during the import of a single run.
     *
     * @throws Exception in case of an exception
     */
    void clearForSingleRun() throws Exception;

}
