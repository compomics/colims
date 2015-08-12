package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantParameterHeaders;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantSummaryHeaders;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.IdentificationFile;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.enums.BinaryFileType;
import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.colims.model.enums.SearchEngineType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Davy
 */
@Component
public class MaxQuantParameterParser {
    @Autowired
    private SearchAndValidationSettingsService searchAndValidationSettingsService;

    private String multiplicity = null;
    private Map<String, SearchAndValidationSettings> runParameters = new HashMap<>();

    private static final String PARAMETERS = "parameters.txt";
    private static final String SUMMARY = "summary.txt";

    private static final HeaderEnum[] mandatoryHeaders = new HeaderEnum[]{
            MaxQuantSummaryHeaders.MULTIPLICITY,
            MaxQuantSummaryHeaders.RAW_FILE,

    };

    public void parse(File quantFolder, FastaDb fastaDb, boolean storeFiles) throws IOException {
        SearchAndValidationSettings searchAndValidationSettings = parseSettings(quantFolder, fastaDb, storeFiles);

        runParameters = newParseRuns(quantFolder, searchAndValidationSettings);
    }

    public SearchAndValidationSettings parseSettings(File quantFolder, FastaDb fastaDb, boolean storeFiles) throws IOException {
        SearchAndValidationSettings searchAndValidationSettings = new SearchAndValidationSettings();
        searchAndValidationSettings.setFastaDb(fastaDb);

        com.compomics.colims.model.SearchParameters searchParameters = new com.compomics.colims.model.SearchParameters();
        searchParameters.setSearchType(null);

        Iterator<Entry<String, String>> parameterIterator = parseParameters(new File(quantFolder, PARAMETERS)).entrySet().iterator();
        Entry<String, String> row;
        String version = null;

        while (parameterIterator.hasNext()) {
            row = parameterIterator.next();

            if (row.getKey().equalsIgnoreCase(MaxQuantParameterHeaders.FTMS_MS_MS_TOLERANCE.getDefaultColumnName())) {
                searchParameters.setFragMassTolerance(Double.parseDouble(row.getValue().split(" ")[0]));
                // TODO: precursor mass tolerance, is it anywhere?

                if (row.getValue().split(" ")[1].equalsIgnoreCase("da")) {
                    searchParameters.setFragMassToleranceUnit(MassAccuracyType.DA);
                    searchParameters.setPrecMassToleranceUnit(MassAccuracyType.DA);
                } else {
                    searchParameters.setFragMassToleranceUnit(MassAccuracyType.PPM);
                    searchParameters.setPrecMassToleranceUnit(MassAccuracyType.PPM);
                }
            } else if (row.getKey().equalsIgnoreCase(MaxQuantParameterHeaders.VERSION.getDefaultColumnName())) {
                version = row.getValue();
            }
        }

        searchAndValidationSettings.setSearchEngine(searchAndValidationSettingsService.getSearchEngine(SearchEngineType.MAX_QUANT, version));

        // currently just storing whole folder
        IdentificationFile identificationFileEntity = new IdentificationFile(quantFolder.getName(), quantFolder.getCanonicalPath());

        if (storeFiles) {
            identificationFileEntity.setBinaryFileType(BinaryFileType.ZIP);
            byte[] content = IOUtils.readAndZip(quantFolder);
            identificationFileEntity.setContent(content);
        }

        //set entity relations
        identificationFileEntity.setSearchAndValidationSettings(searchAndValidationSettings);
        searchAndValidationSettings.getIdentificationFiles().add(identificationFileEntity);

        return searchAndValidationSettings;
    }

    public String getMultiplicity() {
        return multiplicity;
    }

    public Map<String, SearchAndValidationSettings> getRunParameters() {
        return Collections.unmodifiableMap(runParameters);
    }

    public void clear() {
        runParameters.clear();
        multiplicity = null;
    }

    public Map<String, String> parseParameters(File parameterFile) throws IOException {
        Map<String, String> parameters = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(parameterFile);
             InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8").newDecoder());
             LineNumberReader reader = new LineNumberReader(isr)) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\t");

