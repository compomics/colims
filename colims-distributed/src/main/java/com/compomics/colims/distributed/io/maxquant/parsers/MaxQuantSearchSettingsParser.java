package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.core.service.TypedCvParamService;
import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.distributed.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantParameterHeaders;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantSpectrumParameterHeaders;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantSummaryHeaders;
import com.compomics.colims.model.*;
import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.*;
import com.compomics.colims.model.factory.CvParamFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class parses the MaxQuant parameter files and maps them onto the Colims SearchAndValidationSettings and related
 * entities.
 *
 * @author Davy
 * @author Iain
 * @author niels
 */
@Component("maxQuantSearchSettingsParser")
public class MaxQuantSearchSettingsParser {

    /**
     * Logger instance.
     */
    private static Logger LOGGER = Logger.getLogger(MaxQuantSearchSettingsParser.class);

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
    /**
     * The {@link MaxQuantAplParser} bean for getting search parameters parsed from files in the andromeda directory.
     */
    @Autowired
    private MaxQuantAndromedaParser maxQuantAndromedaParser;
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
     * Parse the search parameters for a MaxQuant experiment.
     *
     * @param maxQuantTxtDirectory the MaxQuant txt directory path
     * @param fastaDbs             the FASTA databases used in the experiment
     * @param storeFiles           whether data files should be stored with the experiment
     * @throws IOException thrown in case of of an I/O related problem
     */
    public void parse(Path maxQuantTxtDirectory, EnumMap<FastaDbType, FastaDb> fastaDbs, boolean storeFiles) throws IOException {
        //parse the search settings
        SearchAndValidationSettings searchAndValidationSettings = parseSearchSettings(maxQuantTxtDirectory, fastaDbs, storeFiles);

        runSettings = parseRuns(maxQuantTxtDirectory, searchAndValidationSettings);
    }

    /**
     * Parse the search settings for the given experiment and map them onto a Colims SearchAndValidationSettings
     * instance.
     *
     * @param maxQuantTxtDirectory the MaxQuant txt directory path
     * @param fastaDbs             the FASTA databases used in the experiment
     * @param storeFiles           whether data files should be stored with experiment
     * @return the mapped SearchAndValidationSettings instance
     * @throws IOException thrown in case of of an I/O related problem
     */
    private SearchAndValidationSettings parseSearchSettings(Path maxQuantTxtDirectory, EnumMap<FastaDbType, FastaDb> fastaDbs, boolean storeFiles) throws IOException {
        SearchAndValidationSettings searchAndValidationSettings = new SearchAndValidationSettings();

        //set the FASTA databases entity associations
        fastaDbs.forEach((k, v) -> {
            SearchSettingsHasFastaDb searchSettingsHasFastaDb = new SearchSettingsHasFastaDb(k, searchAndValidationSettings, v);
            searchAndValidationSettings.getSearchSettingsHasFastaDbs().add(searchSettingsHasFastaDb);
        });

        /**
         * Map the search parameters onto a Colims {@link SearchParameters} instance.
         */
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchType(defaultSearchType);

        //parse the parameters file and iterate over the parameters
        Map<String, String> parameters = ParseUtils.parseParameters(Paths.get(maxQuantTxtDirectory.toString(), MaxQuantConstants.PARAMETERS_FILE.value()), MaxQuantConstants.PARAM_TAB_DELIMITER.value(), true);


        //get the MaxQuant version
        String versionParameter = parameters.get(MaxQuantParameterHeaders.VERSION.getValue());
        if (versionParameter != null && !versionParameter.isEmpty() && !version.equals(versionParameter)) {
            version = versionParameter;
        }
        //precursor mass tolerance and unit
        String precursorMassToleranceString = maxQuantAndromedaParser.getSpectrumParameters().get(MaxQuantSpectrumParameterHeaders.PEPTIDE_MASS_TOLERANCE);
        searchParameters.setPrecMassTolerance(Double.parseDouble(precursorMassToleranceString));

        String precursorMassToleranceUnit = maxQuantAndromedaParser.getSpectrumParameters().get(MaxQuantSpectrumParameterHeaders.PEPTIDE_MASS_TOLERANCE_UNIT);
        searchParameters.setFragMassToleranceUnit(MassAccuracyType.valueOf(precursorMassToleranceUnit.toUpperCase(Locale.ENGLISH)));

        //fragment mass tolerance and unit
        String fragmentMassToleranceString = maxQuantAndromedaParser.getSpectrumParameters().get(MaxQuantSpectrumParameterHeaders.FRAGMENT_MASS_TOLERANCE);
        searchParameters.setFragMassTolerance(Double.parseDouble(fragmentMassToleranceString));

        String fragmentMassToleranceUnit = maxQuantAndromedaParser.getSpectrumParameters().get(MaxQuantSpectrumParameterHeaders.FRAGMENT_MASS_TOLERANCE_UNIT);
        searchParameters.setFragMassToleranceUnit(MassAccuracyType.valueOf(fragmentMassToleranceUnit.toUpperCase(Locale.ENGLISH)));

        //enzyme
        TypedCvParam enzyme = mapEnzyme(maxQuantAndromedaParser.getSpectrumParameters().get(MaxQuantSpectrumParameterHeaders.ENZYMES));
        if (enzyme != null) {
            searchParameters.setEnzyme((SearchCvParam) enzyme);
        }

        //set the search engine
        searchAndValidationSettings.setSearchEngine(searchAndValidationSettingsService.getSearchEngine(SearchEngineType.MAX_QUANT, version));

        //set entity relations between SearchAndValidationSettings and SearchParameters
        searchAndValidationSettings.setSearchParameters(searchParameters);
        searchParameters.getSearchAndValidationSettingses().add(searchAndValidationSettings);

        // TODO: 26/05/16 check which files to store
        //currently just storing whole folder
        IdentificationFile identificationFileEntity = new IdentificationFile(maxQuantTxtDirectory.getFileName().toString(), maxQuantTxtDirectory.toString());

        if (storeFiles) {
            identificationFileEntity.setBinaryFileType(BinaryFileType.ZIP);
            byte[] content = IOUtils.readAndZip(maxQuantTxtDirectory.toFile());
            identificationFileEntity.setContent(content);
        }

        //set entity relations between SearchAndValidationSettings ad IdentificationFile
        identificationFileEntity.setSearchAndValidationSettings(searchAndValidationSettings);
        searchAndValidationSettings.getIdentificationFiles().add(identificationFileEntity);

        return searchAndValidationSettings;
    }


