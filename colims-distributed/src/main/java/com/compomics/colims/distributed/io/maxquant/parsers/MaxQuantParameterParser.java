package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.core.service.TypedCvParamService;
import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.distributed.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantParameterHeaders;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantSummaryHeaders;
import com.compomics.colims.model.*;
import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.BinaryFileType;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.colims.model.factory.CvParamFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

/**
 * Parse MaxQuant parameters file
 *
 * @author Davy
 * @author Iain
 */
@Component
public class MaxQuantParameterParser {

    @Autowired
    private SearchAndValidationSettingsService searchAndValidationSettingsService;
    @Autowired
    private TypedCvParamService typedCvParamService;
    @Autowired
    private OlsService olsService;

    /**
     * MaxQuant version
     */
    private String version = null;

    /**
     * Experiment multiplicity
     */
    private String multiplicity = null;

    /**
     * Default search type
     */
    private SearchCvParam defaultSearchType = null;

    /**
     * Settings used in the experiment, indexed by run
     */
    private Map<String, SearchAndValidationSettings> runSettings = new HashMap<>();

    /**
     * Parameters file name
     */
    private static final String PARAMETERS = "parameters.txt";

    /**
     * Summary data file name
     */
    private static final String SUMMARY = "summary.txt";

    private static final String MS_ONTOLOGY_LABEL = "MS";
    private static final String MS_ONTOLOGY = "PSI Mass Spectrometry Ontology [MS]";
    private static final String NOT_APPLICABLE = "N/A";
    private static final String DEFAULT_SEARCH_TYPE_ACCESSION = "MS:1001083";

    private static final HeaderEnum[] mandatoryHeaders = new HeaderEnum[]{
        MaxQuantSummaryHeaders.ENZYME,
        MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES,
        MaxQuantSummaryHeaders.MULTIPLICITY,
        MaxQuantSummaryHeaders.PROTEASE,
        MaxQuantSummaryHeaders.RAW_FILE
    };

    /**
     * Parse parameters for experiment.
     *
     * @param quantFolder Experiment data folder
     * @param fastaDbs the FASTA databases used in experiment
     * @param storeFiles Whether data files should be stored with experiment
     * @throws IOException
     */
    public void parse(File quantFolder, EnumMap<FastaDbType, FastaDb> fastaDbs, boolean storeFiles) throws IOException {
        TypedCvParam searchType = typedCvParamService.findByAccession(DEFAULT_SEARCH_TYPE_ACCESSION, CvParamType.SEARCH_TYPE);

        if (searchType != null) {
            defaultSearchType = (SearchCvParam) searchType;
        } else {
            throw new IllegalStateException("The default search type CV term was not found in the database.");
        }

        SearchAndValidationSettings searchAndValidationSettings = parseSettings(quantFolder, fastaDbs, storeFiles);

        runSettings = parseRuns(quantFolder, searchAndValidationSettings);
    }

    /**
     * Parse common search and validation settings for an experiment.
     *
     * @param maxQuantFolder Experiment data folder
     * @param fastaDbs the FASTA databases used in experiment
     * @param storeFiles Whether data files should be stored with experiment
     * @return A SearchAndValidationSettings object
     * @throws IOException
     */
    public SearchAndValidationSettings parseSettings(File maxQuantFolder, EnumMap<FastaDbType, FastaDb> fastaDbs, boolean storeFiles) throws IOException {
        SearchAndValidationSettings searchAndValidationSettings = new SearchAndValidationSettings();

        //set FASTA database entity associations
        fastaDbs.forEach((k, v) -> {
            SearchSettingsHasFastaDb searchSettingsHasFastaDb = new SearchSettingsHasFastaDb(k, searchAndValidationSettings, v);
            searchAndValidationSettings.getSearchSettingsHasFastaDbs().add(searchSettingsHasFastaDb);
        });

        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchType(defaultSearchType);

        Iterator<Entry<String, String>> parameterIterator = parseParameters(new File(maxQuantFolder, PARAMETERS)).entrySet().iterator();
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

        searchAndValidationSettings.setSearchParameterSettings(searchParameters);   // TODO: why is this called searchparametersettings
        searchParameters.getSearchAndValidationSettingses().add(searchAndValidationSettings);

        searchAndValidationSettings.setSearchEngine(searchAndValidationSettingsService.getSearchEngine(SearchEngineType.MAX_QUANT, version));

        // currently just storing whole folder
        IdentificationFile identificationFileEntity = new IdentificationFile(maxQuantFolder.getName(), maxQuantFolder.getCanonicalPath());

        if (storeFiles) {
            identificationFileEntity.setBinaryFileType(BinaryFileType.ZIP);
            byte[] content = IOUtils.readAndZip(maxQuantFolder);
            identificationFileEntity.setContent(content);
        }

        //set entity relations
        identificationFileEntity.setSearchAndValidationSettings(searchAndValidationSettings);
        searchAndValidationSettings.getIdentificationFiles().add(identificationFileEntity);

        return searchAndValidationSettings;
    }