                if (split.length == 2) {
                    parameters.put(split[0].toLowerCase(Locale.US), split[1]);
                } else {
                    parameters.put(split[0].toLowerCase(Locale.US), "");
                }
            }
        }

        return parameters;
    }

    private Map<String, SearchAndValidationSettings> newParseRuns(File quantFolder, SearchAndValidationSettings searchAndValidationSettings) throws IOException {
        Map<String, SearchAndValidationSettings> allSettings = new HashMap<>();
        TabularFileLineValuesIterator summaryIter = new TabularFileLineValuesIterator(new File(quantFolder, SUMMARY), mandatoryHeaders);
        Map<String, String> row;

        while (summaryIter.hasNext()) {
            row = summaryIter.next();
            SearchAndValidationSettings runSettings = cloneSearchAndValidationSettings(searchAndValidationSettings);

            if (multiplicity == null && row.containsKey(MaxQuantSummaryHeaders.MULTIPLICITY.getDefaultColumnName())) {
                multiplicity = row.get(MaxQuantSummaryHeaders.MULTIPLICITY.getDefaultColumnName());
            }

            if (!row.get(MaxQuantSummaryHeaders.RAW_FILE.getDefaultColumnName()).equalsIgnoreCase("total")) {
                if (row.containsKey(MaxQuantSummaryHeaders.PROTEASE.getDefaultColumnName()) && !row.get(MaxQuantSummaryHeaders.PROTEASE.getDefaultColumnName()).isEmpty()) {
                    // TODO: enzyme from service (see utilitiessearchparametersmapper)
                    //runParameters.setEnzyme();
                    //row.get(MaxQuantSummaryHeaders.PROTEASE.getDefaultColumnName());
                }

                if (!row.get(MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES.getDefaultColumnName()).isEmpty()) {
                    runSettings.getSearchParameters().setNumberOfMissedCleavages(Integer.parseInt(row.get(MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES.getDefaultColumnName())));
                }
            }

            allSettings.put(row.get(MaxQuantSummaryHeaders.RAW_FILE.getDefaultColumnName()), runSettings);
        }

        return allSettings;
    }

    // TODO: is this modification data ever needed? not accessed by mappers
//    private Map<String, SearchParameters> parseRuns(File quantFolder, SearchParameters experimentParams) throws IOException {
//        if (row.containsKey(MaxQuantSummaryHeaders.FIXED_MODIFICATIONS.getDefaultColumnName())) {
//
//            for (String fixedMod : row.get(MaxQuantSummaryHeaders.FIXED_MODIFICATIONS.getDefaultColumnName()).split(";")) {
//                if (!fixedMod.isEmpty()) {
//                    PTM fixedPTM = new PTM();
//                    fixedPTM.setName(fixedMod);
//                    runModifications.addFixedModification(fixedPTM);
//                }
//            }
//        }
//
//        if (row.containsKey(MaxQuantSummaryHeaders.VARIABLE_MODIFICATIONS.getDefaultColumnName())) {
//
//            for (String varMod : row.get(MaxQuantSummaryHeaders.VARIABLE_MODIFICATIONS.getDefaultColumnName()).split(";")) {
//                if (!varMod.isEmpty()) {
//                    PTM varPTM = new PTM();
//                    varPTM.setName(varMod);
//                    runModifications.addVariableModification(varPTM);
//                }
//            }
//        }
//    }

    private SearchAndValidationSettings cloneSearchAndValidationSettings(SearchAndValidationSettings oldSettings) {
        SearchAndValidationSettings newSettings = new SearchAndValidationSettings();

        newSettings.setFastaDb(oldSettings.getFastaDb());
        newSettings.setSearchEngine(oldSettings.getSearchEngine());
        newSettings.getSearchParameters().setFragMassTolerance(oldSettings.getSearchParameters().getFragMassTolerance());
        newSettings.getSearchParameters().setFragMassToleranceUnit(oldSettings.getSearchParameters().getFragMassToleranceUnit());

        return newSettings;
    }
}
