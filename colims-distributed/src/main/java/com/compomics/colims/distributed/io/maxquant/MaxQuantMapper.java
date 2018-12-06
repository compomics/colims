package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.distributed.io.DataMapper;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.model.AnalyticalRun;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * The DataMapper implementation for MaxQuant projects.
 *
 * @author Davy
 * @author Iain
 */
@Component("maxQuantMapper")
public class MaxQuantMapper implements DataMapper<MaxQuantImport> {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MaxQuantMapper.class);
    /**
     * The MaxQuant parent parser class.
     */
    private final MaxQuantParser maxQuantParser;

    @Autowired
    public MaxQuantMapper(MaxQuantParser maxQuantParser) {
        this.maxQuantParser = maxQuantParser;
    }

    @Override
    public void clear() {
        maxQuantParser.clear();
    }

    @Override
    public MappedData mapData(MaxQuantImport maxQuantImport, Path experimentsDirectory, Path fastasDirectory) throws MappingException {
        LOGGER.info("started mapping folder: " + maxQuantImport.getMqParFile());

        List<AnalyticalRun> analyticalRuns;
        try {
            maxQuantParser.clear();

            //parseSpectraAndPSMs the search settings
            maxQuantParser.parseSettingsAndProteins(maxQuantImport, fastasDirectory);

            //parseSpectraAndPSMs the MaxQuant files
            maxQuantParser.parseSpectraAndPSMs(maxQuantImport, null);

            analyticalRuns = maxQuantParser.getAnalyticalRuns();

            //set the storage location
            analyticalRuns.forEach(run -> run.setStorageLocation(maxQuantImport.getCombinedDirectory()));
        } catch (IOException | UnparseableException | JDOMException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException("There was a problem storing your MaxQuant data, underlying exception: ", ex);
        }

        return new MappedData(analyticalRuns, maxQuantParser.getProteinGroupSet());
    }

}
