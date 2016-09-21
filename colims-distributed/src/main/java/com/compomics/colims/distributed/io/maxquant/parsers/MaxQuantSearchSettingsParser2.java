package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.ModificationMappingException;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.core.service.TypedCvParamService;
import com.compomics.colims.distributed.io.maxquant.FixedTabularFileIterator;
import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.headers.*;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesPtmSettingsMapper;
import com.compomics.colims.model.*;
import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.*;
import com.compomics.colims.model.factory.CvParamFactory;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class parses the MaxQuant parameter files and maps them onto the Colims
 * SearchAndValidationSettings and related entities.
 *
 * @author Davy
 * @author Iain
 * @author niels
 */
@Component("maxQuantSearchSettingsParser2")
public class MaxQuantSearchSettingsParser2 {

    /**
     * Logger instance.
     */
    private static Logger LOGGER = Logger.getLogger(MaxQuantSearchSettingsParser2.class);

    private static final String MS_ONTOLOGY_LABEL = "MS";
    private static final String MS_ONTOLOGY = "PSI Mass Spectrometry Ontology [MS]";
    private static final String NOT_APPLICABLE = "N/A";
    private static final String DEFAULT_SEARCH_TYPE_ACCESSION = "MS:1001083";
    private static final String MODIFICATIONS_DELIMITER = ",";
    private static final String MODIFICATION_NAME_ONLY = " ";
    private static final String FILE_PATHS = "filepaths";
    private static final String EXPERIMENTS = "experiments";
    private static final String PARAM_GROUP_INDICES = "paramgroupindices";
    private static final String PARAMETER_GROUPS = "parametergroups";
    private static final String PARAMETER_DELIMITER = ";";

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
     * The mqpar.xml parameters with raw file name (key: raw file name; value:
     * enum map of spectrum parameters).
     */
    private Map<String, EnumMap<MqParHeader, String>> mqParParamsWithRawFile = new HashMap<>();
    /**
     * The analytical run name with experiment name(key: analyticalRun ; value:
     * experiment name).
     */
    private Map<AnalyticalRun, String> analyticalRuns = new HashMap<>();
    /**
     * Isobaric labels for labeled quantification.(key: index , value : isobaric
     * label)
     */
    private Map<Integer, String> isobaricLabels = new HashMap<>();
    /**
     * label mods for SILAC experiments.(key: index , value : isobaric label)
     */
    private Map<Integer, String> labelMods = new HashMap<>();
    private MqParHeaders mqParHeaders;
    private ParametersHeaders parametersHeaders;
    private SummaryHeaders summaryHeaders;
    /**
     * Beans.
     */
    @Autowired
    private SearchAndValidationSettingsService searchAndValidationSettingsService;
    @Autowired
    private TypedCvParamService typedCvParamService;
    @Autowired
    private OlsService olsService;
    @Autowired
    private UtilitiesPtmSettingsMapper utilitiesPtmSettingsMapper;