    /**
     * Parse the search and validation settings for a given data set.
     *
     * @param maxQuantTxtDirectory        the MaxQuant txt directory path
     * @param searchAndValidationSettings an initial SearchAndValidationSettings object to decorate per run
     * @return the search and validation settings indexed by run file name
     * @throws IOException thrown in case of of an I/O related problem
     */
    private Map<String, SearchAndValidationSettings> parseRuns(Path maxQuantTxtDirectory, SearchAndValidationSettings searchAndValidationSettings) throws IOException {
        Map<String, SearchAndValidationSettings> allSettings = new HashMap<>();
        TabularFileLineValuesIterator summaryIterator = new TabularFileLineValuesIterator(Paths.get(maxQuantTxtDirectory.toString(), MaxQuantConstants.SUMMARY_FILE.value()).toFile(), MANDATORY_HEADERS);
        Map<String, String> row;

        while (summaryIterator.hasNext()) {
            row = summaryIterator.next();
            SearchAndValidationSettings runSettings = cloneSearchAndValidationSettings(searchAndValidationSettings);

            if (multiplicity == null && row.containsKey(MaxQuantSummaryHeaders.MULTIPLICITY.getValue())) {
                multiplicity = row.get(MaxQuantSummaryHeaders.MULTIPLICITY.getValue());
            }

            if (!row.get(MaxQuantSummaryHeaders.RAW_FILE.getValue()).equalsIgnoreCase("total")) {
                // apparently protease was old column name
                String enzymeName = row.get(MaxQuantSummaryHeaders.ENZYME.getValue()) == null
                        ? row.get(MaxQuantSummaryHeaders.PROTEASE.getValue())
                        : row.get(MaxQuantSummaryHeaders.ENZYME.getValue());

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

                if (!row.get(MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES.getValue()).isEmpty()) {
                    runSettings.getSearchParameters().setNumberOfMissedCleavages(Integer.parseInt(row.get(MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES.getValue())));
                }
            }

            allSettings.put(row.get(MaxQuantSummaryHeaders.RAW_FILE.getValue()), runSettings);
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

    /**
     * Map the given MaxQuant Enzyme instance to a TypedCvParam instance. Returns null if no mapping was possible.
     *
     * @param maxQuantEnzyme the MaxQuant enzyme
     * @return the TypedCvParam instance
     */
    private TypedCvParam mapEnzyme(final String maxQuantEnzyme) {
        TypedCvParam enzyme;

        //look for the enzyme in the database
        enzyme = typedCvParamService.findByName(maxQuantEnzyme, CvParamType.SEARCH_PARAM_ENZYME, true);

        if (enzyme == null) {
            try {
                //the enzyme was not found by name in the database
                //look for the enzyme in the MS ontology by name
                enzyme = newOlsService.findEnzymeByName(maxQuantEnzyme);
            } catch (RestClientException ex) {
                LOGGER.error(ex.getMessage(), ex);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }

            if (enzyme == null) {
                //the enzyme was not found by name in the MS ontology
                enzyme = CvParamFactory.newTypedCvInstance(CvParamType.SEARCH_PARAM_ENZYME, MS_ONTOLOGY, MS_ONTOLOGY_LABEL, NOT_APPLICABLE, maxQuantEnzyme);
            }

            //persist the newly created enzyme
            typedCvParamService.persist(enzyme);
        }

        return enzyme;
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