    /**
     * Parse a parameters file.
     *
     * @param parameterFile File to be parsed
     * @return Key-value list of file data
     * @throws IOException
     */
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

    /**
     * Parse the search and validation settings for a given data set.
     *
     * @param quantFolder Data folder for max quant run
     * @param searchAndValidationSettings An initial SearchAndValidationSettings
     * object to decorate per run
     * @return Settings indexed by run file name
     * @throws IOException
     */
    private Map<String, SearchAndValidationSettings> parseRuns(File quantFolder, SearchAndValidationSettings searchAndValidationSettings) throws IOException {
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
                // apparently protease was old column name
                String enzymeName = row.get(MaxQuantSummaryHeaders.ENZYME.getDefaultColumnName()) == null
                        ? row.get(MaxQuantSummaryHeaders.PROTEASE.getDefaultColumnName())
                        : row.get(MaxQuantSummaryHeaders.ENZYME.getDefaultColumnName());

                if (!enzymeName.isEmpty()) {
                    // TODO: separate this into utility (not utilities) class
                    TypedCvParam enzyme = typedCvParamService.findByName(enzymeName, CvParamType.SEARCH_PARAM_ENZYME, true);

                    if (enzyme == null) {
                        enzyme = olsService.findEnzymeByName(enzymeName);

                        if (enzyme == null) {
                            enzyme = CvParamFactory.newTypedCvInstance(CvParamType.SEARCH_PARAM_ENZYME, MS_ONTOLOGY, MS_ONTOLOGY_LABEL, NOT_APPLICABLE, enzymeName);
                        }

                        typedCvParamService.persist(enzyme);
                    }

                    runSettings.getSearchParameters().setEnzyme((SearchCvParam) enzyme);
                }

                if (!row.get(MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES.getDefaultColumnName()).isEmpty()) {
                    runSettings.getSearchParameters().setNumberOfMissedCleavages(Integer.parseInt(row.get(MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES.getDefaultColumnName())));
                }
            }

            allSettings.put(row.get(MaxQuantSummaryHeaders.RAW_FILE.getDefaultColumnName()), runSettings);
        }

        return allSettings;
    }

    /**
     * Deep copy a SearchAndValidationSettings object.
     *
     * @param oldSettings Settings to clone
     * @return Cloned object
     */
    private SearchAndValidationSettings cloneSearchAndValidationSettings(SearchAndValidationSettings oldSettings) {
        SearchAndValidationSettings newSettings = new SearchAndValidationSettings();
        newSettings.setSearchParameterSettings(new SearchParameters());

        newSettings.setSearchSettingsHasFastaDbs(oldSettings.getSearchSettingsHasFastaDbs());
        newSettings.setSearchEngine(oldSettings.getSearchEngine());
        newSettings.getSearchParameters().setFragMassTolerance(oldSettings.getSearchParameters().getFragMassTolerance());
        newSettings.getSearchParameters().setFragMassToleranceUnit(oldSettings.getSearchParameters().getFragMassToleranceUnit());

        return newSettings;
    }

    /**
     * Get the multiplicity for this experiment.
     *
     * @return Parsed multiplicity value
     */
    public String getMultiplicity() {
        return multiplicity;
    }

    /**
     * Get the run settings stored in this object
     *
     * @return Copy of the filename/settings map
     */
    public Map<String, SearchAndValidationSettings> getRunSettings() {
        return Collections.unmodifiableMap(runSettings);
    }

    /**
     * Get the version of MaxQuant used.
     *
     * @return Version number string
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Clear data stored in parser
     */
    public void clear() {
        runSettings.clear();
        multiplicity = null;
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
}