    /**
     * No-arg constructor.
     *
     * @throws IOException in case of an Input/Output related problem while parsing the headers.
     */
    public MaxQuantSearchSettingsParser2() throws IOException {
        parametersHeaders = new ParametersHeaders();
        summaryHeaders = new SummaryHeaders();
        mqParHeaders = new MqParHeaders();
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
     * Clear data stored in parser.
     */
    public void clear() {
        runSettings.clear();
        multiplicity = null;
    }

    /**
     * Parse the search parameters for a MaxQuant experiment.
     *
     * @param combinedFolderDirectory the MaxQuant combined folder directory path
     * @param mqParFile               the mqpar.xml parameter file
     * @param fastaDbs                the FASTA databases used in the experiment
     * @param storeFiles              whether data files should be stored with the experiment
     * @throws IOException thrown in case of of an I/O related problem
     */
    public void parse(Path combinedFolderDirectory, Path mqParFile, EnumMap<FastaDbType, List<FastaDb>> fastaDbs, boolean storeFiles) throws IOException, ModificationMappingException, JDOMException {
        Path txtDirectory = Paths.get(combinedFolderDirectory + File.separator + MaxQuantConstants.TXT_DIRECTORY.value());
        //parse the mxpar.xml file
        parseMqParFile(mqParFile);

        //parse the summary.txt file
        FixedTabularFileIterator summaryIterator = new FixedTabularFileIterator(Paths.get(txtDirectory.toString(), MaxQuantConstants.SUMMARY_FILE.value()), summaryHeaders);
        Map<SummaryHeader, String> summaryEntry;
        while (summaryIterator.hasNext()) {
            summaryEntry = summaryIterator.next();
            //parse the search settings
            if (getMqParParamsWithRawFile().containsKey(summaryEntry.get(SummaryHeader.RAW_FILE))) {
                SearchAndValidationSettings searchAndValidationSettings
                        = parseSearchSettings(txtDirectory, fastaDbs, storeFiles, summaryEntry.get(SummaryHeader.RAW_FILE));
                if (multiplicity == null && summaryEntry.containsKey(SummaryHeader.MULTIPLICITY)) {
                    multiplicity = summaryEntry.get(SummaryHeader.MULTIPLICITY);
                }

                runSettings.put(summaryEntry.get(SummaryHeader.RAW_FILE), searchAndValidationSettings);
            }
        }

    }

    /**
     * Parse the search settings for the given experiment and map them onto a
     * Colims SearchAndValidationSettings instance.
     *
     * @param maxQuantTxtDirectory the MaxQuant txt directory path
     * @param fastaDbs             the FASTA databases used in the experiment
     * @param storeFiles           whether data files should be stored with experiment
     * @return the mapped SearchAndValidationSettings instance
     * @throws IOException thrown in case of of an I/O related problem
     */
    private SearchAndValidationSettings parseSearchSettings(Path maxQuantTxtDirectory, EnumMap<FastaDbType, List<FastaDb>> fastaDbs, boolean storeFiles, String rawFileName) throws IOException, ModificationMappingException {
        SearchAndValidationSettings searchAndValidationSettings = new SearchAndValidationSettings();

        //set the FASTA databases entity associations
        fastaDbs.forEach((k, v) -> {
            v.forEach(fastaDb -> {
                SearchSettingsHasFastaDb searchSettingsHasFastaDb = new SearchSettingsHasFastaDb(k, searchAndValidationSettings, fastaDb);
                searchAndValidationSettings.getSearchSettingsHasFastaDbs().add(searchSettingsHasFastaDb);
            });
        });

        /**
         * Map the search parameters onto a Colims {@link SearchParameters}
         * instance.
         */
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchType(defaultSearchType);

        //parse the parameters file and iterate over the parameters
        Map<String, String> parameters = ParseUtils.parseParameters(Paths.get(maxQuantTxtDirectory.toString(), MaxQuantConstants.PARAMETERS_FILE.value()), parametersHeaders.getMandatoryHeaders(), MaxQuantConstants.
                PARAM_TAB_DELIMITER.value(), true);

        //get the MaxQuant version
        String versionParameter = parameters.get(parametersHeaders.get(ParametersHeader.VERSION));
        if (versionParameter != null && !versionParameter.isEmpty() && !version.equals(versionParameter)) {
            version = versionParameter;
        }

        //precursor mass tolerance and unit
        String precursorMassToleranceString = getMqParParamsWithRawFile().get(rawFileName).get(MqParHeader.PEPTIDE_MASS_TOLERANCE);
        searchParameters.setPrecMassTolerance(Double.parseDouble(precursorMassToleranceString));

        String massToleranceUnit = "";
        if (getMqParParamsWithRawFile().get(rawFileName).get(MqParHeader.PEPTIDE_MASS_TOLERANCE_UNIT).equalsIgnoreCase("true")) {
            massToleranceUnit = "ppm";
        } else if (getMqParParamsWithRawFile().get(rawFileName).get(MqParHeader.PEPTIDE_MASS_TOLERANCE_UNIT).equalsIgnoreCase("false")) {
            massToleranceUnit = "da";
        } else {
            massToleranceUnit = "";
        }
        searchParameters.setPrecMassToleranceUnit(MassAccuracyType.valueOf(massToleranceUnit.toUpperCase(Locale.ENGLISH)));

        //fragment mass tolerance and unit
        String fragmentMassToleranceString = getMqParParamsWithRawFile().get(rawFileName).get(MqParHeader.FRAGMENT_MASS_TOLERANCE);
        searchParameters.setFragMassTolerance(Double.parseDouble(fragmentMassToleranceString));

        searchParameters.setFragMassToleranceUnit(MassAccuracyType.valueOf(massToleranceUnit.toUpperCase(Locale.ENGLISH)));

        //enzyme
        TypedCvParam enzyme = mapEnzyme(getMqParParamsWithRawFile().get(rawFileName).get(MqParHeader.ENZYMES));
        if (enzyme != null) {
            searchParameters.setEnzyme((SearchCvParam) enzyme);
        }

        //missed cleavages
        String missedCleavages = getMqParParamsWithRawFile().get(rawFileName).get(MqParHeader.MAX_MISSED_CLEAVAGES);
        searchParameters.setNumberOfMissedCleavages(Integer.parseInt(missedCleavages));

        //upper charge
        String upperCharge = getMqParParamsWithRawFile().get(rawFileName).get(MqParHeader.MAX_CHARGE);
        searchParameters.setUpperCharge(Integer.parseInt(upperCharge));

        //add modifications
        searchParameters.getSearchParametersHasModifications().addAll(createModifications(searchParameters, rawFileName));

        //look for the given search parameter settings in the database
        searchParameters = searchAndValidationSettingsService.getSearchParameters(searchParameters);

        //set the search engine
        searchAndValidationSettings.setSearchEngine(searchAndValidationSettingsService.getSearchEngine(SearchEngineType.MAXQUANT, version));

        //set entity relations between SearchAndValidationSettings and SearchParameters
        searchAndValidationSettings.setSearchParameters(searchParameters);

        // TODO: 6/13/2016 change!
        //searchParameters.getSearchAndValidationSettingses().add(searchAndValidationSettings);
        return searchAndValidationSettings;
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
     * Get the default search type from the database and assign it to the class
     * field.
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
     * Get the spectrum parameters which have link with RAW Files (runs).
     *
     * @return copy of the spectrum parameter with raw file
     */
    public Map<String, EnumMap<MqParHeader, String>> getMqParParamsWithRawFile() {
        return mqParParamsWithRawFile;
    }

    /**
     * Get analytical runs name (experiment name) which have link with
     * analytical runs.
     *
     * @return analyticalRuns
     */
    public Map<AnalyticalRun, String> getAnalyticalRuns() {
        return analyticalRuns;
    }

    /**
     * Get isobaric label for labeled quantification
     *
     * @return isobaricLabels map
     */
    public Map<Integer, String> getIsobaricLabels() {
        return isobaricLabels;
    }

    /**
     * Get label modifications for SILAC experiments.
     *
     * @return labelMods
     */
    public Map<Integer, String> getLabelMods() {
        return labelMods;
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
                enzyme = olsService.findEnzymeByName(maxQuantEnzyme);
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

    /**
     * Create modifications for the given search parameter and the raw file that
     * linked to that parameter.
     *
     * @param searchParameters the search parameters
     * @param rawFileName      the RAW file name
     * @return list of SearchParametersHasModification object.
     * @throws ModificationMappingException
     */
    private List<SearchParametersHasModification> createModifications(SearchParameters searchParameters, String rawFileName) throws ModificationMappingException {
        List<SearchParametersHasModification> searchParametersHasModifications = new ArrayList<>();
        // find the name of fixed modification
        String fixedModifications = getMqParParamsWithRawFile().get(rawFileName).get(MqParHeader.FIXED_MODIFICATIONS);
        // find the name of variable modification
        String variableModifications = getMqParParamsWithRawFile().get(rawFileName).get(MqParHeader.VARIABLE_MODIFICATIONS);

        if (fixedModifications == null && variableModifications == null) {
            return searchParametersHasModifications;
        }

        if (fixedModifications != null && !fixedModifications.isEmpty()) {
            String[] split = fixedModifications.split(MODIFICATIONS_DELIMITER);
            for (int i = 0; i < split.length; i++) {
                SearchParametersHasModification searchParametersHasModification = createSearchParametersHasModification(searchParameters, "");

                SearchModification searchModification = utilitiesPtmSettingsMapper.mapByName(split[i].split(MODIFICATION_NAME_ONLY)[0]);

                searchParametersHasModification.setModificationType(ModificationType.FIXED);
                searchParametersHasModification.setSearchModification(searchModification);

                searchParametersHasModifications.add(searchParametersHasModification);
            }
        }

        if (variableModifications != null && !variableModifications.isEmpty()) {
            String[] split = variableModifications.split(MODIFICATIONS_DELIMITER);
            for (int i = 0; i < split.length; i++) {
                SearchParametersHasModification searchParametersHasModification = createSearchParametersHasModification(searchParameters, "");

                SearchModification searchModification = utilitiesPtmSettingsMapper.mapByName(split[i].split(MODIFICATION_NAME_ONLY)[0]);

                searchParametersHasModification.setModificationType(ModificationType.VARIABLE);
                searchParametersHasModification.setSearchModification(searchModification);

                searchParametersHasModifications.add(searchParametersHasModification);
            }
        }

        return searchParametersHasModifications;
    }

    /**
     * Create searchParametersHasModification instance
     *
     * @param searchParameters
     * @param residues
     * @return searchParametersHasModification instance
     */
    private SearchParametersHasModification createSearchParametersHasModification(SearchParameters searchParameters, String residues) {
        SearchParametersHasModification searchParametersHasModification = new SearchParametersHasModification();

        searchParametersHasModification.setSearchParameters(searchParameters);
        searchParametersHasModification.setResidues(residues);

        return searchParametersHasModification;
    }

    /**
     * Parse the mqpar.xml file and match with runs.
     *
     * @param mqParFile the mqpar.xml file path
     * @throws JDOMException in case of an problem occurring in one of the JDOM classes
     */
    void parseMqParFile(Path mqParFile) throws JDOMException {
        // create a map to hold raw files for each run (key: group index; value: raw file).
        Map<Integer, String> rawFilePath = new HashMap<>();
        //create a map to hold raw file groups for each run (key: group index; value: group number).
        Map<Integer, Integer> rawFileGroup = new HashMap<>();
        //create a map to hold enum map of spectrum parameters and their group(key: group number; value: enum map of spectrum parameters).
        Map<Integer, EnumMap<MqParHeader, String>> spectrumParamsWithGroup = new HashMap<>();
        // create a map to hold experiment names for each run (key: group index; value: experiment name).
        Map<Integer, String> experimentsName = new HashMap<>();

        Resource mqParResource = new FileSystemResource(mqParFile.toFile());
        SAXBuilder builder = new SAXBuilder();
        Document document;
        try {
            document = builder.build(mqParResource.getInputStream());

            Element root = document.getRootElement();
            // keep each raw file in a map (key: int, value: name of file).
            Element filePathsElement = getChildByName(root, FILE_PATHS);
            int counter = 0;
            for (Element filePathElement : filePathsElement.getChildren()) {
                String rawFile = filePathElement.getContent().get(0).getValue();
                String[] split = rawFile.split("\\\\");
                rawFile = org.apache.commons.lang3.StringUtils.substringBefore(split[split.length - 1], ".raw");
                rawFilePath.put(counter, rawFile);
                counter++;
            }

            Element analyticalRunNamesElement = getChildByName(root, EXPERIMENTS);
            counter = 0;
            for (Element analyticalRunNameElement : analyticalRunNamesElement.getChildren()) {
                if (!analyticalRunNameElement.getContent().isEmpty()) {
                    experimentsName.put(counter, analyticalRunNameElement.getContent().get(0).getValue());
                    counter++;
                } else {
                    throw new IllegalStateException("Experiment name in mqpar file is empty.");
                }
            }

            // keep each raw file group number in a map (key: int; value: group number).
            Element rawFileGroupsElement = getChildByName(root, PARAM_GROUP_INDICES);
            counter = 0;
            for (Element rawFileGroupElement : rawFileGroupsElement.getChildren()) {
                int groupNo = Integer.parseInt(rawFileGroupElement.getContent().get(0).getValue());
                rawFileGroup.put(counter, groupNo);
                counter++;
            }

            // keep each search parameters in a map (key: group number; value = maxQuantSpectrumParameterHeaders)
            Element parameterGroupsElement = getChildByName(root, PARAMETER_GROUPS);
            counter = 0;
            for (Element parameterGroupElement : parameterGroupsElement.getChildren()) {
                //create enumMap for mqpar.xml parameters (key: MqParHeader enum; value: parameter value).
                EnumMap<MqParHeader, String> mqParParameters = new EnumMap<>(MqParHeader.class);
                //iterate over the mandatory headers
                for (MaxQuantHeader mqParHeader : mqParHeaders.getMandatoryHeaders()) {
                    Optional<String> foundHeader = mqParHeader.getValues().stream().filter(value -> parameterGroupElement.getChild(value) != null).findFirst();
                    if (foundHeader.isPresent()) {
                        mqParHeader.setParsedValue(mqParHeader.getValues().indexOf(foundHeader.get()));
                        //if the Element has children (enzymes, fixed/variable modifications), concatenate them.
                        Element param = parameterGroupElement.getChild(foundHeader.get());
                        if (param.getChildren().isEmpty()) {
                            mqParParameters.put(MqParHeader.valueOf(mqParHeader.getName()), param.getValue());
                        } else {
                            //join the enzymes
                            String concatenation = getChildByName(parameterGroupElement, foundHeader.get()).getChildren().stream().map(enzyme ->
                                    enzyme.getContent().get(0).getValue()).collect(Collectors.joining(PARAMETER_DELIMITER));
                            mqParParameters.put(MqParHeader.valueOf(mqParHeader.getName()), concatenation);
                        }
                    }
                }

                spectrumParamsWithGroup.put(counter, mqParParameters);
                counter++;

                Element isobaricLabelsElement = getChildByName(parameterGroupElement, "isobaricLabels");
                int labelCounter = 0;
                for (Element isobaricLabelElement : isobaricLabelsElement.getChildren()) {
                    if (!isobaricLabelElement.getContent().isEmpty()) {
                        isobaricLabels.put(labelCounter, isobaricLabelElement.getContent().get(0).getValue());
                    } else {
                        isobaricLabels.put(labelCounter, null);
                    }

                    labelCounter++;
                }

                Element labelModsElement = getChildByName(parameterGroupElement, "labelMods");
                labelCounter = 0;
                for (Element labelModElement : labelModsElement.getChildren()) {
                    if (!labelModElement.getContent().isEmpty()) {
                        labelMods.put(labelCounter, labelModElement.getContent().get(0).getValue());
                    } else {
                        labelMods.put(labelCounter, null);
                    }

                    labelCounter++;
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        // match all maps and put them in mqParParamsWithRawFile map
        // also put raw file name and experiment name in analyticalRuns
        rawFilePath.entrySet().stream().forEach(entry -> {
            // find group number of each file name and using group number find the enumMap of spectrum parameters. Then put them in the map.
            mqParParamsWithRawFile.put(entry.getValue(), spectrumParamsWithGroup.get(rawFileGroup.get(entry.getKey())));
            // fill analyticalRuns
            AnalyticalRun analyticalRun = new AnalyticalRun();
            analyticalRun.setName(entry.getValue());
            analyticalRuns.put(analyticalRun, experimentsName.get(entry.getKey()));
        });
    }

    /**
     * Find child element by name, case insensitive. Returns null if nothing was found.
     *
     * @param parent    the parent element
     * @param childName the child name
     * @return the found child element
     */
    private Element getChildByName(Element parent, String childName) {
        Element element = parent.getChild(childName);
        if (element == null) {
            for (Element e : parent.getChildren()) {
                String tagName = e.getName();
                if (tagName.equalsIgnoreCase(childName)) {
                    return e;
                }
            }
        }
        return element;
    }
}
