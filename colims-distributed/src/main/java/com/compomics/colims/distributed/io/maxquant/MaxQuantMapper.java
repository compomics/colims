package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.distributed.io.DataMapper;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantQuantificationSettingsParser;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantSearchSettingsParser;
import com.compomics.colims.model.AnalyticalRun;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private static final Logger LOGGER = Logger.getLogger(MaxQuantMapper.class);

    private final MaxQuantSearchSettingsParser maxQuantSearchSettingsParser;
    private final MaxQuantParser maxQuantParser;
    private final MaxQuantQuantificationSettingsParser maxQuantQuantificationSettingsParser;
    private final FastaDbService fastaDbService;

    @Autowired
    public MaxQuantMapper(MaxQuantSearchSettingsParser maxQuantSearchSettingsParser,
                          MaxQuantParser maxQuantParser,
                          MaxQuantQuantificationSettingsParser maxQuantQuantificationSettingsParser,
                          FastaDbService fastaDbService) {
        this.maxQuantSearchSettingsParser = maxQuantSearchSettingsParser;
        this.maxQuantParser = maxQuantParser;
        this.maxQuantQuantificationSettingsParser = maxQuantQuantificationSettingsParser;
        this.fastaDbService = fastaDbService;
    }

    @Override
    public void clear() {
        maxQuantSearchSettingsParser.clear();
        maxQuantQuantificationSettingsParser.clear();
        maxQuantParser.clear();
    }

    @Override
    public MappedData mapData(MaxQuantImport maxQuantImport) throws MappingException {
        LOGGER.info("started mapping folder: " + maxQuantImport.getMqParFile().toString());

        List<AnalyticalRun> analyticalRuns;
        try {
            maxQuantParser.clear();

            //parse the MaxQuant files
            maxQuantParser.parse(maxQuantImport);

            analyticalRuns = maxQuantParser.getAnalyticalRuns();
        } catch (IOException | UnparseableException | JDOMException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException("there was a problem storing your max quant data, underlying exception: ", ex);
        }

        return new MappedData(analyticalRuns, maxQuantParser.getProteinGroupSet());
    }

}
