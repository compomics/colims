package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.distributed.io.DataMapper;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.model.AnalyticalRun;
import org.slf4j.Logger;
import org.jdom2.JDOMException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
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
        LOGGER.info("started mapping folder: " + maxQuantImport.getMqParFile().toString());

        List<AnalyticalRun> analyticalRuns;
        try {
            maxQuantParser.clear();
            
            //make the MaxQuantImport resources (mqpar file and combined directory) absolute and check it they exist
            Path relativeCombinedDirectory = maxQuantImport.getCombinedDirectory();
            Path absoluteCombinedDirectory = experimentsDirectory.resolve(relativeCombinedDirectory);
            if (!Files.exists(absoluteCombinedDirectory)) {
                throw new IllegalArgumentException("The combined directory " + absoluteCombinedDirectory.toString() + " doesn't exist.");
            }
            maxQuantImport.setCombinedDirectory(absoluteCombinedDirectory);
            Path relativeMqparFile = maxQuantImport.getMqParFile();
            Path absoluteMqparFile = experimentsDirectory.resolve(relativeMqparFile);
            if (!Files.exists(absoluteMqparFile)) {
                throw new IllegalArgumentException("The mqpar directory " + relativeMqparFile.toString() + " doesn't exist.");
            }
            maxQuantImport.setMqParFile(absoluteMqparFile);

            //parse the MaxQuant files
            maxQuantParser.parse(maxQuantImport, fastasDirectory);

            analyticalRuns = maxQuantParser.getAnalyticalRuns();
            // set storage location.
            analyticalRuns.forEach(run ->run.setStorageLocation(maxQuantImport.getFullCombinedDirectory().toString()));
        } catch (IOException | UnparseableException | JDOMException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException("there was a problem storing your MaxQuant data, underlying exception: ", ex);
        }

        return new MappedData(analyticalRuns, maxQuantParser.getProteinGroupSet());
    }

}
