package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.*;
import com.compomics.colims.core.io.maxquant.parsers.MaxQuantParameterParser;
import com.compomics.colims.core.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.core.io.maxquant.utilities_mappers.MaxQuantUtilitiesPsmMapper;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSpectrumMapper;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.QuantificationEngineType;
import com.compomics.colims.model.enums.SearchEngineType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The DataImporter class for MaxQuant projects.
 *
 * @author Davy
 */
@Component("maxQuantImporter")
public class MaxQuantImporter implements DataImporter<MaxQuantImport> {

    private static final Logger LOGGER = Logger.getLogger(MaxQuantImporter.class);
    @Autowired
    private SearchSettingsMapper searchSettingsMapper;
    @Autowired
    private MaxQuantParameterParser parameterParser;
    @Autowired
    private MaxQuantParser maxQuantParser;
    @Autowired
    private MaxQuantUtilitiesPsmMapper maxQuantUtilitiesPsmMapper;
    @Autowired
    private QuantificationSettingsMapper quantificationSettingsMapper;

    @Override
    public void clear() {
        parameterParser.clear();
    }

    @Override
    public List<AnalyticalRun> importData(MaxQuantImport maxQuantImport) throws MappingException {
        LOGGER.info("started mapping folder: " + maxQuantImport.getMaxQuantDirectory().getName());
        List<AnalyticalRun> mappedRuns = new ArrayList<>();

        try {
            //just in case
            maxQuantParser.clear();

            parameterParser.parse(maxQuantImport.getMaxQuantDirectory());
            maxQuantParser.parseFolder(maxQuantImport.getMaxQuantDirectory(), parameterParser.getMultiplicity());

            for (MaxQuantAnalyticalRun aParsedRun : maxQuantParser.getRuns()) {
                AnalyticalRun targetRun = new AnalyticalRun();
                targetRun.setStorageLocation(maxQuantImport.getMaxQuantDirectory().getCanonicalPath());

                //first, map the search settings
                SearchAndValidationSettings searchAndValidationSettings = mapSearchSettings(maxQuantImport, targetRun);

                List<Spectrum> mappedSpectra = new ArrayList<>(aParsedRun.getSpectra().size());

                for (Map.Entry<Integer, Spectrum> aParsedSpectrum : aParsedRun.getSpectra().entrySet()) {
                    Spectrum targetSpectrum = new Spectrum();

                    //set entity relation
                    targetSpectrum.setAnalyticalRun(targetRun);

                    mappedSpectra.add(targetSpectrum);

                    // instead of mapper here we can just build relations as needed
                    // but i wonder if even this can't be included in the parser

                    maxQuantUtilitiesPsmMapper.map(aParsedSpectrum.getValue(), maxQuantParser, targetSpectrum);
                }
                targetRun.setSpectrums(mappedSpectra);
                mappedRuns.add(targetRun);
            }

        } catch (IOException |  UnparseableException | MappingException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException("there was a problem storing your max quant data, underlying exception: ", ex);
        }

        return mappedRuns;
    }

    /**
     * Map the search settings.
     *
     * @param maxQuantImport The MaxQuantImport instance
     * @param analyticalRun  the AnalyticalRun instance onto the search settings will be mapped
     * @return the mapped SearchAndValidationSettings instance
     * @throws IOException                       thrown in case of an I/O related problem
     * @throws HeaderEnumNotInitialisedException thrown in case of an non initialised header exception
     */
    private SearchAndValidationSettings mapSearchSettings(final MaxQuantImport maxQuantImport, final AnalyticalRun analyticalRun) throws IOException {
        SearchAndValidationSettings searchAndValidationSettings;

        List<File> identificationFiles = new ArrayList<>();
        identificationFiles.add(maxQuantImport.getMaxQuantDirectory());

        // TODO: settings for multiple runs
        searchAndValidationSettings = searchSettingsMapper.map(SearchEngineType.MAX_QUANT, parameterParser.getMaxQuantVersion(), maxQuantImport.getFastaDb(), parameterParser.getRunParameters().values().iterator().next(), identificationFiles, false);

        //set entity relations
        analyticalRun.setSearchAndValidationSettings(searchAndValidationSettings);
        searchAndValidationSettings.setAnalyticalRun(analyticalRun);

        return searchAndValidationSettings;
    }

    /**
     * Map the quantification settings.
     *
     * @param maxQuantImport The MaxQuantImport instance
     * @param analyticalRun  the AnalyticalRun instance onto the quantification settings will be mapped
     * @return the imported QuantificationSettings instance
     * @throws IOException thrown in case of an I/O related problem
     */
    private QuantificationSettings importQuantSettings(final MaxQuantImport maxQuantImport, final AnalyticalRun analyticalRun) throws IOException {
        QuantificationSettings quantificationSettings;

        List<File> quantFiles = new ArrayList<>();
        quantFiles.add(new File(maxQuantImport.getMaxQuantDirectory(), "msms.txt"));  // TODO: make a constant also is this the right file?
        QuantificationParameters params = new QuantificationParameters();

        quantificationSettings = quantificationSettingsMapper.map(QuantificationEngineType.MAX_QUANT, parameterParser.getMaxQuantVersion(), quantFiles, params);

        //set entity relations
        analyticalRun.setQuantificationSettings(quantificationSettings);
        quantificationSettings.setAnalyticalRun(analyticalRun);

        return quantificationSettings;
    }
}
