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
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.colims.model.factory.CvParamFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class parses the MaxQuant parameters file and maps them onto the Colims SearchAndValidationSettings and related
 * entities.
 *
 * @author Davy
 * @author Iain
 * @author niels
 */
@Component("maxQuantSearchSettingsParser")
public class MaxQuantSearchSettingsParser {

    /**
     * The MaxQuant summary data file name.
     */
    private static final String SUMMARY = "summary.txt";
    /**
     * The MaxQuant Parameters file name.
     */
    private static final String PARAMETERS_FILE = "parameters.txt";

    private static final String MS_ONTOLOGY_LABEL = "MS";
    private static final String MS_ONTOLOGY = "PSI Mass Spectrometry Ontology [MS]";
    private static final String NOT_APPLICABLE = "N/A";
    private static final String DEFAULT_SEARCH_TYPE_ACCESSION = "MS:1001083";
    private static final String PARAMETER_DELIMITER = "\t";

    private static final HeaderEnum[] MANDATORY_HEADERS = new HeaderEnum[]{
            MaxQuantSummaryHeaders.ENZYME,
            MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES,
            MaxQuantSummaryHeaders.MULTIPLICITY,
            MaxQuantSummaryHeaders.PROTEASE,
            MaxQuantSummaryHeaders.RAW_FILE
    };

    /**
     * The MaxQuant version.
     */
    private String version = "N/A";
    /**
     * The experiment multiplicity.
     */
    private String multiplicity;
    /**
     * The default search type.
     */
    private SearchCvParam defaultSearchType;
    /**
     * The settings used in the experiment, indexed by run.
     */
    private Map<String, SearchAndValidationSettings> runSettings = new HashMap<>();
    @Autowired
    private SearchAndValidationSettingsService searchAndValidationSettingsService;
    @Autowired
    private TypedCvParamService typedCvParamService;
    @Autowired
    private OlsService newOlsService;

    /**
     * Get the version of MaxQuant used.
     *
     * @return Version number string
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Clear data stored in parser.
     */
    public void clear() {
        runSettings.clear();
        multiplicity = null;
    }

    /**
     * Parse parameters for experiment.
     *
     * @param maxQuantFolder  the MaxQuant folder
     * @param fastaDbs   the FASTA databases used in the experiment
     * @param storeFiles whether data files should be stored with experiment
     * @throws IOException thrown in case of of an I/O related problem
     */
    public void parse(File maxQuantFolder, EnumMap<FastaDbType, FastaDb> fastaDbs, boolean storeFiles) throws IOException {

        //first, get the

        SearchAndValidationSettings searchAndValidationSettings = parseSearchSettings(maxQuantFolder, fastaDbs, storeFiles);

        runSettings = parseRuns(maxQuantFolder, searchAndValidationSettings);
    }

