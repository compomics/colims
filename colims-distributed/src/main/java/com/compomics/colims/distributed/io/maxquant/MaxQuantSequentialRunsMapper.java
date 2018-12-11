package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.distributed.io.SequentialRunsDataMapper;
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
import java.util.Set;

/**
 * The DataMapper implementation for MaxQuant projects.
 *
 * @author Davy
 * @author Iain
 */
@Component("maxQuantSequentialRunsMapper")
public class MaxQuantSequentialRunsMapper implements SequentialRunsDataMapper<MaxQuantImport> {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MaxQuantSequentialRunsMapper.class);
    /**
     * The MaxQuant parent parser class.
     */
    private final MaxQuantParser maxQuantParser;

    private MaxQuantImport maxQuantImport;

    @Autowired
    public MaxQuantSequentialRunsMapper(MaxQuantParser maxQuantParser) {
        this.maxQuantParser = maxQuantParser;
    }

    @Override
    public void clear() {
        maxQuantParser.clear();
    }

    @Override
    public void clearAfterSingleRun(String runName) {
        maxQuantParser.clearAfterSingleRun(runName);
    }

    @Override
    public Set<String> getRunNames(MaxQuantImport dataImport, Path fastasDirectory) throws IOException, JDOMException {
        this.maxQuantImport = dataImport;

        return maxQuantParser.parseSettingsAndProteins(dataImport, fastasDirectory);
    }

    @Override
    public MappedData mapData(String analyticalRun) throws MappingException {
        LOGGER.info("started mapping folder: " + maxQuantImport.getMqParFile() + " for run " + analyticalRun);

        List<AnalyticalRun> analyticalRuns;
        try {
            //parseSpectraAndPSMs the MaxQuant files
            maxQuantParser.parseSpectraAndPSMs(maxQuantImport, analyticalRun);

            analyticalRuns = maxQuantParser.getAnalyticalRuns();

            //set storage location
            analyticalRuns.forEach(run -> run.setStorageLocation(maxQuantImport.getCombinedDirectory()));
        } catch (IOException | UnparseableException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException("There was a problem storing your MaxQuant data, underlying exception: ", ex);
        }

        return new MappedData(analyticalRuns, maxQuantParser.getProteinGroupSet());
    }

}
