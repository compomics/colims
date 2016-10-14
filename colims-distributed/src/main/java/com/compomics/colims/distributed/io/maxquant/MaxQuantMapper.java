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
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.enums.FastaDbType;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
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
        LOGGER.info("started mapping folder: " + maxQuantImport.getParameterFilePath().toString());

        List<AnalyticalRun> analyticalRuns = new ArrayList<>();
        try {
            maxQuantParser.clear();

            EnumMap<FastaDbType, List<FastaDb>> fastaDbs = new EnumMap<>(FastaDbType.class);
            //get the FASTA db entities from the database
            maxQuantImport.getFastaDbIds().forEach((FastaDbType fastaDbType, List<Long> fastaDbIds) -> {
                List<FastaDb> fastaDbList = new ArrayList<>();
                fastaDbIds.forEach(fastaDbId -> {
                    fastaDbList.add(fastaDbService.findById(fastaDbId));
                });
                fastaDbs.put(fastaDbType, fastaDbList);
            });

            //parse the search settings
            maxQuantSearchSettingsParser.parse(maxQuantImport.getCombinedFolderDirectory(), maxQuantImport.getParameterFilePath(), fastaDbs);

            //parse the rest
            maxQuantParser.parse(maxQuantImport.getCombinedFolderDirectory(), fastaDbs,
                    maxQuantImport.isIncludeContaminants(),
                    maxQuantImport.isIncludeUnidentifiedSpectra(),
                    maxQuantImport.getSelectedProteinGroupHeaders());

            analyticalRuns = maxQuantParser.getRuns();

            //parse the quantification settings
            //for a silac experiment, we don't have any reagent name from maxquant.
            //Colims gives reagent name due to number of sample.
            if (maxQuantImport.getQuantificationLabel().equals("SILAC")) {
                List<String> silacReagents = new ArrayList<>();
                if (maxQuantSearchSettingsParser.getLabelMods().size() == 3) {
                    silacReagents.addAll(Arrays.asList("SILAC light", "SILAC medium", "SILAC heavy"));
                    maxQuantQuantificationSettingsParser.parse(analyticalRuns, maxQuantImport.getQuantificationLabel(), silacReagents);
                } else if (maxQuantSearchSettingsParser.getLabelMods().size() == 2) {
                    silacReagents.addAll(Arrays.asList("SILAC light", "SILAC heavy"));
                    maxQuantQuantificationSettingsParser.parse(analyticalRuns, maxQuantImport.getQuantificationLabel(), silacReagents);
                }
            } else {
                List<String> reagents = new ArrayList<>(maxQuantSearchSettingsParser.getIsobaricLabels().values());
                maxQuantQuantificationSettingsParser.parse(analyticalRuns, maxQuantImport.getQuantificationLabel(), reagents);
            }
            // link quantification settings to analytical run
            analyticalRuns.forEach(analyticalRun -> {
                analyticalRun.setQuantificationSettings(maxQuantQuantificationSettingsParser.getRunsAndQuantificationSettings().get(analyticalRun));
            });

        } catch (IOException | UnparseableException | JDOMException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException("there was a problem storing your max quant data, underlying exception: ", ex);
        }

        return new MappedData(analyticalRuns, maxQuantParser.getProteinGroupSet());
    }

}