    /**
     * Parse the search settings for the given experiment and map them onto a Colims SearchAndValidationSettings
     * instance.
     *
     * @param txtFolder  the MaxQuant txt folder
     * @param fastaDbs   the FASTA databases used in the experiment
     * @param storeFiles whether data files should be stored with experiment
     * @return the mapped SearchAndValidationSettings instance
     * @throws IOException thrown in case of of an I/O related problem
     */
    private SearchAndValidationSettings parseSearchSettings(File txtFolder, EnumMap<FastaDbType, FastaDb> fastaDbs, boolean storeFiles) throws IOException {
        SearchAndValidationSettings searchAndValidationSettings = new SearchAndValidationSettings();

        //set the FASTA databases entity associations
        fastaDbs.forEach((k, v) -> {
            SearchSettingsHasFastaDb searchSettingsHasFastaDb = new SearchSettingsHasFastaDb(k, searchAndValidationSettings, v);
            searchAndValidationSettings.getSearchSettingsHasFastaDbs().add(searchSettingsHasFastaDb);
        });

        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchType(defaultSearchType);

        //parse the parameters file and iterate over the parameters
        Map<String, String> parameters = ParseUtils.parseParameters(new File(txtFolder, PARAMETERS_FILE), true);
        //get the MaxQuant version
        String versionParameter = parameters.get(MaxQuantParameterHeaders.VERSION.getDefaultColumnName().toLowerCase());
        if (versionParameter != null && !versionParameter.isEmpty() && !version.equals(versionParameter)) {
            version = versionParameter;
        }

        //set the search engine
        searchAndValidationSettings.setSearchEngine(searchAndValidationSettingsService.getSearchEngine(SearchEngineType.MAX_QUANT, version));

        //
//        Entry<String, String> parameter;
//        while (parameterIterator.hasNext()) {
//            parameter = parameterIterator.next();
//
//            if (parameter.getKey().equalsIgnoreCase(MaxQuantParameterHeaders.FTMS_MS_MS_TOLERANCE.getDefaultColumnName())) {
//                searchParameters.setFragMassTolerance(Double.parseDouble(parameter.getValue().split(" ")[0]));
//                // TODO: precursor mass tolerance, is it anywhere?
//
//                if (parameter.getValue().split(" ")[1].equalsIgnoreCase("da")) {
//                    searchParameters.setFragMassToleranceUnit(MassAccuracyType.DA);
//                    searchParameters.setPrecMassToleranceUnit(MassAccuracyType.DA);
//                } else {
//                    searchParameters.setFragMassToleranceUnit(MassAccuracyType.PPM);
//                    searchParameters.setPrecMassToleranceUnit(MassAccuracyType.PPM);
//                }
//            } else if (parameter.getKey().equalsIgnoreCase(MaxQuantParameterHeaders.VERSION.getDefaultColumnName())) {
//                version = parameter.getValue();
//            }
//        }

        searchAndValidationSettings.setSearchParameters(searchParameters);
        searchParameters.getSearchAndValidationSettingses().add(searchAndValidationSettings);

        // currently just storing whole folder
        IdentificationFile identificationFileEntity = new IdentificationFile(txtFolder.getName(), txtFolder.getCanonicalPath());

        if (storeFiles) {
            identificationFileEntity.setBinaryFileType(BinaryFileType.ZIP);
            byte[] content = IOUtils.readAndZip(txtFolder);
            identificationFileEntity.setContent(content);
        }

        //set entity relations
        identificationFileEntity.setSearchAndValidationSettings(searchAndValidationSettings);
        searchAndValidationSettings.getIdentificationFiles().add(identificationFileEntity);

        return searchAndValidationSettings;
    }



    /**
     * Parse the search and validation settings for a given data set.
     *
     * @param quantFolder                 Data folder for max quant run
     * @param searchAndValidationSettings An initial SearchAndValidationSettings object to decorate per run
     * @return Settings indexed by run file name
     * @throws IOException thrown in case of of an I/O related problem
     */
    private Map<String, SearchAndValidationSettings> parseRuns(File quantFolder, SearchAndValidationSettings searchAndValidationSettings) throws IOException {
        Map<String, SearchAndValidationSettings> allSettings = new HashMap<>();
        TabularFileLineValuesIterator summaryIter = new TabularFileLineValuesIterator(new File(quantFolder, SUMMARY), MANDATORY_HEADERS);
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
                        enzyme = newOlsService.findEnzymeByName(enzymeName);

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
        newSettings.setSearchParameters(new SearchParameters());

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
     * Get the default search type from the database and assign it to the class field.
     */
    @PostConstruct
    private void getDefaultSearchType() {
        //look for the default search type in the database
        TypedCvParam searchType = typedCvParamService.findByAccession(DEFAULT_SEARCH_TYPE_ACCESSION, CvParamType.SEARCH_TYPE);
        if (searchType != null) {
            defaultSearchType = (SearchCvParam) searchType;
        } else {
            throw new IllegalStateException("The default search type CV term was not found in the database.");
        }
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
